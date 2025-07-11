package com.badlogic.gdx.backends.android;

import android.opengl.GLES30;

import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class AndroidGL30 extends AndroidGL20 implements GL30 {
    @Override
    public void glReadBuffer(int mode) {
        GLES30.glReadBuffer(mode);
    }

    @Override
    public void glDrawRangeElements(int mode, int start, int end, int count, int type, java.nio.Buffer indices) {
        GLES30.glDrawRangeElements(mode, start, end, count, type, indices);
    }

    @Override
    public void glDrawRangeElements(int mode, int start, int end, int count, int type, int offset) {
        GLES30.glDrawRangeElements(mode, start, end, count, type, offset);
    }

    @Override
    public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type,
                             int offset) {
        if (offset != 0) throw new GdxRuntimeException("non zero offset is not supported");
        GLES30.glTexImage2D(target, level, internalformat, width, height, border, format, type, null);
    }

    @Override
    public void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format,
                             int type, java.nio.Buffer pixels) {
        if (pixels == null)
            GLES30.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, 0);
        else
            GLES30.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, pixels);
    }

    @Override
    public void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format,
                             int type, int offset) {
        GLES30.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, offset);
    }

    @Override
    public void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type,
                                int offset) {
        if (offset != 0) throw new GdxRuntimeException("non zero offset is not supported");
        GLES30.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, null);
    }

    @Override
    public void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth,
                                int format, int type, java.nio.Buffer pixels) {
        GLES30.glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, pixels);
    }

    @Override
    public void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth,
                                int format, int type, int offset) {
        GLES30.glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, offset);
    }

    @Override
    public void glCopyTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int x, int y, int width,
                                    int height) {
        GLES30.glCopyTexSubImage3D(target, level, xoffset, yoffset, zoffset, x, y, width, height);
    }

    @Override
    public void glGenQueries(int n, int[] ids, int offset) {
        GLES30.glGenQueries(n, ids, offset);
    }

    @Override
    public void glGenQueries(int n, java.nio.IntBuffer ids) {
        GLES30.glGenQueries(n, ids);
    }

    @Override
    public void glDeleteQueries(int n, int[] ids, int offset) {
        GLES30.glDeleteQueries(n, ids, offset);
    }

    @Override
    public void glDeleteQueries(int n, java.nio.IntBuffer ids) {
        GLES30.glDeleteQueries(n, ids);
    }

    @Override
    public boolean glIsQuery(int id) {
        return GLES30.glIsQuery(id);
    }

    @Override
    public void glBeginQuery(int target, int id) {
        GLES30.glBeginQuery(target, id);
    }

    @Override
    public void glEndQuery(int target) {
        GLES30.glEndQuery(target);
    }

    @Override
    public void glGetQueryiv(int target, int pname, java.nio.IntBuffer params) {
        GLES30.glGetQueryiv(target, pname, params);
    }

    @Override
    public void glGetQueryObjectuiv(int id, int pname, java.nio.IntBuffer params) {
        GLES30.glGetQueryObjectuiv(id, pname, params);
    }

    @Override
    public boolean glUnmapBuffer(int target) {
        return GLES30.glUnmapBuffer(target);
    }

    @Override
    public java.nio.Buffer glGetBufferPointerv(int target, int pname) {
        return GLES30.glGetBufferPointerv(target, pname);
    }

    @Override
    public void glDrawBuffers(int n, java.nio.IntBuffer bufs) {
        GLES30.glDrawBuffers(n, bufs);
    }

    @Override
    public void glUniformMatrix2x3fv(int location, int count, boolean transpose, java.nio.FloatBuffer value) {
        GLES30.glUniformMatrix2x3fv(location, count, transpose, value);
    }

    @Override
    public void glUniformMatrix3x2fv(int location, int count, boolean transpose, java.nio.FloatBuffer value) {
        GLES30.glUniformMatrix3x2fv(location, count, transpose, value);
    }

    @Override
    public void glUniformMatrix2x4fv(int location, int count, boolean transpose, java.nio.FloatBuffer value) {
        GLES30.glUniformMatrix2x4fv(location, count, transpose, value);
    }

    @Override
    public void glUniformMatrix4x2fv(int location, int count, boolean transpose, java.nio.FloatBuffer value) {
        GLES30.glUniformMatrix4x2fv(location, count, transpose, value);
    }

    @Override
    public void glUniformMatrix3x4fv(int location, int count, boolean transpose, java.nio.FloatBuffer value) {
        GLES30.glUniformMatrix3x4fv(location, count, transpose, value);
    }

    @Override
    public void glUniformMatrix4x3fv(int location, int count, boolean transpose, java.nio.FloatBuffer value) {
        GLES30.glUniformMatrix4x3fv(location, count, transpose, value);
    }

    @Override
    public void glBlitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1,
                                  int mask, int filter) {
        GLES30.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
    }

    @Override
    public void glRenderbufferStorageMultisample(int target, int samples, int internalformat, int width, int height) {
        GLES30.glRenderbufferStorageMultisample(target, samples, internalformat, width, height);
    }

    @Override
    public void glFramebufferTextureLayer(int target, int attachment, int texture, int level, int layer) {
        GLES30.glFramebufferTextureLayer(target, attachment, texture, level, layer);
    }

    @Override
    public java.nio.Buffer glMapBufferRange(int target, int offset, int length, int access) {
        return GLES30.glMapBufferRange(target, offset, length, access);
    }

    @Override
    public void glFlushMappedBufferRange(int target, int offset, int length) {
        GLES30.glFlushMappedBufferRange(target, offset, length);
    }

    @Override
    public void glBindVertexArray(int array) {
        GLES30.glBindVertexArray(array);
    }

    @Override
    public void glDeleteVertexArrays(int n, int[] arrays, int offset) {
        GLES30.glDeleteVertexArrays(n, arrays, offset);
    }

    @Override
    public void glDeleteVertexArrays(int n, java.nio.IntBuffer arrays) {
        GLES30.glDeleteVertexArrays(n, arrays);
    }

    @Override
    public void glGenVertexArrays(int n, int[] arrays, int offset) {
        GLES30.glGenVertexArrays(n, arrays, offset);
    }

    @Override
    public void glGenVertexArrays(int n, java.nio.IntBuffer arrays) {
        GLES30.glGenVertexArrays(n, arrays);
    }

    @Override
    public boolean glIsVertexArray(int array) {
        return GLES30.glIsVertexArray(array);
    }

    @Override
    public void glBeginTransformFeedback(int primitiveMode) {
        GLES30.glBeginTransformFeedback(primitiveMode);
    }

    @Override
    public void glEndTransformFeedback() {
        GLES30.glEndTransformFeedback();
    }

    @Override
    public void glBindBufferRange(int target, int index, int buffer, int offset, int size) {
        GLES30.glBindBufferRange(target, index, buffer, offset, size);
    }

    @Override
    public void glBindBufferBase(int target, int index, int buffer) {
        GLES30.glBindBufferBase(target, index, buffer);
    }

    @Override
    public void glTransformFeedbackVaryings(int program, String[] varyings, int bufferMode) {
        GLES30.glTransformFeedbackVaryings(program, varyings, bufferMode);
    }

    @Override
    public void glVertexAttribIPointer(int index, int size, int type, int stride, int offset) {
        GLES30.glVertexAttribIPointer(index, size, type, stride, offset);
    }

    @Override
    public void glGetVertexAttribIiv(int index, int pname, java.nio.IntBuffer params) {
        GLES30.glGetVertexAttribIiv(index, pname, params);
    }

    @Override
    public void glGetVertexAttribIuiv(int index, int pname, java.nio.IntBuffer params) {
        GLES30.glGetVertexAttribIuiv(index, pname, params);
    }

    @Override
    public void glVertexAttribI4i(int index, int x, int y, int z, int w) {
        GLES30.glVertexAttribI4i(index, x, y, z, w);
    }

    @Override
    public void glVertexAttribI4ui(int index, int x, int y, int z, int w) {
        GLES30.glVertexAttribI4ui(index, x, y, z, w);
    }

    @Override
    public void glGetUniformuiv(int program, int location, java.nio.IntBuffer params) {
        GLES30.glGetUniformuiv(program, location, params);
    }

    @Override
    public int glGetFragDataLocation(int program, String name) {
        return GLES30.glGetFragDataLocation(program, name);
    }

    @Override
    public void glUniform1uiv(int location, int count, java.nio.IntBuffer value) {
        GLES30.glUniform1uiv(location, count, value);
    }

    @Override
    public void glUniform3uiv(int location, int count, java.nio.IntBuffer value) {
        GLES30.glUniform3uiv(location, count, value);
    }

    @Override
    public void glUniform4uiv(int location, int count, java.nio.IntBuffer value) {
        GLES30.glUniform4uiv(location, count, value);
    }

    @Override
    public void glClearBufferiv(int buffer, int drawbuffer, java.nio.IntBuffer value) {
        GLES30.glClearBufferiv(buffer, drawbuffer, value);
    }

    @Override
    public void glClearBufferuiv(int buffer, int drawbuffer, java.nio.IntBuffer value) {
        GLES30.glClearBufferuiv(buffer, drawbuffer, value);
    }

    @Override
    public void glClearBufferfv(int buffer, int drawbuffer, java.nio.FloatBuffer value) {
        GLES30.glClearBufferfv(buffer, drawbuffer, value);
    }

    @Override
    public void glClearBufferfi(int buffer, int drawbuffer, float depth, int stencil) {
        GLES30.glClearBufferfi(buffer, drawbuffer, depth, stencil);
    }

    @Override
    public String glGetStringi(int name, int index) {
        return GLES30.glGetStringi(name, index);
    }

    @Override
    public void glCopyBufferSubData(int readTarget, int writeTarget, int readOffset, int writeOffset, int size) {
        GLES30.glCopyBufferSubData(readTarget, writeTarget, readOffset, writeOffset, size);
    }

    @Override
    public void glGetUniformIndices(int program, String[] uniformNames, java.nio.IntBuffer uniformIndices) {
        GLES30.glGetUniformIndices(program, uniformNames, uniformIndices);
    }

    @Override
    public void glGetActiveUniformsiv(int program, int uniformCount, java.nio.IntBuffer uniformIndices, int pname,
                                      java.nio.IntBuffer params) {
        GLES30.glGetActiveUniformsiv(program, uniformCount, uniformIndices, pname, params);
    }

    @Override
    public int glGetUniformBlockIndex(int program, String uniformBlockName) {
        return GLES30.glGetUniformBlockIndex(program, uniformBlockName);
    }

    @Override
    public void glGetActiveUniformBlockiv(int program, int uniformBlockIndex, int pname, java.nio.IntBuffer params) {
        GLES30.glGetActiveUniformBlockiv(program, uniformBlockIndex, pname, params);
    }

    @Override
    public void glGetActiveUniformBlockName(int program, int uniformBlockIndex, java.nio.Buffer length,
                                            java.nio.Buffer uniformBlockName) {
        GLES30.glGetActiveUniformBlockName(program, uniformBlockIndex, length, uniformBlockName);
    }

    @Override
    public String glGetActiveUniformBlockName(int program, int uniformBlockIndex) {
        return GLES30.glGetActiveUniformBlockName(program, uniformBlockIndex);
    }

    @Override
    public void glUniformBlockBinding(int program, int uniformBlockIndex, int uniformBlockBinding) {
        GLES30.glUniformBlockBinding(program, uniformBlockIndex, uniformBlockBinding);
    }

    @Override
    public void glDrawArraysInstanced(int mode, int first, int count, int instanceCount) {
        GLES30.glDrawArraysInstanced(mode, first, count, instanceCount);
    }

    @Override
    public void glDrawElementsInstanced(int mode, int count, int type, int indicesOffset, int instanceCount) {
        GLES30.glDrawElementsInstanced(mode, count, type, indicesOffset, instanceCount);
    }

    @Override
    public void glGetInteger64v(int pname, java.nio.LongBuffer params) {
        GLES30.glGetInteger64v(pname, params);
    }

    @Override
    public void glGetBufferParameteri64v(int target, int pname, java.nio.LongBuffer params) {
        GLES30.glGetBufferParameteri64v(target, pname, params);
    }

    @Override
    public void glGenSamplers(int count, int[] samplers, int offset) {
        GLES30.glGenSamplers(count, samplers, offset);
    }

    @Override
    public void glGenSamplers(int count, java.nio.IntBuffer samplers) {
        GLES30.glGenSamplers(count, samplers);
    }

    @Override
    public void glDeleteSamplers(int count, int[] samplers, int offset) {
        GLES30.glDeleteSamplers(count, samplers, offset);
    }

    @Override
    public void glDeleteSamplers(int count, java.nio.IntBuffer samplers) {
        GLES30.glDeleteSamplers(count, samplers);
    }

    @Override
    public boolean glIsSampler(int sampler) {
        return GLES30.glIsSampler(sampler);
    }

    @Override
    public void glBindSampler(int unit, int sampler) {
        GLES30.glBindSampler(unit, sampler);
    }

    @Override
    public void glSamplerParameteri(int sampler, int pname, int param) {
        GLES30.glSamplerParameteri(sampler, pname, param);
    }

    @Override
    public void glSamplerParameteriv(int sampler, int pname, java.nio.IntBuffer param) {
        GLES30.glSamplerParameteriv(sampler, pname, param);
    }

    @Override
    public void glSamplerParameterf(int sampler, int pname, float param) {
        GLES30.glSamplerParameterf(sampler, pname, param);
    }

    @Override
    public void glSamplerParameterfv(int sampler, int pname, java.nio.FloatBuffer param) {
        GLES30.glSamplerParameterfv(sampler, pname, param);
    }

    @Override
    public void glGetSamplerParameteriv(int sampler, int pname, java.nio.IntBuffer params) {
        GLES30.glGetSamplerParameteriv(sampler, pname, params);
    }

    @Override
    public void glGetSamplerParameterfv(int sampler, int pname, java.nio.FloatBuffer params) {
        GLES30.glGetSamplerParameterfv(sampler, pname, params);
    }

    @Override
    public void glVertexAttribDivisor(int index, int divisor) {
        GLES30.glVertexAttribDivisor(index, divisor);
    }

    @Override
    public void glBindTransformFeedback(int target, int id) {
        GLES30.glBindTransformFeedback(target, id);
    }

    @Override
    public void glDeleteTransformFeedbacks(int n, int[] ids, int offset) {
        GLES30.glDeleteTransformFeedbacks(n, ids, offset);
    }

    @Override
    public void glDeleteTransformFeedbacks(int n, java.nio.IntBuffer ids) {
        GLES30.glDeleteTransformFeedbacks(n, ids);
    }

    @Override
    public void glGenTransformFeedbacks(int n, int[] ids, int offset) {
        GLES30.glGenTransformFeedbacks(n, ids, offset);
    }

    @Override
    public void glGenTransformFeedbacks(int n, java.nio.IntBuffer ids) {
        GLES30.glGenTransformFeedbacks(n, ids);
    }

    @Override
    public boolean glIsTransformFeedback(int id) {
        return GLES30.glIsTransformFeedback(id);
    }

    @Override
    public void glPauseTransformFeedback() {
        GLES30.glPauseTransformFeedback();
    }

    @Override
    public void glResumeTransformFeedback() {
        GLES30.glResumeTransformFeedback();
    }

    @Override
    public void glProgramParameteri(int program, int pname, int value) {
        GLES30.glProgramParameteri(program, pname, value);
    }

    @Override
    public void glInvalidateFramebuffer(int target, int numAttachments, java.nio.IntBuffer attachments) {
        GLES30.glInvalidateFramebuffer(target, numAttachments, attachments);
    }

    @Override
    public void glInvalidateSubFramebuffer(int target, int numAttachments, java.nio.IntBuffer attachments, int x, int y,
                                           int width, int height) {
        GLES30.glInvalidateSubFramebuffer(target, numAttachments, attachments, x, y, width, height);
    }
}
