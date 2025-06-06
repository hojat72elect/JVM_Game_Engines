/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// copied from https://cs.android.com/android/platform/superproject/+/master:frameworks/base/opengl/java/android/opengl/GLES31.java

package com.badlogic.gdx.graphics;

public interface GL31 extends GL30 {

    int GL_VERTEX_SHADER_BIT = 0x00000001;
    int GL_FRAGMENT_SHADER_BIT = 0x00000002;
    int GL_COMPUTE_SHADER_BIT = 0x00000020;
    int GL_ALL_SHADER_BITS = -1; // 0xFFFFFFFF

    int GL_VERTEX_ATTRIB_ARRAY_BARRIER_BIT = 0x00000001;
    int GL_ELEMENT_ARRAY_BARRIER_BIT = 0x00000002;
    int GL_UNIFORM_BARRIER_BIT = 0x00000004;
    int GL_TEXTURE_FETCH_BARRIER_BIT = 0x00000008;
    int GL_SHADER_IMAGE_ACCESS_BARRIER_BIT = 0x00000020;
    int GL_COMMAND_BARRIER_BIT = 0x00000040;
    int GL_PIXEL_BUFFER_BARRIER_BIT = 0x00000080;
    int GL_TEXTURE_UPDATE_BARRIER_BIT = 0x00000100;
    int GL_BUFFER_UPDATE_BARRIER_BIT = 0x00000200;
    int GL_FRAMEBUFFER_BARRIER_BIT = 0x00000400;
    int GL_TRANSFORM_FEEDBACK_BARRIER_BIT = 0x00000800;
    int GL_ATOMIC_COUNTER_BARRIER_BIT = 0x00001000;
    int GL_SHADER_STORAGE_BARRIER_BIT = 0x00002000;
    int GL_ALL_BARRIER_BITS = -1; // 0xFFFFFFFF

    int GL_TEXTURE_WIDTH = 0x1000;
    int GL_TEXTURE_HEIGHT = 0x1001;
    int GL_TEXTURE_INTERNAL_FORMAT = 0x1003;
    int GL_STENCIL_INDEX = 0x1901;
    int GL_TEXTURE_RED_SIZE = 0x805C;
    int GL_TEXTURE_GREEN_SIZE = 0x805D;
    int GL_TEXTURE_BLUE_SIZE = 0x805E;
    int GL_TEXTURE_ALPHA_SIZE = 0x805F;
    int GL_TEXTURE_DEPTH = 0x8071;
    int GL_PROGRAM_SEPARABLE = 0x8258;
    int GL_ACTIVE_PROGRAM = 0x8259;
    int GL_PROGRAM_PIPELINE_BINDING = 0x825A;
    int GL_MAX_COMPUTE_SHARED_MEMORY_SIZE = 0x8262;
    int GL_MAX_COMPUTE_UNIFORM_COMPONENTS = 0x8263;
    int GL_MAX_COMPUTE_ATOMIC_COUNTER_BUFFERS = 0x8264;
    int GL_MAX_COMPUTE_ATOMIC_COUNTERS = 0x8265;
    int GL_MAX_COMBINED_COMPUTE_UNIFORM_COMPONENTS = 0x8266;
    int GL_COMPUTE_WORK_GROUP_SIZE = 0x8267;
    int GL_MAX_UNIFORM_LOCATIONS = 0x826E;
    int GL_VERTEX_ATTRIB_BINDING = 0x82D4;
    int GL_VERTEX_ATTRIB_RELATIVE_OFFSET = 0x82D5;
    int GL_VERTEX_BINDING_DIVISOR = 0x82D6;
    int GL_VERTEX_BINDING_OFFSET = 0x82D7;
    int GL_VERTEX_BINDING_STRIDE = 0x82D8;
    int GL_MAX_VERTEX_ATTRIB_RELATIVE_OFFSET = 0x82D9;
    int GL_MAX_VERTEX_ATTRIB_BINDINGS = 0x82DA;
    int GL_MAX_VERTEX_ATTRIB_STRIDE = 0x82E5;
    int GL_TEXTURE_COMPRESSED = 0x86A1;
    int GL_TEXTURE_DEPTH_SIZE = 0x884A;
    int GL_READ_ONLY = 0x88B8;
    int GL_WRITE_ONLY = 0x88B9;
    int GL_READ_WRITE = 0x88BA;
    int GL_TEXTURE_STENCIL_SIZE = 0x88F1;
    int GL_TEXTURE_RED_TYPE = 0x8C10;
    int GL_TEXTURE_GREEN_TYPE = 0x8C11;
    int GL_TEXTURE_BLUE_TYPE = 0x8C12;
    int GL_TEXTURE_ALPHA_TYPE = 0x8C13;
    int GL_TEXTURE_DEPTH_TYPE = 0x8C16;
    int GL_TEXTURE_SHARED_SIZE = 0x8C3F;
    int GL_SAMPLE_POSITION = 0x8E50;
    int GL_SAMPLE_MASK = 0x8E51;
    int GL_SAMPLE_MASK_VALUE = 0x8E52;
    int GL_MAX_SAMPLE_MASK_WORDS = 0x8E59;
    int GL_MIN_PROGRAM_TEXTURE_GATHER_OFFSET = 0x8E5E;
    int GL_MAX_PROGRAM_TEXTURE_GATHER_OFFSET = 0x8E5F;
    int GL_MAX_IMAGE_UNITS = 0x8F38;
    int GL_MAX_COMBINED_SHADER_OUTPUT_RESOURCES = 0x8F39;
    int GL_IMAGE_BINDING_NAME = 0x8F3A;
    int GL_IMAGE_BINDING_LEVEL = 0x8F3B;
    int GL_IMAGE_BINDING_LAYERED = 0x8F3C;
    int GL_IMAGE_BINDING_LAYER = 0x8F3D;
    int GL_IMAGE_BINDING_ACCESS = 0x8F3E;
    int GL_DRAW_INDIRECT_BUFFER = 0x8F3F;
    int GL_DRAW_INDIRECT_BUFFER_BINDING = 0x8F43;
    int GL_VERTEX_BINDING_BUFFER = 0x8F4F;
    int GL_IMAGE_2D = 0x904D;
    int GL_IMAGE_3D = 0x904E;
    int GL_IMAGE_CUBE = 0x9050;
    int GL_IMAGE_2D_ARRAY = 0x9053;
    int GL_INT_IMAGE_2D = 0x9058;
    int GL_INT_IMAGE_3D = 0x9059;
    int GL_INT_IMAGE_CUBE = 0x905B;
    int GL_INT_IMAGE_2D_ARRAY = 0x905E;
    int GL_UNSIGNED_INT_IMAGE_2D = 0x9063;
    int GL_UNSIGNED_INT_IMAGE_3D = 0x9064;
    int GL_UNSIGNED_INT_IMAGE_CUBE = 0x9066;
    int GL_UNSIGNED_INT_IMAGE_2D_ARRAY = 0x9069;
    int GL_IMAGE_BINDING_FORMAT = 0x906E;
    int GL_IMAGE_FORMAT_COMPATIBILITY_TYPE = 0x90C7;
    int GL_IMAGE_FORMAT_COMPATIBILITY_BY_SIZE = 0x90C8;
    int GL_IMAGE_FORMAT_COMPATIBILITY_BY_CLASS = 0x90C9;
    int GL_MAX_VERTEX_IMAGE_UNIFORMS = 0x90CA;
    int GL_MAX_FRAGMENT_IMAGE_UNIFORMS = 0x90CE;
    int GL_MAX_COMBINED_IMAGE_UNIFORMS = 0x90CF;
    int GL_SHADER_STORAGE_BUFFER = 0x90D2;
    int GL_SHADER_STORAGE_BUFFER_BINDING = 0x90D3;
    int GL_SHADER_STORAGE_BUFFER_START = 0x90D4;
    int GL_SHADER_STORAGE_BUFFER_SIZE = 0x90D5;
    int GL_MAX_VERTEX_SHADER_STORAGE_BLOCKS = 0x90D6;
    int GL_MAX_FRAGMENT_SHADER_STORAGE_BLOCKS = 0x90DA;
    int GL_MAX_COMPUTE_SHADER_STORAGE_BLOCKS = 0x90DB;
    int GL_MAX_COMBINED_SHADER_STORAGE_BLOCKS = 0x90DC;
    int GL_MAX_SHADER_STORAGE_BUFFER_BINDINGS = 0x90DD;
    int GL_MAX_SHADER_STORAGE_BLOCK_SIZE = 0x90DE;
    int GL_SHADER_STORAGE_BUFFER_OFFSET_ALIGNMENT = 0x90DF;
    int GL_DEPTH_STENCIL_TEXTURE_MODE = 0x90EA;
    int GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS = 0x90EB;
    int GL_DISPATCH_INDIRECT_BUFFER = 0x90EE;
    int GL_DISPATCH_INDIRECT_BUFFER_BINDING = 0x90EF;
    int GL_TEXTURE_2D_MULTISAMPLE = 0x9100;
    int GL_TEXTURE_BINDING_2D_MULTISAMPLE = 0x9104;
    int GL_TEXTURE_SAMPLES = 0x9106;
    int GL_TEXTURE_FIXED_SAMPLE_LOCATIONS = 0x9107;
    int GL_SAMPLER_2D_MULTISAMPLE = 0x9108;
    int GL_INT_SAMPLER_2D_MULTISAMPLE = 0x9109;
    int GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE = 0x910A;
    int GL_MAX_COLOR_TEXTURE_SAMPLES = 0x910E;
    int GL_MAX_DEPTH_TEXTURE_SAMPLES = 0x910F;
    int GL_MAX_INTEGER_SAMPLES = 0x9110;
    int GL_COMPUTE_SHADER = 0x91B9;
    int GL_MAX_COMPUTE_UNIFORM_BLOCKS = 0x91BB;
    int GL_MAX_COMPUTE_TEXTURE_IMAGE_UNITS = 0x91BC;
    int GL_MAX_COMPUTE_IMAGE_UNIFORMS = 0x91BD;
    int GL_MAX_COMPUTE_WORK_GROUP_COUNT = 0x91BE;
    int GL_MAX_COMPUTE_WORK_GROUP_SIZE = 0x91BF;
    int GL_ATOMIC_COUNTER_BUFFER = 0x92C0;
    int GL_ATOMIC_COUNTER_BUFFER_BINDING = 0x92C1;
    int GL_ATOMIC_COUNTER_BUFFER_START = 0x92C2;
    int GL_ATOMIC_COUNTER_BUFFER_SIZE = 0x92C3;
    int GL_MAX_VERTEX_ATOMIC_COUNTER_BUFFERS = 0x92CC;
    int GL_MAX_FRAGMENT_ATOMIC_COUNTER_BUFFERS = 0x92D0;
    int GL_MAX_COMBINED_ATOMIC_COUNTER_BUFFERS = 0x92D1;
    int GL_MAX_VERTEX_ATOMIC_COUNTERS = 0x92D2;
    int GL_MAX_FRAGMENT_ATOMIC_COUNTERS = 0x92D6;
    int GL_MAX_COMBINED_ATOMIC_COUNTERS = 0x92D7;
    int GL_MAX_ATOMIC_COUNTER_BUFFER_SIZE = 0x92D8;
    int GL_ACTIVE_ATOMIC_COUNTER_BUFFERS = 0x92D9;
    int GL_UNSIGNED_INT_ATOMIC_COUNTER = 0x92DB;
    int GL_MAX_ATOMIC_COUNTER_BUFFER_BINDINGS = 0x92DC;
    int GL_UNIFORM = 0x92E1;
    int GL_UNIFORM_BLOCK = 0x92E2;
    int GL_PROGRAM_INPUT = 0x92E3;
    int GL_PROGRAM_OUTPUT = 0x92E4;
    int GL_BUFFER_VARIABLE = 0x92E5;
    int GL_SHADER_STORAGE_BLOCK = 0x92E6;
    int GL_TRANSFORM_FEEDBACK_VARYING = 0x92F4;
    int GL_ACTIVE_RESOURCES = 0x92F5;
    int GL_MAX_NAME_LENGTH = 0x92F6;
    int GL_MAX_NUM_ACTIVE_VARIABLES = 0x92F7;
    int GL_NAME_LENGTH = 0x92F9;
    int GL_TYPE = 0x92FA;
    int GL_ARRAY_SIZE = 0x92FB;
    int GL_OFFSET = 0x92FC;
    int GL_BLOCK_INDEX = 0x92FD;
    int GL_ARRAY_STRIDE = 0x92FE;
    int GL_MATRIX_STRIDE = 0x92FF;
    int GL_IS_ROW_MAJOR = 0x9300;
    int GL_ATOMIC_COUNTER_BUFFER_INDEX = 0x9301;
    int GL_BUFFER_BINDING = 0x9302;
    int GL_BUFFER_DATA_SIZE = 0x9303;
    int GL_NUM_ACTIVE_VARIABLES = 0x9304;
    int GL_ACTIVE_VARIABLES = 0x9305;
    int GL_REFERENCED_BY_VERTEX_SHADER = 0x9306;
    int GL_REFERENCED_BY_FRAGMENT_SHADER = 0x930A;
    int GL_REFERENCED_BY_COMPUTE_SHADER = 0x930B;
    int GL_TOP_LEVEL_ARRAY_SIZE = 0x930C;
    int GL_TOP_LEVEL_ARRAY_STRIDE = 0x930D;
    int GL_LOCATION = 0x930E;
    int GL_FRAMEBUFFER_DEFAULT_WIDTH = 0x9310;
    int GL_FRAMEBUFFER_DEFAULT_HEIGHT = 0x9311;
    int GL_FRAMEBUFFER_DEFAULT_SAMPLES = 0x9313;
    int GL_FRAMEBUFFER_DEFAULT_FIXED_SAMPLE_LOCATIONS = 0x9314;
    int GL_MAX_FRAMEBUFFER_WIDTH = 0x9315;
    int GL_MAX_FRAMEBUFFER_HEIGHT = 0x9316;
    int GL_MAX_FRAMEBUFFER_SAMPLES = 0x9318;

    // C function void glDispatchCompute ( GLuint num_groups_x, GLuint num_groups_y, GLuint num_groups_z )

    void glDispatchCompute(int num_groups_x, int num_groups_y, int num_groups_z);

    // C function void glDispatchComputeIndirect ( GLintptr indirect );

    void glDispatchComputeIndirect(long indirect);

    // C function void glDrawArraysIndirect ( GLenum mode, const void *indirect );

    void glDrawArraysIndirect(int mode, long indirect);

    // C function glDrawElementsIndirect ( GLenum mode, GLenum type, const void *indirect );

    void glDrawElementsIndirect(int mode, int type, long indirect);

    // C function void glFramebufferParameteri ( GLenum target, GLenum pname, GLint param )

    void glFramebufferParameteri(int target, int pname, int param);

    // C function void glGetFramebufferParameteriv ( GLenum target, GLenum pname, GLint *params )

// void glGetFramebufferParameteriv(
// int target,
// int pname,
// int[] params,
// int offset
// );

    // C function void glGetFramebufferParameteriv ( GLenum target, GLenum pname, GLint *params )

    void glGetFramebufferParameteriv(int target, int pname, java.nio.IntBuffer params);

    // C function void glGetProgramInterfaceiv ( GLuint program, GLenum programInterface, GLenum pname, GLint *params )

// void glGetProgramInterfaceiv(
// int program,
// int programInterface,
// int pname,
// int[] params,
// int offset
// );

    // C function void glGetProgramInterfaceiv ( GLuint program, GLenum programInterface, GLenum pname, GLint *params )

    void glGetProgramInterfaceiv(int program, int programInterface, int pname, java.nio.IntBuffer params);

    // C function GLuint glGetProgramResourceIndex ( GLuint program, GLenum programInterface, const GLchar *name )

    int glGetProgramResourceIndex(int program, int programInterface, String name);

    // C function void glGetProgramResourceName ( GLuint program, GLenum programInterface, GLuint index, GLsizei bufSize, GLsizei
    // *length, GLchar *name )

    String glGetProgramResourceName(int program, int programInterface, int index);

    // C function void glGetProgramResourceiv ( GLuint program, GLenum programInterface, GLuint index, GLsizei propCount, const
    // GLenum *props, GLsizei bufSize, GLsizei *length, GLint *params )

// void glGetProgramResourceiv(
// int program,
// int programInterface,
// int index,
// int propCount,
// int[] props,
// int propsOffset,
// int bufSize,
// int[] length,
// int lengthOffset,
// int[] params,
// int paramsOffset
// );

    // C function void glGetProgramResourceiv ( GLuint program, GLenum programInterface, GLuint index, GLsizei propCount, const
    // GLenum *props, GLsizei bufSize, GLsizei *length, GLint *params )

    void glGetProgramResourceiv(int program, int programInterface, int index,
// int propCount,
                                java.nio.IntBuffer props,
// int bufSize,
                                java.nio.IntBuffer length, java.nio.IntBuffer params);

    // C function GLint glGetProgramResourceLocation ( GLuint program, GLenum programInterface, const GLchar *name )

    int glGetProgramResourceLocation(int program, int programInterface, String name);

    // C function void glUseProgramStages ( GLuint pipeline, GLbitfield stages, GLuint program )

    void glUseProgramStages(int pipeline, int stages, int program);

    // C function void glActiveShaderProgram ( GLuint pipeline, GLuint program )

    void glActiveShaderProgram(int pipeline, int program);

    // C function GLuint glCreateShaderProgramv ( GLenum type, GLsizei count, const GLchar *const *strings )

    int glCreateShaderProgramv(int type, String[] strings);

    // C function void glBindProgramPipeline ( GLuint pipeline )

    void glBindProgramPipeline(int pipeline);

    // C function void glDeleteProgramPipelines ( GLsizei n, const GLuint *pipelines )

// void glDeleteProgramPipelines(
// int n,
// int[] pipelines,
// int offset
// );

    // C function void glDeleteProgramPipelines ( GLsizei n, const GLuint *pipelines )

    void glDeleteProgramPipelines(int n, java.nio.IntBuffer pipelines);

    // C function void glGenProgramPipelines ( GLsizei n, GLuint *pipelines )

// void glGenProgramPipelines(
// int n,
// int[] pipelines,
// int offset
// );

    // C function void glGenProgramPipelines ( GLsizei n, GLuint *pipelines )

    void glGenProgramPipelines(int n, java.nio.IntBuffer pipelines);

    // C function GLboolean glIsProgramPipeline ( GLuint pipeline )

    boolean glIsProgramPipeline(int pipeline);

    // C function void glGetProgramPipelineiv ( GLuint pipeline, GLenum pname, GLint *params )

// void glGetProgramPipelineiv(
// int pipeline,
// int pname,
// int[] params,
// int offset
// );

    // C function void glGetProgramPipelineiv ( GLuint pipeline, GLenum pname, GLint *params )

    void glGetProgramPipelineiv(int pipeline, int pname, java.nio.IntBuffer params);

    // C function void glProgramUniform1i ( GLuint program, GLint location, GLint v0 )

    void glProgramUniform1i(int program, int location, int v0);

    // C function void glProgramUniform2i ( GLuint program, GLint location, GLint v0, GLint v1 )

    void glProgramUniform2i(int program, int location, int v0, int v1);

    // C function void glProgramUniform3i ( GLuint program, GLint location, GLint v0, GLint v1, GLint v2 )

    void glProgramUniform3i(int program, int location, int v0, int v1, int v2);

    // C function void glProgramUniform4i ( GLuint program, GLint location, GLint v0, GLint v1, GLint v2, GLint v3 )

    void glProgramUniform4i(int program, int location, int v0, int v1, int v2, int v3);

    // C function void glProgramUniform1ui ( GLuint program, GLint location, GLuint v0 )

    void glProgramUniform1ui(int program, int location, int v0);

    // C function void glProgramUniform2ui ( GLuint program, GLint location, GLuint v0, GLuint v1 )

    void glProgramUniform2ui(int program, int location, int v0, int v1);

    // C function void glProgramUniform3ui ( GLuint program, GLint location, GLuint v0, GLuint v1, GLuint v2 )

    void glProgramUniform3ui(int program, int location, int v0, int v1, int v2);

    // C function void glProgramUniform4ui ( GLuint program, GLint location, GLuint v0, GLuint v1, GLuint v2, GLuint v3 )

    void glProgramUniform4ui(int program, int location, int v0, int v1, int v2, int v3);

    // C function void glProgramUniform1f ( GLuint program, GLint location, GLfloat v0 )

    void glProgramUniform1f(int program, int location, float v0);

    // C function void glProgramUniform2f ( GLuint program, GLint location, GLfloat v0, GLfloat v1 )

    void glProgramUniform2f(int program, int location, float v0, float v1);

    // C function void glProgramUniform3f ( GLuint program, GLint location, GLfloat v0, GLfloat v1, GLfloat v2 )

    void glProgramUniform3f(int program, int location, float v0, float v1, float v2);

    // C function void glProgramUniform4f ( GLuint program, GLint location, GLfloat v0, GLfloat v1, GLfloat v2, GLfloat v3 )

    void glProgramUniform4f(int program, int location, float v0, float v1, float v2, float v3);

    // C function void glProgramUniform1iv ( GLuint program, GLint location, GLsizei count, const GLint *value )

// void glProgramUniform1iv(
// int program,
// int location,
// int count,
// int[] value,
// int offset
// );

    // C function void glProgramUniform1iv ( GLuint program, GLint location, GLsizei count, const GLint *value )

    void glProgramUniform1iv(int program, int location,
// int count,
                             java.nio.IntBuffer value);

    // C function void glProgramUniform2iv ( GLuint program, GLint location, GLsizei count, const GLint *value )

// void glProgramUniform2iv(
// int program,
// int location,
// int count,
// int[] value,
// int offset
// );

    // C function void glProgramUniform2iv ( GLuint program, GLint location, GLsizei count, const GLint *value )

    void glProgramUniform2iv(int program, int location,
// int count,
                             java.nio.IntBuffer value);

    // C function void glProgramUniform3iv ( GLuint program, GLint location, GLsizei count, const GLint *value )

// void glProgramUniform3iv(
// int program,
// int location,
// int count,
// int[] value,
// int offset
// );

    // C function void glProgramUniform3iv ( GLuint program, GLint location, GLsizei count, const GLint *value )

    void glProgramUniform3iv(int program, int location,
// int count,
                             java.nio.IntBuffer value);

    // C function void glProgramUniform4iv ( GLuint program, GLint location, GLsizei count, const GLint *value )

// void glProgramUniform4iv(
// int program,
// int location,
// int count,
// int[] value,
// int offset
// );

    // C function void glProgramUniform4iv ( GLuint program, GLint location, GLsizei count, const GLint *value )

    void glProgramUniform4iv(int program, int location,
// int count,
                             java.nio.IntBuffer value);

    // C function void glProgramUniform1uiv ( GLuint program, GLint location, GLsizei count, const GLuint *value )

// void glProgramUniform1uiv(
// int program,
// int location,
// int count,
// int[] value,
// int offset
// );

    // C function void glProgramUniform1uiv ( GLuint program, GLint location, GLsizei count, const GLuint *value )

    void glProgramUniform1uiv(int program, int location,
// int count,
                              java.nio.IntBuffer value);

    // C function void glProgramUniform2uiv ( GLuint program, GLint location, GLsizei count, const GLuint *value )

// void glProgramUniform2uiv(
// int program,
// int location,
// int count,
// int[] value,
// int offset
// );

    // C function void glProgramUniform2uiv ( GLuint program, GLint location, GLsizei count, const GLuint *value )

    void glProgramUniform2uiv(int program, int location,
// int count,
                              java.nio.IntBuffer value);

    // C function void glProgramUniform3uiv ( GLuint program, GLint location, GLsizei count, const GLuint *value )

// void glProgramUniform3uiv(
// int program,
// int location,
// int count,
// int[] value,
// int offset
// );

    // C function void glProgramUniform3uiv ( GLuint program, GLint location, GLsizei count, const GLuint *value )

    void glProgramUniform3uiv(int program, int location,
// int count,
                              java.nio.IntBuffer value);

    // C function void glProgramUniform4uiv ( GLuint program, GLint location, GLsizei count, const GLuint *value )

// void glProgramUniform4uiv(
// int program,
// int location,
// int count,
// int[] value,
// int offset
// );

    // C function void glProgramUniform4uiv ( GLuint program, GLint location, GLsizei count, const GLuint *value )

    void glProgramUniform4uiv(int program, int location,
// int count,
                              java.nio.IntBuffer value);

    // C function void glProgramUniform1fv ( GLuint program, GLint location, GLsizei count, const GLfloat *value )

// void glProgramUniform1fv(
// int program,
// int location,
// int count,
// float[] value,
// int offset
// );

    // C function void glProgramUniform1fv ( GLuint program, GLint location, GLsizei count, const GLfloat *value )

    void glProgramUniform1fv(int program, int location,
// int count,
                             java.nio.FloatBuffer value);

    // C function void glProgramUniform2fv ( GLuint program, GLint location, GLsizei count, const GLfloat *value )

// void glProgramUniform2fv(
// int program,
// int location,
// int count,
// float[] value,
// int offset
// );

    // C function void glProgramUniform2fv ( GLuint program, GLint location, GLsizei count, const GLfloat *value )

    void glProgramUniform2fv(int program, int location,
// int count,
                             java.nio.FloatBuffer value);

    // C function void glProgramUniform3fv ( GLuint program, GLint location, GLsizei count, const GLfloat *value )

// void glProgramUniform3fv(
// int program,
// int location,
// int count,
// float[] value,
// int offset
// );

    // C function void glProgramUniform3fv ( GLuint program, GLint location, GLsizei count, const GLfloat *value )

    void glProgramUniform3fv(int program, int location,
// int count,
                             java.nio.FloatBuffer value);

    // C function void glProgramUniform4fv ( GLuint program, GLint location, GLsizei count, const GLfloat *value )

// void glProgramUniform4fv(
// int program,
// int location,
// int count,
// float[] value,
// int offset
// );

    // C function void glProgramUniform4fv ( GLuint program, GLint location, GLsizei count, const GLfloat *value )

    void glProgramUniform4fv(int program, int location,
// int count,
                             java.nio.FloatBuffer value);

    // C function void glProgramUniformMatrix2fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const
    // GLfloat *value )

// void glProgramUniformMatrix2fv(
// int program,
// int location,
// int count,
// boolean transpose,
// float[] value,
// int offset
// );

    // C function void glProgramUniformMatrix2fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const
    // GLfloat *value )

    void glProgramUniformMatrix2fv(int program, int location,
// int count,
                                   boolean transpose, java.nio.FloatBuffer value);

    // C function void glProgramUniformMatrix3fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const
    // GLfloat *value )

// void glProgramUniformMatrix3fv(
// int program,
// int location,
// int count,
// boolean transpose,
// float[] value,
// int offset
// );

    // C function void glProgramUniformMatrix3fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const
    // GLfloat *value )

    void glProgramUniformMatrix3fv(int program, int location,
// int count,
                                   boolean transpose, java.nio.FloatBuffer value);

    // C function void glProgramUniformMatrix4fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const
    // GLfloat *value )

// void glProgramUniformMatrix4fv(
// int program,
// int location,
// int count,
// boolean transpose,
// float[] value,
// int offset
// );

    // C function void glProgramUniformMatrix4fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const
    // GLfloat *value )

    void glProgramUniformMatrix4fv(int program, int location,
// int count,
                                   boolean transpose, java.nio.FloatBuffer value);

    // C function void glProgramUniformMatrix2x3fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const
    // GLfloat *value )

// void glProgramUniformMatrix2x3fv(
// int program,
// int location,
// int count,
// boolean transpose,
// float[] value,
// int offset
// );

    // C function void glProgramUniformMatrix2x3fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const
    // GLfloat *value )

    void glProgramUniformMatrix2x3fv(int program, int location,
// int count,
                                     boolean transpose, java.nio.FloatBuffer value);

    // C function void glProgramUniformMatrix3x2fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const
    // GLfloat *value )

// void glProgramUniformMatrix3x2fv(
// int program,
// int location,
// int count,
// boolean transpose,
// float[] value,
// int offset
// );

    // C function void glProgramUniformMatrix3x2fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const
    // GLfloat *value )

    void glProgramUniformMatrix3x2fv(int program, int location,
// int count,
                                     boolean transpose, java.nio.FloatBuffer value);

    // C function void glProgramUniformMatrix2x4fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const
    // GLfloat *value )

// void glProgramUniformMatrix2x4fv(
// int program,
// int location,
// int count,
// boolean transpose,
// float[] value,
// int offset
// );

    // C function void glProgramUniformMatrix2x4fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const
    // GLfloat *value )

    void glProgramUniformMatrix2x4fv(int program, int location,
// int count,
                                     boolean transpose, java.nio.FloatBuffer value);

    // C function void glProgramUniformMatrix4x2fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const
    // GLfloat *value )

// void glProgramUniformMatrix4x2fv(
// int program,
// int location,
// int count,
// boolean transpose,
// float[] value,
// int offset
// );

    // C function void glProgramUniformMatrix4x2fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const
    // GLfloat *value )

    void glProgramUniformMatrix4x2fv(int program, int location,
// int count,
                                     boolean transpose, java.nio.FloatBuffer value);

    // C function void glProgramUniformMatrix3x4fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const
    // GLfloat *value )

// void glProgramUniformMatrix3x4fv(
// int program,
// int location,
// int count,
// boolean transpose,
// float[] value,
// int offset
// );

    // C function void glProgramUniformMatrix3x4fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const
    // GLfloat *value )

    void glProgramUniformMatrix3x4fv(int program, int location,
// int count,
                                     boolean transpose, java.nio.FloatBuffer value);

    // C function void glProgramUniformMatrix4x3fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const
    // GLfloat *value )

// void glProgramUniformMatrix4x3fv(
// int program,
// int location,
// int count,
// boolean transpose,
// float[] value,
// int offset
// );

    // C function void glProgramUniformMatrix4x3fv ( GLuint program, GLint location, GLsizei count, GLboolean transpose, const
    // GLfloat *value )

    void glProgramUniformMatrix4x3fv(int program, int location,
// int count,
                                     boolean transpose, java.nio.FloatBuffer value);

    // C function void glValidateProgramPipeline ( GLuint pipeline )

    void glValidateProgramPipeline(int pipeline);

    // C function void glGetProgramPipelineInfoLog( GLuint program, GLsizei maxLength, GLsizei * length, GLchar * infoLog);

    String glGetProgramPipelineInfoLog(int program);

    // C function void glBindImageTexture ( GLuint unit, GLuint texture, GLint level, GLboolean layered, GLint layer, GLenum
    // access, GLenum format )

    void glBindImageTexture(int unit, int texture, int level, boolean layered, int layer, int access, int format);

    // C function void glGetBooleani_v ( GLenum target, GLuint index, GLboolean *data )

// void glGetBooleani_v(
// int target,
// int index,
// boolean[] data,
// int offset
// );

    // C function void glGetBooleani_v ( GLenum target, GLuint index, GLboolean *data )

    void glGetBooleani_v(int target, int index, java.nio.IntBuffer data);

    // C function void glMemoryBarrier ( GLbitfield barriers )

    void glMemoryBarrier(int barriers);

    // C function void glMemoryBarrierByRegion ( GLbitfield barriers )

    void glMemoryBarrierByRegion(int barriers);

    // C function void glTexStorage2DMultisample ( GLenum target, GLsizei samples, GLenum internalformat, GLsizei width, GLsizei
    // height, GLboolean fixedsamplelocations )

    void glTexStorage2DMultisample(int target, int samples, int internalformat, int width, int height,
                                   boolean fixedsamplelocations);

    // C function void glGetMultisamplefv ( GLenum pname, GLuint index, GLfloat *val )

// void glGetMultisamplefv(
// int pname,
// int index,
// float[] val,
// int offset
// );

    // C function void glGetMultisamplefv ( GLenum pname, GLuint index, GLfloat *val )

    void glGetMultisamplefv(int pname, int index, java.nio.FloatBuffer val);

    // C function void glSampleMaski ( GLuint maskNumber, GLbitfield mask )

    void glSampleMaski(int maskNumber, int mask);

    // C function void glGetTexLevelParameteriv ( GLenum target, GLint level, GLenum pname, GLint *params )

// void glGetTexLevelParameteriv(
// int target,
// int level,
// int pname,
// int[] params,
// int offset
// );

    // C function void glGetTexLevelParameteriv ( GLenum target, GLint level, GLenum pname, GLint *params )

    void glGetTexLevelParameteriv(int target, int level, int pname, java.nio.IntBuffer params);

    // C function void glGetTexLevelParameterfv ( GLenum target, GLint level, GLenum pname, GLfloat *params )

// void glGetTexLevelParameterfv(
// int target,
// int level,
// int pname,
// float[] params,
// int offset
// );

    // C function void glGetTexLevelParameterfv ( GLenum target, GLint level, GLenum pname, GLfloat *params )

    void glGetTexLevelParameterfv(int target, int level, int pname, java.nio.FloatBuffer params);

    // C function void glBindVertexBuffer ( GLuint bindingindex, GLuint buffer, GLintptr offset, GLsizei stride )

    void glBindVertexBuffer(int bindingindex, int buffer, long offset, int stride);

    // C function void glVertexAttribFormat ( GLuint attribindex, GLint size, GLenum type, GLboolean normalized, GLuint
    // relativeoffset )

    void glVertexAttribFormat(int attribindex, int size, int type, boolean normalized, int relativeoffset);

    // C function void glVertexAttribIFormat ( GLuint attribindex, GLint size, GLenum type, GLuint relativeoffset )

    void glVertexAttribIFormat(int attribindex, int size, int type, int relativeoffset);

    // C function void glVertexAttribBinding ( GLuint attribindex, GLuint bindingindex )

    void glVertexAttribBinding(int attribindex, int bindingindex);

    // C function void glVertexBindingDivisor ( GLuint bindingindex, GLuint divisor )

    void glVertexBindingDivisor(int bindingindex, int divisor);
}
