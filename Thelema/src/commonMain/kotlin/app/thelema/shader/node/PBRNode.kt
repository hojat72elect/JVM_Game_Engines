/*
 * Copyright 2020-2021 Anton Trushkov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.thelema.shader.node

import app.thelema.app.APP
import app.thelema.g3d.IUniformArgs
import app.thelema.g3d.cam.ActiveCamera
import app.thelema.g3d.light.DirectionalLight
import app.thelema.g3d.light.ILight
import app.thelema.g3d.light.LightType
import app.thelema.math.MATH
import app.thelema.shader.Shader
import kotlin.random.Random

// TODO https://github.com/KhronosGroup/glTF-Sample-Viewer/blob/master/src/shaders/pbr.frag

/** @author zeganstyl */
class PBRNode(): ShaderNode() {
    constructor(block: PBRNode.() -> Unit): this() { block(this) }

    override val componentName: String
        get() = "PBRNode"

    var worldPosition by input(GLSLNode.vertex.position)
    var baseColor by input(GLSL.oneFloat)
    var alpha by inputOrNull()
    var normal by input(GLSLNode.vertex.normal)
    var occlusion by inputOrNull()
    var roughness by inputOrNull()
    var metallic by inputOrNull()
    var emissive by inputOrNull()

    val result: IShaderData = output(GLSLVec4("result"))

    var maxNumDirectionLights: Int = 1
    var shadowCascadesNum = DirectionalLight.shadowCascadesNumDefault
    var maxLights: Int = 50
        set(value) {
            if (value < 1) throw IllegalStateException("PBRNode: maxLights can't be < 1")
            field = value
        }

    var receiveShadows: Boolean = false

    var iblEnabled = false
    var iblMaxMipLevels = 0

    var softShadowSamples = 16

    private val brdfLUTName: String
        get() = "u_GGXLUT"

    private val irradianceMapName: String
        get() = "u_LambertianEnvSampler"

    private val prefilterMapName: String
        get() = "u_GGXEnvSampler"

    private var lightsNum = 0
    private var dirLightIndex = 0

    private val camPosUniform = "camera_position${GLSL.id()}"

    init {
        worldPosition = GLSLNode.vertex.position
    }

    override fun shaderCompiled() {
        super.shaderCompiled()

        for (i in 0 until softShadowSamples) {
            val u = Random.nextFloat()
            val v = Random.nextFloat()
            val x = MATH.sqrt(v) * MATH.cos(MATH.PI2 * u)
            val y = MATH.sqrt(v) * MATH.sin(MATH.PI2 * u)
            shader.set("offsets[$i]", x, y)
        }
    }

    override fun bind(uniforms: IUniformArgs) {
        super.bind(uniforms)

        dirLightIndex = 0
        lightsNum = 0

        val scene = uniforms.scene
        if (scene != null) {
            val lights = scene.lights
            for (i in lights.indices) {
                setLight(lights[i])
            }

            if (APP.isEditorMode && dirLightIndex == 0) {
                defaultLight.direction.set(ActiveCamera.node.worldPosition).scl(-1f).nor()
                setLight(defaultLight)
            }
        }

        //shader["lightsNum"] = lightsNum
        //shader["uDirLightsNum"] = dirLightIndex
        shader[camPosUniform] = ActiveCamera.node.worldPosition

        val world = scene?.world
        if (world != null) {
            if (iblEnabled) {
                shader[brdfLUTName] = shader.getNextTextureUnit().also { world.brdfLUTMap?.bind(it) }
                shader[irradianceMapName] = shader.getNextTextureUnit().also { world.environmentIrradianceMap?.bind(it) }
                shader[prefilterMapName] = shader.getNextTextureUnit().also { world.environmentPrefilterMap?.bind(it) }
            }

            shader["uAmbientColor"] = world.ambientColor
        } else {
            shader.set("uAmbientColor", 0.01f, 0.01f, 0.01f)
        }
    }

    private fun setLight(light: ILight) {
        if (light.isLightEnabled) {
            lightsNum++

            when (light.lightType) {
                LightType.Directional -> {
                    light as DirectionalLight
                    setDirLightShadow(light, dirLightIndex)
                    dirLightIndex++
                }
            }
        }
    }

    private fun setDirLightShadow(light: DirectionalLight, index: Int) {
        if (light.isShadowEnabled) {
            val lightCascadesStartIndex = index * light.shadowCascadesNum
            shader["invTexSize"] = light.shadowSoftness / light.shadowMapWidth
            for (j in 0 until light.shadowCascadesNum) {
                val cascadeIndex = lightCascadesStartIndex + j
                shader["uDirLightCascadeEnd[$cascadeIndex]"] = light.shadowCascadeEnd[j]
                shader["uDirLightViewProj[$cascadeIndex]"] = light.viewProjectionMatrices[j]

                val unit = shader.getNextTextureUnit()
                shader["uDirLightShadowMap[$cascadeIndex]"] = unit
                light.shadowMaps[j].also { tex ->
                    shader.set("uDirLightShadowMapSize[$cascadeIndex]", tex.width.toFloat(), tex.height.toFloat())
                    tex.bind(unit)
                }
            }
        }
    }

    override fun executionFrag(out: StringBuilder) {
        if (result.isUsed) {
            val alpha = alpha
            val baseColor = baseColor
            val color = if (alpha != null) {
                "vec4(${baseColor.asVec3()}, ${alpha.asFloat()})"
            } else {
                baseColor.asVec4()
            }
            out.append("${result.ref} = pbrMain(${worldPosition.asVec3()}, $color, ${normal.asVec3()}, ${(occlusion ?: GLSL.oneFloat).asFloat()}, ${(roughness ?: GLSL.oneFloat).asFloat()}, ${(metallic ?: GLSL.zeroFloat).asFloat()}, ${(emissive ?: GLSL.zeroFloat).asVec3()});\n")
        }
    }

    override fun declarationFrag(out: StringBuilder) {
        if (result.isUsed) {
            out.append("uniform vec3 $camPosUniform;\n")

            if (receiveShadows) {
                val num = maxNumDirectionLights * shadowCascadesNum
                out.append("uniform sampler2D uDirLightShadowMap[$num];\n")
                out.append("uniform vec2 uDirLightShadowMapSize[$num];\n")
                out.append("uniform float uDirLightCascadeEnd[$num];\n")
                out.append("in vec4 vDirLightClipSpacePos[$num];\n")
            }

            if (iblEnabled) {
                out.append("uniform sampler2D $brdfLUTName;\n")
                out.append("uniform samplerCube $irradianceMapName;\n")
                out.append("uniform samplerCube $prefilterMapName;\n")
            }

            out.append("${result.typedRef} = ${result.typeStr}(0.0);\n")
            out.append(pbrCode())
        }
    }

    override fun declarationVert(out: StringBuilder) {
        if (result.isUsed) {
            if (receiveShadows) {
                val num = maxNumDirectionLights * shadowCascadesNum
                out.append("uniform mat4 uDirLightViewProj[$num];\n")
                out.append("out vec4 vDirLightClipSpacePos[$num];\n")
            }
        }
    }

    override fun executionVert(out: StringBuilder) {
        if (result.isUsed) {
            if (receiveShadows) {
                val num = maxNumDirectionLights * shadowCascadesNum
                out.append("for (int i = 0; i < $num; i++) {\n")
                out.append("vDirLightClipSpacePos[i] = uDirLightViewProj[i] * ${worldPosition.asVec4()};\n")
                out.append("}\n")
            }
        }
    }

/* GPU Gems 2
 #define SAMPLES_COUNT 32
 #define INV_SAMPLES_COUNT (1.0f / SAMPLES_COUNT)
 uniform sampler2D decal;  // decal texture
 uniform sampler2D spot;   // projected spotlight image
 uniform sampler2DShadow shadowMap;  // shadow map
 uniform float fwidth;
 uniform vec2 offsets[SAMPLES_COUNT];    // these are passed down from vertex shader
 varying vec4 shadowMapPos;
 varying vec3 normal;
 varying vec2 texCoord;
 varying vec3 lightVec;
 varying vec3 view;
 void main(void)  {
     float shadow = 0;
     float fsize = shadowMapPos.w * fwidth;
     vec4 smCoord = shadowMapPos;
     for (int i = 0; i<SAMPLES_COUNT; i++) {
         smCoord.xy = offsets[i] * fsize + shadowMapPos;
         shadow += texture2DProj(shadowMap, smCoord) * INV_SAMPLES_COUNT;
     }
     vec3 N = normalize(normal);
     vec3 L = normalize(lightVec);
     vec3 V = normalize(view);
     vec3 R = reflect(-V, N);      // calculate diffuse dot product
     float NdotL = max(dot(N, L), 0);      // modulate lighting with the computed shadow value
     vec3 color = texture2D(decal, texCoord).xyz;
     gl_FragColor.xyz = (color * NdotL + pow(max(dot(R, L), 0), 64)) * shadow * texture2DProj(spot, shadowMapPos) + color * 0.1;
 }
 */

    private fun cascadedShadowDecl(): String = if (receiveShadows) """
${
        if (softShadowSamples > 0) {
            """
#define SAMPLES_COUNT $softShadowSamples
#define INV_SAMPLES_COUNT (1.0f / SAMPLES_COUNT)
uniform float invTexSize;
uniform vec2 offsets[SAMPLES_COUNT];
"""
        } else ""
    }

float shadowFactor(float currentDepth, float closestDepth) {
    float bias = 0.001;
    if (currentDepth - bias > closestDepth)
        return 1.0;
    else
        return 0.0;
}

// improved bilinear interpolated texture fetch
float textureGoodF( sampler2D sam, vec2 uv, vec2 res, float currentDepth ) {
    vec2 st = uv * res;

    ivec2 iuv = ivec2(floor( st ));
    vec2 fuv = fract( st );

    float a = shadowFactor(currentDepth, texelFetch( sam, iuv, 0 ).x);
    float b = shadowFactor(currentDepth, texelFetch( sam, iuv + ivec2(1, 0), 0 ).x);
    float c = shadowFactor(currentDepth, texelFetch( sam, iuv + ivec2(0, 1), 0 ).x);
    float d = shadowFactor(currentDepth, texelFetch( sam, iuv + ivec2(1, 1), 0 ).x);

    return mix( mix( a, b, fuv.x), mix( c, d, fuv.x), fuv.y );
}

float getShadow(const int shadowIndex) {
    vec4 lightSpacePos = vDirLightClipSpacePos[shadowIndex];
    vec3 projCoords = (lightSpacePos.xyz / lightSpacePos.w) * 0.5 + 0.5;
    float shadow = 0.0;
    vec2 res = uDirLightShadowMapSize[shadowIndex];
${
        if (softShadowSamples > 0) {
            """
        float fsize = lightSpacePos.w * invTexSize;
        vec3 smCoord = projCoords;
        for (int s = 0; s<SAMPLES_COUNT; s++) {
            smCoord.xy = offsets[s] * fsize + projCoords.xy;
            
            shadow += textureGoodF(uDirLightShadowMap[shadowIndex], smCoord.xy, res, projCoords.z);
        }
        
        shadow *= INV_SAMPLES_COUNT;
    """
        } else {
            """
        shadow += textureGoodF(uDirLightShadowMap[shadowIndex], projCoords.xy, res, projCoords.z);
    """
        }
}
    return shadow;
}
""" else ""

    private fun cascadedShadowsExe(
        visualizeCascadeFields: Boolean = false
    ): String = if (receiveShadows) """
// cascaded shadow
${if (visualizeCascadeFields) "vec3 cascadeColor = vec3(0.0);" else ""}
int lightIndex = dirLightIndex * $shadowCascadesNum;
for (int j = 0; j < ${shadowCascadesNum * maxNumDirectionLights}; j++) {
    // this index comparison is for webgl
    if (${Shader.CLIP_SPACE_POS}.z < uDirLightCascadeEnd[j] && j >= lightIndex && j < lightIndex + $shadowCascadesNum) {
        ${if (visualizeCascadeFields) """
        if (j == 0) {
            cascadeColor = vec3(1.0, 0.0, 0.0);
        } else if (j == 1) {
            cascadeColor = vec3(0.0, 1.0, 0.0);
        } else if (j == 2) {
            cascadeColor = vec3(0.0, 0.0, 1.0);       
        } else if (j == 3) {
            cascadeColor = vec3(1.0, 1.0, 0.0);       
        }
        """ else ""}
        
        //float closestDepth = texture2D(uDirLightShadowMap[j], projCoords.xy).x;
        shadow -= getShadow(j);
        
        ${if (visualizeCascadeFields) "shadowColor += cascadeColor * 0.5;" else ""}
        
        break;
    }
    
    shadow = clamp(shadow, 0.0, 1.0);
}
""" else ""

    private fun pbrCode(): String {
        return """

${cascadedShadowDecl()}

//uniform int uDirLightsNum;
//uniform int lightsNum;

uniform vec3 uAmbientColor;

// <material_info.glsl> ================================================================================================
struct MaterialInfo
{
    float ior;
    float perceptualRoughness;      // roughness value, as authored by the model creator (input to shader)
    vec3 f0;                        // full reflectance color (n incidence angle)

    float alphaRoughness;           // roughness mapped to a more linear change in the roughness (proposed by [2])
    vec3 c_diff;

    vec3 f90;                       // reflectance color at grazing angle
    float metallic;

    vec3 baseColor;

    float sheenRoughnessFactor;
    vec3 sheenColorFactor;

    vec3 clearcoatF0;
    vec3 clearcoatF90;
    float clearcoatFactor;
    vec3 clearcoatNormal;
    float clearcoatRoughness;

    // KHR_materials_specular 
    float specularWeight; // product of specularFactor and specularTexture.a

    float transmissionFactor;

    float thickness;
    vec3 attenuationColor;
    float attenuationDistance;
};
// </material_info.glsl> ===============================================================================================

// <functions.glsl> ====================================================================================================
const float M_PI = 3.141592653589793;


struct NormalInfo {
    vec3 ng;   // Geometric normal
    vec3 n;    // Pertubed normal
    vec3 t;    // Pertubed tangent
    vec3 b;    // Pertubed bitangent
};


float clampedDot(vec3 x, vec3 y)
{
    return clamp(dot(x, y), 0.0, 1.0);
}


float max3(vec3 v)
{
    return max(max(v.x, v.y), v.z);
}


float applyIorToRoughness(float roughness, float ior)
{
    // Scale roughness with IOR so that an IOR of 1.0 results in no microfacet refraction and
    // an IOR of 1.5 results in the default amount of microfacet refraction.
    return roughness * clamp(ior * 2.0 - 2.0, 0.0, 1.0);
}
// </functions.glsl> ===================================================================================================

// <brdf.glsl> =========================================================================================================
//
// Fresnel
//
// http://graphicrants.blogspot.com/2013/08/specular-brdf-reference.html
// https://github.com/wdas/brdf/tree/master/src/brdfs
// https://google.github.io/filament/Filament.md.html
//

// The following equation models the Fresnel reflectance term of the spec equation (aka F())
// Implementation of fresnel from [4], Equation 15
vec3 F_Schlick(vec3 f0, vec3 f90, float VdotH)
{
    return f0 + (f90 - f0) * pow(clamp(1.0 - VdotH, 0.0, 1.0), 5.0);
}


// Smith Joint GGX
// Note: Vis = G / (4 * NdotL * NdotV)
// see Eric Heitz. 2014. Understanding the Masking-Shadowing Function in Microfacet-Based BRDFs. Journal of Computer Graphics Techniques, 3
// see Real-Time Rendering. Page 331 to 336.
// see https://google.github.io/filament/Filament.md.html#materialsystem/specularbrdf/geometricshadowing(specularg)
float V_GGX(float NdotL, float NdotV, float alphaRoughness)
{
    float alphaRoughnessSq = alphaRoughness * alphaRoughness;

    float GGXV = NdotL * sqrt(NdotV * NdotV * (1.0 - alphaRoughnessSq) + alphaRoughnessSq);
    float GGXL = NdotV * sqrt(NdotL * NdotL * (1.0 - alphaRoughnessSq) + alphaRoughnessSq);

    float GGX = GGXV + GGXL;
    if (GGX > 0.0)
    {
        return 0.5 / GGX;
    }
    return 0.0;
}


// The following equation(s) model the distribution of microfacet normals across the area being drawn (aka D())
// Implementation from "Average Irregularity Representation of a Roughened Surface for Ray Reflection" by T. S. Trowbridge, and K. P. Reitz
// Follows the distribution function recommended in the SIGGRAPH 2013 course notes from EPIC Games [1], Equation 3.
float D_GGX(float NdotH, float alphaRoughness)
{
    float alphaRoughnessSq = alphaRoughness * alphaRoughness;
    float f = (NdotH * NdotH) * (alphaRoughnessSq - 1.0) + 1.0;
    return alphaRoughnessSq / (M_PI * f * f);
}


float lambdaSheenNumericHelper(float x, float alphaG)
{
    float oneMinusAlphaSq = (1.0 - alphaG) * (1.0 - alphaG);
    float a = mix(21.5473, 25.3245, oneMinusAlphaSq);
    float b = mix(3.82987, 3.32435, oneMinusAlphaSq);
    float c = mix(0.19823, 0.16801, oneMinusAlphaSq);
    float d = mix(-1.97760, -1.27393, oneMinusAlphaSq);
    float e = mix(-4.32054, -4.85967, oneMinusAlphaSq);
    return a / (1.0 + b * pow(x, c)) + d * x + e;
}


float lambdaSheen(float cosTheta, float alphaG)
{
    if (abs(cosTheta) < 0.5)
    {
        return exp(lambdaSheenNumericHelper(cosTheta, alphaG));
    }
    else
    {
        return exp(2.0 * lambdaSheenNumericHelper(0.5, alphaG) - lambdaSheenNumericHelper(1.0 - cosTheta, alphaG));
    }
}


float V_Sheen(float NdotL, float NdotV, float sheenRoughness)
{
    sheenRoughness = max(sheenRoughness, 0.000001); //clamp (0,1]
    float alphaG = sheenRoughness * sheenRoughness;

    return clamp(1.0 / ((1.0 + lambdaSheen(NdotV, alphaG) + lambdaSheen(NdotL, alphaG)) *
        (4.0 * NdotV * NdotL)), 0.0, 1.0);
}


//Sheen implementation-------------------------------------------------------------------------------------
// See  https://github.com/sebavan/glTF/tree/KHR_materials_sheen/extensions/2.0/Khronos/KHR_materials_sheen

// Estevez and Kulla http://www.aconty.com/pdf/s2017_pbs_imageworks_sheen.pdf
float D_Charlie(float sheenRoughness, float NdotH)
{
    sheenRoughness = max(sheenRoughness, 0.000001); //clamp (0,1]
    float alphaG = sheenRoughness * sheenRoughness;
    float invR = 1.0 / alphaG;
    float cos2h = NdotH * NdotH;
    float sin2h = 1.0 - cos2h;
    return (2.0 + invR) * pow(sin2h, invR * 0.5) / (2.0 * M_PI);
}


//https://github.com/KhronosGroup/glTF/tree/master/specification/2.0#acknowledgments AppendixB
vec3 BRDF_lambertian(vec3 f0, vec3 f90, vec3 diffuseColor, float specularWeight, float VdotH)
{
    // see https://seblagarde.wordpress.com/2012/01/08/pi-or-not-to-pi-in-game-lighting-equation/
    return (1.0 - specularWeight * F_Schlick(f0, f90, VdotH)) * (diffuseColor / M_PI);
}


//  https://github.com/KhronosGroup/glTF/tree/master/specification/2.0#acknowledgments AppendixB
vec3 BRDF_specularGGX(vec3 f0, vec3 f90, float alphaRoughness, float specularWeight, float VdotH, float NdotL, float NdotV, float NdotH)
{
    vec3 F = F_Schlick(f0, f90, VdotH);
    float Vis = V_GGX(NdotL, NdotV, alphaRoughness);
    float D = D_GGX(NdotH, alphaRoughness);

    return specularWeight * F * Vis * D;
}


// f_sheen
vec3 BRDF_specularSheen(vec3 sheenColor, float sheenRoughness, float NdotL, float NdotV, float NdotH)
{
    float sheenDistribution = D_Charlie(sheenRoughness, NdotH);
    float sheenVisibility = V_Sheen(NdotL, NdotV, sheenRoughness);
    return sheenColor * sheenDistribution * sheenVisibility;
}
// </brdf.glsl> ========================================================================================================

// <punctual.glsl> =====================================================================================================
// KHR_lights_punctual extension.
// see https://github.com/KhronosGroup/glTF/tree/master/extensions/2.0/Khronos/KHR_lights_punctual


// https://github.com/KhronosGroup/glTF/blob/master/extensions/2.0/Khronos/KHR_lights_punctual/README.md#range-property
float getRangeAttenuation(float range, float distance)
{
    if (range <= 0.0)
    {
        // negative range means unlimited
        return 1.0 / pow(distance, 2.0);
    }
    return max(min(1.0 - pow(distance / range, 4.0), 1.0), 0.0) / pow(distance, 2.0);
}


// https://github.com/KhronosGroup/glTF/blob/master/extensions/2.0/Khronos/KHR_lights_punctual/README.md#inner-and-outer-cone-angles
float getSpotAttenuation(vec3 pointToLight, vec3 spotDirection, float outerConeCos, float innerConeCos)
{
    float actualCos = dot(normalize(spotDirection), normalize(-pointToLight));
    if (actualCos > outerConeCos)
    {
        if (actualCos < innerConeCos)
        {
            return smoothstep(outerConeCos, innerConeCos, actualCos);
        }
        return 1.0;
    }
    return 0.0;
}


vec3 getLighIntensity(Light light, vec3 pointToLight)
{
    float rangeAttenuation = 1.0;
    float spotAttenuation = 1.0;

    if (light.type != LightType_Directional)
    {
        rangeAttenuation = getRangeAttenuation(light.range, length(pointToLight));
    }
    if (light.type == LightType_Spot)
    {
        spotAttenuation = getSpotAttenuation(pointToLight, light.direction, light.outerConeCos, light.innerConeCos);
    }

    return rangeAttenuation * spotAttenuation * light.intensity * light.color;
}


vec3 getPunctualRadianceTransmission(vec3 normal, vec3 view, vec3 pointToLight, float alphaRoughness,
    vec3 f0, vec3 f90, vec3 baseColor, float ior)
{
    float transmissionRougness = applyIorToRoughness(alphaRoughness, ior);

    vec3 n = normalize(normal);           // Outward direction of surface point
    vec3 v = normalize(view);             // Direction from surface point to view
    vec3 l = normalize(pointToLight);
    vec3 l_mirror = normalize(l + 2.0*n*dot(-l, n));     // Mirror light reflection vector on surface
    vec3 h = normalize(l_mirror + v);            // Halfway vector between transmission light vector and v

    float D = D_GGX(clamp(dot(n, h), 0.0, 1.0), transmissionRougness);
    vec3 F = F_Schlick(f0, f90, clamp(dot(v, h), 0.0, 1.0));
    float Vis = V_GGX(clamp(dot(n, l_mirror), 0.0, 1.0), clamp(dot(n, v), 0.0, 1.0), transmissionRougness);

    // Transmission BTDF
    return (1.0 - F) * baseColor * D * Vis;
}


vec3 getPunctualRadianceClearCoat(vec3 clearcoatNormal, vec3 v, vec3 l, vec3 h, float VdotH, vec3 f0, vec3 f90, float clearcoatRoughness)
{
    float NdotL = clampedDot(clearcoatNormal, l);
    float NdotV = clampedDot(clearcoatNormal, v);
    float NdotH = clampedDot(clearcoatNormal, h);
    return NdotL * BRDF_specularGGX(f0, f90, clearcoatRoughness * clearcoatRoughness, 1.0, VdotH, NdotL, NdotV, NdotH);
}


vec3 getPunctualRadianceSheen(vec3 sheenColor, float sheenRoughness, float NdotL, float NdotV, float NdotH)
{
    return NdotL * BRDF_specularSheen(sheenColor, sheenRoughness, NdotL, NdotV, NdotH);
}


// Compute attenuated light as it travels through a volume.
vec3 applyVolumeAttenuation(vec3 radiance, float transmissionDistance, vec3 attenuationColor, float attenuationDistance)
{
    if (attenuationDistance == 0.0)
    {
        // Attenuation distance is +∞ (which we indicate by zero), i.e. the transmitted color is not attenuated at all.
        return radiance;
    }
    else
    {
        // Compute light attenuation using Beer's law.
        vec3 attenuationCoefficient = -log(attenuationColor) / attenuationDistance;
        vec3 transmittance = exp(-attenuationCoefficient * transmissionDistance); // Beer's law
        return transmittance * radiance;
    }
}


vec3 getVolumeTransmissionRay(vec3 n, vec3 v, float thickness, float ior, mat4 modelMatrix)
{
    // Direction of refracted light.
    vec3 refractionVector = refract(-v, normalize(n), 1.0 / ior);

    // Compute rotation-independant scaling of the model matrix.
    vec3 modelScale;
    modelScale.x = length(vec3(modelMatrix[0].xyz));
    modelScale.y = length(vec3(modelMatrix[1].xyz));
    modelScale.z = length(vec3(modelMatrix[2].xyz));

    // The thickness is specified in local space.
    return normalize(refractionVector) * thickness * modelScale;
}
// </punctual.glsl> ====================================================================================================

// <ibl.glsl> ==========================================================================================================
${
            if (iblEnabled) {
                """
vec3 getIBLRadianceGGX(vec3 n, vec3 v, float roughness, vec3 F0, float specularWeight)
{
    float NdotV = clampedDot(n, v);
    float lod = roughness * ${iblMaxMipLevels - 1}.0;
    vec3 reflection = normalize(reflect(-v, n));

    vec2 brdfSamplePoint = clamp(vec2(NdotV, roughness), vec2(0.0, 0.0), vec2(1.0, 1.0));
    vec2 f_ab = texture2D(u_GGXLUT, brdfSamplePoint).rg;
    
    vec4 specularSample = textureLod(u_GGXEnvSampler, reflection, lod);

    vec3 specularLight = specularSample.rgb;

    // see https://bruop.github.io/ibl/#single_scattering_results at Single Scattering Results
    // Roughness dependent fresnel, from Fdez-Aguera
    vec3 Fr = max(vec3(1.0 - roughness), F0) - F0;
    vec3 k_S = F0 + Fr * pow(1.0 - NdotV, 5.0);
    vec3 FssEss = k_S * f_ab.x + f_ab.y;

    return specularWeight * specularLight * FssEss;
}

// specularWeight is introduced with KHR_materials_specular
vec3 getIBLRadianceLambertian(vec3 n, vec3 v, float roughness, vec3 diffuseColor, vec3 F0, float specularWeight)
{
    float NdotV = clampedDot(n, v);
    vec2 brdfSamplePoint = clamp(vec2(NdotV, roughness), vec2(0.0, 0.0), vec2(1.0, 1.0));
    vec2 f_ab = texture2D(u_GGXLUT, brdfSamplePoint).rg;

    vec3 irradiance = textureCube(u_LambertianEnvSampler, n).rgb;

    // see https://bruop.github.io/ibl/#single_scattering_results at Single Scattering Results
    // Roughness dependent fresnel, from Fdez-Aguera

    vec3 Fr = max(vec3(1.0 - roughness), F0) - F0;
    vec3 k_S = F0 + Fr * pow(1.0 - NdotV, 5.0);
    vec3 FssEss = specularWeight * k_S * f_ab.x + f_ab.y; // <--- GGX / specular light contribution (scale it down if the specularWeight is low)

    // Multiple scattering, from Fdez-Aguera
    float Ems = (1.0 - (f_ab.x + f_ab.y));
    vec3 F_avg = specularWeight * (F0 + (1.0 - F0) / 21.0);
    vec3 FmsEms = Ems * FssEss * F_avg / (1.0 - F_avg * Ems);
    vec3 k_D = diffuseColor * (1.0 - FssEss + FmsEms); // we use +FmsEms as indicated by the formula in the blog post (might be a typo in the implementation)

    return (FmsEms + k_D) * irradiance;
}
"""
            } else ""
        }

// </ibl.glsl> =========================================================================================================



vec3 fresnelSchlickRoughness(float cosTheta, vec3 F0, float roughness)
{
    return F0 + (max(vec3(1.0 - roughness), F0) - F0) * pow(max(1.0 - cosTheta, 0.0), 5.0);
}  



/*
worldPosition - vertex position
viewToPosition = normalize(cameraPosition - worldPosition)
*/
vec4 pbrMain(vec3 worldPosition, vec4 baseColor, vec3 Normal, float occlusion, float perceptualRoughness, float metallic, vec3 emissive) {
    vec3 viewToPosition = normalize($camPosUniform - worldPosition);

    vec3 n = Normal;
    vec3 v = viewToPosition;

    MaterialInfo materialInfo;
    materialInfo.baseColor = baseColor.rgb;
    materialInfo.metallic = metallic;
    materialInfo.perceptualRoughness = perceptualRoughness;
    
    // The default index of refraction of 1.5 yields a dielectric normal incidence reflectance of 0.04.
    materialInfo.ior = 1.5;
    materialInfo.f0 = vec3(0.04);
    materialInfo.specularWeight = 1.0;
    
    materialInfo.c_diff = mix(materialInfo.baseColor.rgb * (vec3(1.0) - materialInfo.f0),  vec3(0), materialInfo.metallic);
    materialInfo.f0 = mix(materialInfo.f0, materialInfo.baseColor.rgb, materialInfo.metallic);
    
    // ...
    
    materialInfo.perceptualRoughness = clamp(materialInfo.perceptualRoughness, 0.0, 1.0);
    materialInfo.metallic = clamp(materialInfo.metallic, 0.0, 1.0);
    
    // Roughness is authored as perceptual roughness; as is convention,
    // convert to material roughness by squaring the perceptual roughness.
    materialInfo.alphaRoughness = materialInfo.perceptualRoughness * materialInfo.perceptualRoughness;
    
    // Compute reflectance.
    float reflectance = max(max(materialInfo.f0.r, materialInfo.f0.g), materialInfo.f0.b);

    // Anything less than 2% is physically impossible and is instead considered to be shadowing. Compare to "Real-Time-Rendering" 4th editon on page 325.
    materialInfo.f90 = vec3(1.0);

    // LIGHTING
    vec3 f_specular = vec3(0.0);
    vec3 f_diffuse = vec3(0.0);
    vec3 f_emissive = vec3(0.0);
    vec3 f_clearcoat = vec3(0.0);
    vec3 f_sheen = vec3(0.0);
    vec3 f_transmission = vec3(0.0);

    float albedoSheenScaling = 1.0;

    ${
            if (iblEnabled) {
                """
f_specular += getIBLRadianceGGX(n, v, materialInfo.perceptualRoughness, materialInfo.f0, materialInfo.specularWeight);
f_diffuse += getIBLRadianceLambertian(n, v, materialInfo.perceptualRoughness, materialInfo.c_diff, materialInfo.f0, materialInfo.specularWeight);
"""
            } else ""
        }
    
    // ...
    
    float ao = clamp(occlusion, 0.0, 1.0);
    f_diffuse *= ao;
    // apply ambient occlusion to all lighting that is not punctual
    f_specular *= ao;
    f_sheen *= ao;
    f_clearcoat *= ao;

    int dirLightIndex = 0;
    // https://stackoverflow.com/questions/38986208/webgl-loop-index-cannot-be-compared-with-non-constant-expression
    for (int i=0; i<$maxLights; i++) {
        if (i >= LightsNum) { break; }
        
        float shadow = 1.0;
        
        Light light = Lights[i];
        vec3 pointToLight;
        if (light.type != LightType_Directional) {
            pointToLight = light.position - worldPosition;
        } else {
            pointToLight = -light.direction;
            if (light.shadowEnabled) {
                ${cascadedShadowsExe()}
            }
            dirLightIndex++;
        }
        
        // BSTF
        vec3 l = normalize(pointToLight);   // Direction from surface point to light
        vec3 h = normalize(l + v);          // Direction of the vector between l and v, called halfway vector
        float NdotL = clampedDot(n, l);
        float NdotV = clampedDot(n, v);
        float NdotH = clampedDot(n, h);
        float LdotH = clampedDot(l, h);
        float VdotH = clampedDot(v, h);
        
        if (NdotL > 0.0 || NdotV > 0.0) {
            // Calculation of analytical light
            // https://github.com/KhronosGroup/glTF/tree/master/specification/2.0#acknowledgments AppendixB
            vec3 intensity = getLighIntensity(light, pointToLight) * shadow;
            f_diffuse += intensity * NdotL *  BRDF_lambertian(materialInfo.f0, materialInfo.f90, materialInfo.c_diff, materialInfo.specularWeight, VdotH);
            f_specular += intensity * NdotL * BRDF_specularGGX(materialInfo.f0, materialInfo.f90, materialInfo.alphaRoughness, materialInfo.specularWeight, VdotH, NdotL, NdotV, NdotH);
        }
        
        // ...
    }
    
    if (LightsNum == 0) f_diffuse += materialInfo.c_diff;
    
    f_emissive = emissive;
    
    vec3 color = vec3(0.0);

//    vec3 ambient = uAmbientColor * baseColor.rgb * occlusion;
//    color += ambient;

    // Layer blending

    float clearcoatFactor = 0.0;
    vec3 clearcoatFresnel = vec3(0);
    
    #ifdef MATERIAL_TRANSMISSION
        vec3 diffuse = mix(f_diffuse, f_transmission, materialInfo.transmissionFactor);
    #else
        vec3 diffuse = f_diffuse;
    #endif

    color = f_emissive + diffuse + f_specular;
    color = f_sheen + color * albedoSheenScaling;
    color = color * (1.0 - clearcoatFactor * clearcoatFresnel) + f_clearcoat;

    // regular shading
    return vec4(color, baseColor.a);
}
"""
    }

    companion object {
        val defaultLight = DirectionalLight()
    }
}
