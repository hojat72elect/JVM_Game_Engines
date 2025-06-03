package org.oreon.gl.components.ui;

import org.oreon.core.math.Vec2f;
import org.oreon.core.model.Mesh;
import org.oreon.core.util.BufferUtil;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class GUIVAO {

    private final int vbo;
    private final int ibo;
    private final int vaoId;
    private final int offset;
    private int indices;
    private int vertices;

    public GUIVAO() {
        vbo = glGenBuffers();
        ibo = glGenBuffers();
        vaoId = glGenVertexArrays();
        indices = 0;
        vertices = 0;
        offset = 24;
    }

    public void addData(Mesh mesh) {
        indices = mesh.getIndices().length;
        vertices = mesh.getVertices().length;

        glBindVertexArray(vaoId);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, BufferUtil.createFlippedBufferSOA(mesh.getVertices()), GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, BufferUtil.createFlippedBuffer(mesh.getIndices()), GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, (long) vertices * offset);

        glBindVertexArray(0);
    }

    public void draw() {

        glBindVertexArray(vaoId);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, indices, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
    }

    public void update(Vec2f[] texCoords) {
        glBindVertexArray(vaoId);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, (long) vertices * offset, BufferUtil.createFlippedBuffer(texCoords));
        glBindVertexArray(0);
    }

    public void update(List<Vec2f> texCoords) {
        glBindVertexArray(vaoId);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, (long) vertices * offset, BufferUtil.createFlippedBuffer(texCoords));
        glBindVertexArray(0);
    }
}
