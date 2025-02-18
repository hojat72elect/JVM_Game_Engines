/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package opengl.templates

import org.lwjgl.generator.*
import opengl.*

val NV_half_float = "NVHalfFloat".nativeClassGL("NV_half_float", postfix = NV) {
    IntConstant(
        "HALF_FLOAT_NV"..0x140B
    )

    void(
        "Vertex2hNV",

        GLhalfNV("x"),
        GLhalfNV("y")
    )

    void(
        "Vertex2hvNV",

        Check(2)..GLhalfNV.const.p("v")
    )

    void(
        "Vertex3hNV",

        GLhalfNV("x"),
        GLhalfNV("y"),
        GLhalfNV("z")
    )

    void(
        "Vertex3hvNV",

        Check(3)..GLhalfNV.const.p("v")
    )

    void(
        "Vertex4hNV",

        GLhalfNV("x"),
        GLhalfNV("y"),
        GLhalfNV("z"),
        GLhalfNV("w")
    )

    void(
        "Vertex4hvNV",

        Check(4)..GLhalfNV.const.p("v")
    )

    void(
        "Normal3hNV",

        GLhalfNV("nx"),
        GLhalfNV("ny"),
        GLhalfNV("nz")
    )

    void(
        "Normal3hvNV",

        Check(3)..GLhalfNV.const.p("v")
    )

    void(
        "Color3hNV",

        GLhalfNV("red"),
        GLhalfNV("green"),
        GLhalfNV("blue")
    )

    void(
        "Color3hvNV",

        Check(3)..GLhalfNV.const.p("v")
    )

    void(
        "Color4hNV",

        GLhalfNV("red"),
        GLhalfNV("green"),
        GLhalfNV("blue"),
        GLhalfNV("alpha")
    )

    void(
        "Color4hvNV",

        Check(4)..GLhalfNV.const.p("v")
    )

    void(
        "TexCoord1hNV",

        GLhalfNV("s")
    )

    void(
        "TexCoord1hvNV",

        Check(1)..GLhalfNV.const.p("v")
    )

    void(
        "TexCoord2hNV",

        GLhalfNV("s"),
        GLhalfNV("t")
    )

    void(
        "TexCoord2hvNV",

        Check(2)..GLhalfNV.const.p("v")
    )

    void(
        "TexCoord3hNV",

        GLhalfNV("s"),
        GLhalfNV("t"),
        GLhalfNV("r")
    )

    void(
        "TexCoord3hvNV",

        Check(3)..GLhalfNV.const.p("v")
    )

    void(
        "TexCoord4hNV",

        GLhalfNV("s"),
        GLhalfNV("t"),
        GLhalfNV("r"),
        GLhalfNV("q")
    )

    void(
        "TexCoord4hvNV",

        Check(4)..GLhalfNV.const.p("v")
    )

    void(
        "MultiTexCoord1hNV",

        GLenum("target"),
        GLhalfNV("s")
    )

    void(
        "MultiTexCoord1hvNV",

        GLenum("target"),
        Check(1)..GLhalfNV.const.p("v")
    )

    void(
        "MultiTexCoord2hNV",

        GLenum("target"),
        GLhalfNV("s"),
        GLhalfNV("t")
    )

    void(
        "MultiTexCoord2hvNV",

        GLenum("target"),
        Check(2)..GLhalfNV.const.p("v")
    )

    void(
        "MultiTexCoord3hNV",

        GLenum("target"),
        GLhalfNV("s"),
        GLhalfNV("t"),
        GLhalfNV("r")
    )

    void(
        "MultiTexCoord3hvNV",

        GLenum("target"),
        Check(3)..GLhalfNV.const.p("v")
    )

    void(
        "MultiTexCoord4hNV",

        GLenum("target"),
        GLhalfNV("s"),
        GLhalfNV("t"),
        GLhalfNV("r"),
        GLhalfNV("q")
    )

    void(
        "MultiTexCoord4hvNV",

        GLenum("target"),
        Check(4)..GLhalfNV.const.p("v")
    )

    DependsOn("GL_EXT_fog_coord")..void(
        "FogCoordhNV",

        GLhalfNV("fog")
    )

    DependsOn("GL_EXT_fog_coord")..void(
        "FogCoordhvNV",

        Check(1)..GLhalfNV.const.p("fog")
    )

    DependsOn("GL_EXT_secondary_color")..void(
        "SecondaryColor3hNV",

        GLhalfNV("red"),
        GLhalfNV("green"),
        GLhalfNV("blue")
    )

    DependsOn("GL_EXT_secondary_color")..void(
        "SecondaryColor3hvNV",

        Check(3)..GLhalfNV.const.p("v")
    )

    DependsOn("GL_EXT_vertex_weighting")..void(
        "VertexWeighthNV",

        GLhalfNV("weight")
    )

    DependsOn("GL_EXT_vertex_weighting")..void(
        "VertexWeighthvNV",

        Check(1)..GLhalfNV.const.p("weight")
    )

    DependsOn("GL_NV_vertex_program")..void(
        "VertexAttrib1hNV",

        GLuint("index"),
        GLhalfNV("x")
    )

    DependsOn("GL_NV_vertex_program")..void(
        "VertexAttrib1hvNV",

        GLuint("index"),
        Check(1)..GLhalfNV.const.p("v")
    )

    DependsOn("GL_NV_vertex_program")..void(
        "VertexAttrib2hNV",

        GLuint("index"),
        GLhalfNV("x"),
        GLhalfNV("y")
    )

    DependsOn("GL_NV_vertex_program")..void(
        "VertexAttrib2hvNV",

        GLuint("index"),
        Check(2)..GLhalfNV.const.p("v")
    )

    DependsOn("GL_NV_vertex_program")..void(
        "VertexAttrib3hNV",

        GLuint("index"),
        GLhalfNV("x"),
        GLhalfNV("y"),
        GLhalfNV("z")
    )

    DependsOn("GL_NV_vertex_program")..void(
        "VertexAttrib3hvNV",

        GLuint("index"),
        Check(3)..GLhalfNV.const.p("v")
    )

    DependsOn("GL_NV_vertex_program")..void(
        "VertexAttrib4hNV",

        GLuint("index"),
        GLhalfNV("x"),
        GLhalfNV("y"),
        GLhalfNV("z"),
        GLhalfNV("w")
    )

    DependsOn("GL_NV_vertex_program")..void(
        "VertexAttrib4hvNV",

        GLuint("index"),
        Check(4)..GLhalfNV.const.p("v")
    )

    DependsOn("GL_NV_vertex_program")..void(
        "VertexAttribs1hvNV",

        GLuint("index"),
        AutoSize("v")..GLsizei("n"),
        GLhalfNV.const.p("v")
    )

    DependsOn("GL_NV_vertex_program")..void(
        "VertexAttribs2hvNV",

        GLuint("index"),
        AutoSize(2, "v")..GLsizei("n"),
        GLhalfNV.const.p("v")
    )

    DependsOn("GL_NV_vertex_program")..void(
        "VertexAttribs3hvNV",

        GLuint("index"),
        AutoSize(3, "v")..GLsizei("n"),
        GLhalfNV.const.p("v")
    )

    DependsOn("GL_NV_vertex_program")..void(
        "VertexAttribs4hvNV",

        GLuint("index"),
        AutoSize(4, "v")..GLsizei("n"),
        GLhalfNV.const.p("v")
    )
}