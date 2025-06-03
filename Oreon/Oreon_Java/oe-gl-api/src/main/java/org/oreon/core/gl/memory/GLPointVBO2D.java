package org.oreon.core.gl.memory;

import org.oreon.core.math.Vec2f;
import org.oreon.core.util.BufferUtil;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class GLPointVBO2D implements VBO {

    private final int vbo;
    private final int vaoId;
    private int size;


    public GLPointVBO2D() {
        vbo = glGenBuffers();
        vaoId = glGenVertexArrays();
        size = 0;
    }

    public void addData(Vec2f[] points) {
        size = points.length;

        glBindVertexArray(vaoId);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, BufferUtil.createFlippedBuffer(points), GL_STATIC_DRAW);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, Float.BYTES * 2, 0);

        glBindVertexArray(0);
    }


    public void draw() {
        glBindVertexArray(vaoId);

        glEnableVertexAttribArray(0);

        glDrawArrays(GL_POINTS, 0, size);

        glDisableVertexAttribArray(0);

        glBindVertexArray(0);
    }

    public void delete() {
        glBindVertexArray(vaoId);
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vaoId);
        glBindVertexArray(0);
    }
}
