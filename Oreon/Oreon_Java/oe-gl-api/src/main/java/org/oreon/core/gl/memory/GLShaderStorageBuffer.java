package org.oreon.core.gl.memory;

import org.oreon.core.math.Vec2f;
import org.oreon.core.util.BufferUtil;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

/**
 * Shader Storage Buffer Object
 */

public class GLShaderStorageBuffer {

    private final int ssbo;

    public GLShaderStorageBuffer() {
        ssbo = glGenBuffers();
    }

    public void addData(Vec2f[] data) {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        glBufferData(GL_SHADER_STORAGE_BUFFER, BufferUtil.createFlippedBuffer(data), GL_STATIC_READ);
    }

    public void addData(int[] data) {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        glBufferData(GL_SHADER_STORAGE_BUFFER, BufferUtil.createFlippedBuffer(data), GL_STATIC_READ);
    }

    public void addData(float[] data) {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        glBufferData(GL_SHADER_STORAGE_BUFFER, BufferUtil.createFlippedBuffer(data), GL_STATIC_READ);
    }

    public void addData(ByteBuffer data) {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        glBufferData(GL_SHADER_STORAGE_BUFFER, data, GL_STATIC_READ);
    }

    public void bindBufferBase(int index) {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, index, ssbo);
    }

}
