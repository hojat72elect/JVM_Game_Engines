/*
 * Copyright (c) 2003-2018 jMonkeyEngine
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 * 
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.gde.glsl.highlighter.lexer;

import com.jme3.gde.glsl.highlighter.util.Trie;
import java.util.ArrayList;
import java.util.List;

/**
 * Brace, yourselves, this file contains every word that means something in
 * GLSL. Expect about 400 lines of code that just adds strings.
 *
 * @author grizeldi
 */
public final class GlslKeywordLibrary {
    
    public enum KeywordType {
        KEYWORD, BUILTIN_FUNCTION, BUILTIN_VARIABLE, BASIC_TYPE, UNFINISHED;
    }

    public record Keyword(String keyword, KeywordType keywordType) {

    }
    
    private static final Trie keywords = new Trie();
    private static final Trie builtinFunctions = new Trie();
    private static final Trie builtinVariables = new Trie();
    private static final Trie basicTypes = new Trie();

    static {
        //keywords
        keywords.insert("attribute");
        keywords.insert("const");
        keywords.insert("uniform");
        keywords.insert("varying");
        keywords.insert("buffer");
        keywords.insert("shared");
        keywords.insert("coherent");
        keywords.insert("volatile");
        keywords.insert("restrict");
        keywords.insert("readonly");
        keywords.insert("writeonly");
        keywords.insert("atomic_uint");
        keywords.insert("layout");
        keywords.insert("centroid");
        keywords.insert("flat");
        keywords.insert("smooth");
        keywords.insert("noperspective");
        keywords.insert("patch");
        keywords.insert("sample");
        keywords.insert("break");
        keywords.insert("continue");
        keywords.insert("do");
        keywords.insert("for");
        keywords.insert("while");
        keywords.insert("switch");
        keywords.insert("case");
        keywords.insert("default");
        keywords.insert("if");
        keywords.insert("else");
        keywords.insert("subroutine");
        keywords.insert("in");
        keywords.insert("out");
        keywords.insert("inout");
        keywords.insert("void");
        keywords.insert("true");
        keywords.insert("false");
        keywords.insert("invariant");
        keywords.insert("precise");
        keywords.insert("discard");
        keywords.insert("return");
        //primitives and other types
        basicTypes.insert("float");
        basicTypes.insert("double");
        basicTypes.insert("int");
        basicTypes.insert("bool");
        basicTypes.insert("mat2");
        basicTypes.insert("mat3");
        basicTypes.insert("mat4");
        basicTypes.insert("dmat2");
        basicTypes.insert("dmat3");
        basicTypes.insert("dmat4");
        basicTypes.insert("mat2x2");
        basicTypes.insert("mat2x3");
        basicTypes.insert("mat2x4");
        basicTypes.insert("dmat2x2");
        basicTypes.insert("dmat2x3");
        basicTypes.insert("dmat2x4");
        basicTypes.insert("mat3x2");
        basicTypes.insert("mat3x3");
        basicTypes.insert("mat3x4");
        basicTypes.insert("dmat3x2");
        basicTypes.insert("dmat3x3");
        basicTypes.insert("dmat3x4");
        basicTypes.insert("mat4x2");
        basicTypes.insert("mat4x3");
        basicTypes.insert("mat4x4");
        basicTypes.insert("dmat4x2");
        basicTypes.insert("dmat4x3");
        basicTypes.insert("dmat4x4");
        basicTypes.insert("vec2");
        basicTypes.insert("vec3");
        basicTypes.insert("vec4");
        basicTypes.insert("ivec2");
        basicTypes.insert("ivec3");
        basicTypes.insert("ivec4");
        basicTypes.insert("bvec2");
        basicTypes.insert("bvec3");
        basicTypes.insert("bvec4");
        basicTypes.insert("dvec2");
        basicTypes.insert("dvec3");
        basicTypes.insert("dvec4");
        basicTypes.insert("uint");
        basicTypes.insert("uvec2");
        basicTypes.insert("uvec3");
        basicTypes.insert("uvec4");
        basicTypes.insert("lowp");
        basicTypes.insert("mediump");
        basicTypes.insert("highp");
        basicTypes.insert("precision");
        basicTypes.insert("sampler1D");
        basicTypes.insert("sampler2D");
        basicTypes.insert("sampler3D");
        basicTypes.insert("samplerCube");
        basicTypes.insert("sampler1DShadow");
        basicTypes.insert("sampler2DShadow");
        basicTypes.insert("samplerCubeShadow");
        basicTypes.insert("sampler1DArray");
        basicTypes.insert("sampler2DArray");
        basicTypes.insert("sampler1DArrayShadow");
        basicTypes.insert("sampler2DArrayShadow");
        basicTypes.insert("isampler1D");
        basicTypes.insert("isampler2D");
        basicTypes.insert("isampler3D");
        basicTypes.insert("isamplerCube");
        basicTypes.insert("isampler1DArray");
        basicTypes.insert("isampler2DArray");
        basicTypes.insert("usampler1D");
        basicTypes.insert("usampler2D");
        basicTypes.insert("usampler3D");
        basicTypes.insert("usamplerCube");
        basicTypes.insert("usampler1DArray");
        basicTypes.insert("usampler2DArray");
        basicTypes.insert("sampler2DRect");
        basicTypes.insert("sampler2DRectShadow");
        basicTypes.insert("isampler2DRect");
        basicTypes.insert("usampler2DRect");
        basicTypes.insert("samplerBuffer");
        basicTypes.insert("isamplerBuffer");
        basicTypes.insert("usamplerBuffer");
        basicTypes.insert("sampler2DMS");
        basicTypes.insert("isampler2DMS");
        basicTypes.insert("usampler2DMS");
        basicTypes.insert("sampler2DMSArray");
        basicTypes.insert("isampler2DMSArray");
        basicTypes.insert("usampler2DMSArray");
        basicTypes.insert("samplerCubeArray");
        basicTypes.insert("samplerCubeArrayShadow");
        basicTypes.insert("isamplerCubeArray");
        basicTypes.insert("usamplerCubeArray");
        basicTypes.insert("image1D");
        basicTypes.insert("iimage1D");
        basicTypes.insert("uimage1D");
        basicTypes.insert("image2D");
        basicTypes.insert("iimage2D");
        basicTypes.insert("uimage2D");
        basicTypes.insert("image3D");
        basicTypes.insert("iimage3D");
        basicTypes.insert("uimage3D");
        basicTypes.insert("image2DRect");
        basicTypes.insert("iimage2DRect");
        basicTypes.insert("uimage2DRect");
        basicTypes.insert("imageCube");
        basicTypes.insert("iimageCube");
        basicTypes.insert("uimageCube");
        basicTypes.insert("imageBuffer");
        basicTypes.insert("iimageBuffer");
        basicTypes.insert("uimageBuffer");
        basicTypes.insert("image1DArray");
        basicTypes.insert("iimage1DArray");
        basicTypes.insert("uimage1DArray");
        basicTypes.insert("image2DArray");
        basicTypes.insert("iimage2DArray");
        basicTypes.insert("uimage2DArray");
        basicTypes.insert("imageCubeArray");
        basicTypes.insert("iimageCubeArray");
        basicTypes.insert("uimageCubeArray");
        basicTypes.insert("image2DMS");
        basicTypes.insert("iimage2DMS");
        basicTypes.insert("uimage2DMS");
        basicTypes.insert("image2DMSArray");
        basicTypes.insert("iimage2DMSArray");
        basicTypes.insert("uimage2DMSArray");
        basicTypes.insert("struct");
        //builtin variables
        //compute shaders
        builtinVariables.insert("gl_NumWorkGroups");
        builtinVariables.insert("gl_WorkGroupSize");
        builtinVariables.insert("gl_WorkGroupID");
        builtinVariables.insert("gl_LocalInvocationID");
        builtinVariables.insert("gl_GlobalInvocationID");
        builtinVariables.insert("gl_LocalInvocationIndex");
        //vertex shaders
        builtinVariables.insert("gl_VertexID");
        builtinVariables.insert("gl_InstanceID");
        builtinVariables.insert("gl_Position");
        //geometry shaders
        builtinVariables.insert("gl_PrimitiveIDIn");
        builtinVariables.insert("gl_Layer");
        builtinVariables.insert("gl_ViewportIndex");
        //tesselation shaders
        builtinVariables.insert("gl_MaxPatchVertices");
        builtinVariables.insert("gl_PatchVerticesIn");
        builtinVariables.insert("gl_TessLevelOuter");
        builtinVariables.insert("gl_TessLevelInner");
        builtinVariables.insert("gl_TessCoord");
        //fragment shaders
        builtinVariables.insert("gl_FragCoord");
        builtinVariables.insert("gl_FrontFacing");
        builtinVariables.insert("gl_PointCoord");
        builtinVariables.insert("gl_SampleID");
        builtinVariables.insert("gl_SamplePosition");
        builtinVariables.insert("gl_SampleMaskIn");
        builtinVariables.insert("gl_Layer");
        builtinVariables.insert("gl_ViewportIndex");
        builtinVariables.insert("gl_FragColor");
        //general
        builtinVariables.insert("gl_Position");
        builtinVariables.insert("gl_PointSize");
        builtinVariables.insert("gl_ClipDistance");
        builtinVariables.insert("gl_InvocationID");
        builtinVariables.insert("gl_PrimitiveID");
        //jme variables - this is why we build custom plugins :) (apart from existing being under GPL)
        builtinVariables.insert("inPosition");
        builtinVariables.insert("inNormal");
        builtinVariables.insert("inColor");
        builtinVariables.insert("inTextCoord");
        builtinVariables.insert("g_WorldMatrix");
        builtinVariables.insert("g_ViewMatrix");
        builtinVariables.insert("g_ProjectionMatrix");
        builtinVariables.insert("g_WorldViewMatrix");
        builtinVariables.insert("g_WorldViewProjectionMatrix");
        builtinVariables.insert("g_WorldNormalMatrix");
        builtinVariables.insert("g_NormalMatrix");
        builtinVariables.insert("g_ViewProjectionMatrix");
        builtinVariables.insert("g_WorldMatrixInverseTranspose");
        builtinVariables.insert("g_WorldMatrixInverse");
        builtinVariables.insert("g_ViewMatrixInverse");
        builtinVariables.insert("g_ProjectionMatrixInverse");
        builtinVariables.insert("g_ViewProjectionMatrixInverse");
        builtinVariables.insert("g_WorldViewMatrixInverse");
        builtinVariables.insert("g_NormalMatrixInverse");
        builtinVariables.insert("g_WorldViewProjectionMatrixInverse");
        builtinVariables.insert("g_ViewPort");
        builtinVariables.insert("g_FrustumNearFar");
        builtinVariables.insert("g_Resolution");
        builtinVariables.insert("g_ResolutionInverse");
        builtinVariables.insert("g_Aspect");
        builtinVariables.insert("g_CameraPosition");
        builtinVariables.insert("g_CameraDirection");
        builtinVariables.insert("g_CameraLeft");
        builtinVariables.insert("g_CameraUp");
        builtinVariables.insert("g_Time");
        builtinVariables.insert("g_Tpf");
        builtinVariables.insert("g_FrameRate");
        builtinVariables.insert("g_LightDirection");
        builtinVariables.insert("g_LightPosition");
        builtinVariables.insert("g_LightColor");
        builtinVariables.insert("g_AmbientLightColor");
        //builtin functions
        builtinFunctions.insert("radians");
        builtinFunctions.insert("degrees");
        builtinFunctions.insert("sin");
        builtinFunctions.insert("cos");
        builtinFunctions.insert("tan");
        builtinFunctions.insert("asin");
        builtinFunctions.insert("acos");
        builtinFunctions.insert("atan");
        builtinFunctions.insert("sinh");
        builtinFunctions.insert("cosh");
        builtinFunctions.insert("tanh");
        builtinFunctions.insert("asinh");
        builtinFunctions.insert("acosh");
        builtinFunctions.insert("atanh");
        builtinFunctions.insert("pow");
        builtinFunctions.insert("exp");
        builtinFunctions.insert("log");
        builtinFunctions.insert("exp2");
        builtinFunctions.insert("log2");
        builtinFunctions.insert("sqrt");
        builtinFunctions.insert("inversesqrt");
        builtinFunctions.insert("abs");
        builtinFunctions.insert("sign");
        builtinFunctions.insert("floor");
        builtinFunctions.insert("trunc");
        builtinFunctions.insert("round");
        builtinFunctions.insert("roundEven");
        builtinFunctions.insert("ceil");
        builtinFunctions.insert("fract");
        builtinFunctions.insert("mod");
        builtinFunctions.insert("modf");
        builtinFunctions.insert("min");
        builtinFunctions.insert("max");
        builtinFunctions.insert("clamp");
        builtinFunctions.insert("mix");
        builtinFunctions.insert("step");
        builtinFunctions.insert("smoothstep");
        builtinFunctions.insert("isnan");
        builtinFunctions.insert("isinf");
        builtinFunctions.insert("floatBitsToInt");
        builtinFunctions.insert("floatBitsToUInt");
        builtinFunctions.insert("intBitsToFloat");
        builtinFunctions.insert("uintBitsToFloat");
        builtinFunctions.insert("fma");
        builtinFunctions.insert("frexp");
        builtinFunctions.insert("packUnorm2x16");
        builtinFunctions.insert("packSnorm2x16");
        builtinFunctions.insert("packUnorm4x8");
        builtinFunctions.insert("packSnorm4x8");
        builtinFunctions.insert("unpackUnorm2x16");
        builtinFunctions.insert("unpackSnorm2x16");
        builtinFunctions.insert("unpackUnorm4x8");
        builtinFunctions.insert("unpackSnorm4x8");
        builtinFunctions.insert("packDouble2x32");
        builtinFunctions.insert("unpackDouble2x32");
        builtinFunctions.insert("packHalf2x16");
        builtinFunctions.insert("unpackHalf2x16");
        builtinFunctions.insert("length");
        builtinFunctions.insert("distance");
        builtinFunctions.insert("dot");
        builtinFunctions.insert("cross");
        builtinFunctions.insert("normalize");
        builtinFunctions.insert("ftransform");
        builtinFunctions.insert("faceforward");
        builtinFunctions.insert("reflect");
        builtinFunctions.insert("refract");
        builtinFunctions.insert("matrixCompMult");
        builtinFunctions.insert("outerProduct");
        builtinFunctions.insert("transpose");
        builtinFunctions.insert("determinant");
        builtinFunctions.insert("inverse");
        builtinFunctions.insert("lessThan");
        builtinFunctions.insert("lessThanEqual");
        builtinFunctions.insert("greaterThan");
        builtinFunctions.insert("greaterThanEqual");
        builtinFunctions.insert("equal");
        builtinFunctions.insert("notEqual");
        builtinFunctions.insert("any");
        builtinFunctions.insert("all");
        builtinFunctions.insert("not");
        builtinFunctions.insert("uaddCarry");
        builtinFunctions.insert("usubBorrow");
        builtinFunctions.insert("umulExtended");
        builtinFunctions.insert("imulExtended");
        builtinFunctions.insert("bitfieldExtract");
        builtinFunctions.insert("bitfieldInsert");
        builtinFunctions.insert("bitfieldReverse");
        builtinFunctions.insert("bitCount");
        builtinFunctions.insert("findLSB");
        builtinFunctions.insert("findMSB");
        builtinFunctions.insert("textureSize");
        builtinFunctions.insert("textureQueryLod");
        builtinFunctions.insert("textureQueryLevels");
        builtinFunctions.insert("texture");
        builtinFunctions.insert("textureProj");
        builtinFunctions.insert("textureLod");
        builtinFunctions.insert("textureOffset");
        builtinFunctions.insert("texelFetch");
        builtinFunctions.insert("texelFetchOffset");
        builtinFunctions.insert("textureProjOffset");
        builtinFunctions.insert("textureLodOffset");
        builtinFunctions.insert("textureProjLod");
        builtinFunctions.insert("textureProjLodOffset");
        builtinFunctions.insert("textureGrad");
        builtinFunctions.insert("textureGradOffset");
        builtinFunctions.insert("textureProjGrad");
        builtinFunctions.insert("textureProjGradOffset");
        builtinFunctions.insert("textureGather");
        builtinFunctions.insert("textureGatherOffset");
        builtinFunctions.insert("textureGatherOffsets");
        builtinFunctions.insert("texture1D");
        builtinFunctions.insert("texture1DProj");
        builtinFunctions.insert("texture1DLod");
        builtinFunctions.insert("texture1DProjLod");
        builtinFunctions.insert("texture2D");
        builtinFunctions.insert("texture2DProj");
        builtinFunctions.insert("texture2DLod");
        builtinFunctions.insert("texture2DProjLod");
        builtinFunctions.insert("texture3D");
        builtinFunctions.insert("texture3DProj");
        builtinFunctions.insert("texture3DLod");
        builtinFunctions.insert("texture3DProjLod");
        builtinFunctions.insert("textureCube");
        builtinFunctions.insert("textureCubeLod");
        builtinFunctions.insert("shadow1D");
        builtinFunctions.insert("shadow2D");
        builtinFunctions.insert("shadow1DProj");
        builtinFunctions.insert("shadow2DProj");
        builtinFunctions.insert("shadow1DLod");
        builtinFunctions.insert("shadow2DLod");
        builtinFunctions.insert("shadow1DProjLod");
        builtinFunctions.insert("shadow2DProjLod");
        builtinFunctions.insert("atomicCounterIncrement");
        builtinFunctions.insert("atomicCounterDecrement");
        builtinFunctions.insert("atomicCounter");
        builtinFunctions.insert("atomicAdd");
        builtinFunctions.insert("atomicMin");
        builtinFunctions.insert("atomicMax");
        builtinFunctions.insert("atomicAnd");
        builtinFunctions.insert("atomicOr");
        builtinFunctions.insert("atomicXor");
        builtinFunctions.insert("atomicExchange");
        builtinFunctions.insert("atomicCompSwap");
        builtinFunctions.insert("imageSize");
        builtinFunctions.insert("imageLoad");
        builtinFunctions.insert("imageStore");
        builtinFunctions.insert("imageAtomicAdd");
        builtinFunctions.insert("imageAtomicMin");
        builtinFunctions.insert("imageAtomicMax");
        builtinFunctions.insert("imageAtomicAnd");
        builtinFunctions.insert("imageAtomicOr");
        builtinFunctions.insert("imageAtomicXor");
        builtinFunctions.insert("imageAtomicExchange");
        builtinFunctions.insert("imageAtomicCompSwap");
        builtinFunctions.insert("dFdx");
        builtinFunctions.insert("dFdy");
        builtinFunctions.insert("fwidth");
        builtinFunctions.insert("interpolateAtCentroid");
        builtinFunctions.insert("interpolateAtSample");
        builtinFunctions.insert("interpolateAtOffset");
        builtinFunctions.insert("noise1");
        builtinFunctions.insert("noise2");
        builtinFunctions.insert("noise3");
        builtinFunctions.insert("noise4");
        builtinFunctions.insert("EmitStreamVertex");
        builtinFunctions.insert("EndStreamPrimitive");
        builtinFunctions.insert("EmitVertex");
        builtinFunctions.insert("EndPrimitive");
        builtinFunctions.insert("barrier");
        builtinFunctions.insert("memoryBarrier");
        builtinFunctions.insert("memoryBarrierAtomicCounter");
        builtinFunctions.insert("memoryBarrierBuffer");
        builtinFunctions.insert("memoryBarrierShared");
        builtinFunctions.insert("memoryBarrierImage");
        builtinFunctions.insert("groupMemoryBarrier");
    }

    /**
     * Finds if given string is either a partial match, full match or nothing at
     * all
     *
     * @param s string to search for
     * @return returns the status of the string
     */
    public static KeywordType lookup(String s) {
        KeywordType returnType = null;
        returnType = lookup(s, returnType, KeywordType.BASIC_TYPE, basicTypes);
        if (returnType == KeywordType.UNFINISHED || returnType == null) {
            returnType = lookup(s, returnType, KeywordType.BUILTIN_VARIABLE, builtinVariables);
        }
        if (returnType == KeywordType.UNFINISHED || returnType == null) {
            returnType = lookup(s, returnType, KeywordType.BUILTIN_FUNCTION, builtinFunctions);
        }
        if (returnType == KeywordType.UNFINISHED || returnType == null) {
            returnType = lookup(s, returnType, KeywordType.KEYWORD, keywords);
        }

        return returnType;
    }

    /**
     * Gets all the possible matches for given string (auto-complete)
     *
     * @param s string to search for
     * @return all the matches for given string
     */
    public static List<Keyword> lookupAll(String s) {
        List<Keyword> matches = new ArrayList<>();

        matches.addAll(basicTypes.searchAll(s).stream().map((keyword) -> new Keyword(keyword, KeywordType.BASIC_TYPE)).toList());
        matches.addAll(builtinVariables.searchAll(s).stream().map((keyword) -> new Keyword(keyword, KeywordType.BUILTIN_VARIABLE)).toList());
        matches.addAll(builtinFunctions.searchAll(s).stream().map((keyword) -> new Keyword(keyword, KeywordType.BUILTIN_FUNCTION)).toList());
        matches.addAll(keywords.searchAll(s).stream().map((keyword) -> new Keyword(keyword, KeywordType.KEYWORD)).toList());

        return matches;
    }

    private static KeywordType lookup(String s, KeywordType currentType, KeywordType matchType, Trie searchTrie) {
        Trie.MatchType match = searchTrie.search(s);
        if (match == Trie.MatchType.FULL_MATCH) {
            return matchType;
        }
        if (match == Trie.MatchType.PARTIAL_MATCH) {
            return KeywordType.UNFINISHED;
        }

        return currentType;
    }
}
