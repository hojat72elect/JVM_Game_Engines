#version 450
#extension GL_ARB_separate_shader_objects : enable
#define PI 3.1415926535897932384626433832795
#define iSteps 16
#define jSteps 8

precision highp float;

layout(location = 0) out vec4 albedo_out;
layout(location = 1) out vec4 worldPosition_out;
layout(location = 2) out vec4 normal_out;
layout(location = 3) out vec4 specular_emission_diffuse_ssao_bloom_out;
layout(location = 4) out vec4 lightScattering_out;

uniform mat4 m_Projection;
uniform mat4 m_View;
uniform vec3 v_Sun;
uniform float r_Sun;
uniform int width;
uniform int height;
uniform int isReflection;
uniform float bloom;
uniform float horizonVerticalShift;
uniform float reflectionVerticalShift;
const vec3 sunBaseColor = vec3(1.0f,0.79f,0.43f);

vec2 rsi(vec3 r0, vec3 rd, float sr) {
    // ray-sphere intersection that assumes
    // the sphere is centered at the origin.
    // No intersection when result.x > result.y
    float a = dot(rd, rd);
    float b = 2.0 * dot(rd, r0);
    float c = dot(r0, r0) - (sr * sr);
    float d = (b*b) - 4.0*a*c;
    if (d < 0.0) return vec2(1e5,-1e5);
    return vec2(
        (-b - sqrt(d))/(2.0*a),
        (-b + sqrt(d))/(2.0*a)
    );
}

vec3 atmosphere(vec3 r, vec3 r0, vec3 pSun, float iSun, float rPlanet, float rAtmos,
				vec3 kRlh, float kMie, float shRlh, float shMie, float g) {
    // Normalize the sun and view directions.
    pSun = normalize(pSun);
    r = normalize(r);

    // Calculate the step size of the primary ray.
    vec2 p = rsi(r0, r, rAtmos);
    if (p.x > p.y) return vec3(0,0,0);
    p.y = min(p.y, rsi(r0, r, rPlanet).x);
    float iStepSize = (p.y - p.x) / float(iSteps);

    // Initialize the primary ray time.
    float iTime = 0.0;

    // Initialize accumulators for Rayleigh and Mie scattering.
    vec3 totalRlh = vec3(0,0,0);
    vec3 totalMie = vec3(0,0,0);

    // Initialize optical depth accumulators for the primary ray.
    float iOdRlh = 0.0;
    float iOdMie = 0.0;

    // Calculate the Rayleigh and Mie phases.
    float mu = dot(r, pSun);
    float mumu = mu * mu;
    float gg = g * g;
    float pRlh = 3.0 / (16.0 * PI) * (1.0 + mumu);
    float pMie = 3.0 / (8.0 * PI) * ((1.0 - gg) * (mumu + 1.0)) / (pow(1.0 + gg - 2.0 * mu * g, 1.5) * (2.0 + gg));

    // Sample the primary ray.
    for (int i = 0; i < iSteps; i++) {

        // Calculate the primary ray sample position.
        vec3 iPos = r0 + r * (iTime + iStepSize * 0.5);

        // Calculate the height of the sample.
        float iHeight = length(iPos) - rPlanet;

        // Calculate the optical depth of the Rayleigh and Mie scattering for this step.
        float odStepRlh = exp(-iHeight / shRlh) * iStepSize;
        float odStepMie = exp(-iHeight / shMie) * iStepSize;

        // Accumulate optical depth.
        iOdRlh += odStepRlh;
        iOdMie += odStepMie;

        // Calculate the step size of the secondary ray.
        float jStepSize = rsi(iPos, pSun, rAtmos).y / float(jSteps);

        // Initialize the secondary ray time.
        float jTime = 0.0;

        // Initialize optical depth accumulators for the secondary ray.
        float jOdRlh = 0.0;
        float jOdMie = 0.0;

        // Sample the secondary ray.
        for (int j = 0; j < jSteps; j++) {

            // Calculate the secondary ray sample position.
            vec3 jPos = iPos + pSun * (jTime + jStepSize * 0.5);

            // Calculate the height of the sample.
            float jHeight = length(jPos) - rPlanet;

            // Accumulate the optical depth.
            jOdRlh += exp(-jHeight / shRlh) * jStepSize;
            jOdMie += exp(-jHeight / shMie) * jStepSize;

            // Increment the secondary ray time.
            jTime += jStepSize;
        }

        // Calculate attenuation.
        vec3 attn = exp(-(kMie * (iOdMie + jOdMie) + kRlh * (iOdRlh + jOdRlh)));

        // Accumulate scattering.
        totalRlh += odStepRlh * attn;
        totalMie += odStepMie * attn;

        // Increment the primary ray time.
        iTime += iStepSize;

    }

    // Calculate and return the final color.
    return iSun * (pRlh * kRlh * totalRlh + pMie * kMie * totalMie);
}


void main() {

	vec2 ndc = vec2(gl_FragCoord.x/width * 2 -1,gl_FragCoord.y/height * 2 -1);
	vec4 ray_clip = vec4(ndc, -1.0, 1.0);
	vec4 ray_eye = inverse(m_Projection) * ray_clip;
	ray_eye = vec4(ray_eye.xy, 1.0, 0.0);
	vec3 ray_world = (inverse(m_View) * ray_eye).xyz;
	
	if (isReflection == 1){
		ray_world.y *= -1;
		ray_world.y += reflectionVerticalShift;
	}
	else{
		ray_world.y += horizonVerticalShift;
	}
	
	vec3 out_LightScattering = vec3(0);
	
    vec3 out_Color = atmosphere(
        normalize(ray_world),        	// normalized ray direction
        vec3(0,6372e3,0),              	// ray origin
        v_Sun,                  		// position of the sun
        isReflection == 0 ? 48.0 : 22.0,// intensity of the sun
        6371e3,                         // radius of the planet in meters
        6471e3,                         // radius of the atmosphere in meters
        vec3(5.5e-6, 13.0e-6, 22.4e-6), // Rayleigh scattering coefficient
        21e-6,                          // Mie scattering coefficient
        8e3,                            // Rayleigh scale height
        1.2e3,                          // Mie scale height
        0.758                           // Mie preferred scattering direction
    );
	
	// Apply exposure.
    out_Color = 1.0 - exp(-1.0 * out_Color);
	
	float sunRadius = length(normalize(ray_world)- normalize(v_Sun));
	
	// no sun rendering when scene reflection
	if(sunRadius < r_Sun && isReflection == 0)
	{
		sunRadius /= r_Sun;
		float smoothRadius = smoothstep(0,1,0.1f/sunRadius-0.1f);
		out_Color = mix(out_Color, sunBaseColor * 4, smoothRadius);
		
		smoothRadius = smoothstep(0,1,0.18f/sunRadius-0.2f);
		out_LightScattering = mix(vec3(0), sunBaseColor, smoothRadius);
	}

    albedo_out = vec4(out_Color,1);
	worldPosition_out = vec4(0,0,0,0);
	normal_out = vec4(0,0,0,0);
	specular_emission_diffuse_ssao_bloom_out = vec4(0,0,0,bloom);
	lightScattering_out = vec4(out_LightScattering,0);
}