package org.oreon.core.gl.pipeline;

import lombok.Getter;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindBufferBase;

public abstract class VertexInputBuffer {

    @Getter
    private final int handle;
    private int target;

    public VertexInputBuffer(int target) {

        handle = glGenBuffers();
    }

    public void bind() {

        glBindBuffer(target, handle);
    }

    public void unbind() {

        glBindBuffer(target, 0);
    }

    public void bindBufferBase(int binding) {

        glBindBufferBase(target, binding, handle);
    }

    public void allocate(ByteBuffer data, int usage) {

        glBufferData(target, data, usage);
    }

    public void delete() {

        glDeleteBuffers(handle);
    }
}
