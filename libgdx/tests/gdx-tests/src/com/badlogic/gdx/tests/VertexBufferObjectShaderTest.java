package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.IndexBufferObject;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexBufferObject;
import com.badlogic.gdx.graphics.glutils.VertexBufferObjectWithVAO;
import com.badlogic.gdx.graphics.glutils.VertexData;
import com.badlogic.gdx.tests.utils.GdxTest;

public class VertexBufferObjectShaderTest extends GdxTest {
    Texture texture;
    ShaderProgram shader;
    VertexData vbo;
    IndexBufferObject indices;

    @Override
    public void dispose() {
        texture.dispose();
        vbo.dispose();
        indices.dispose();
        shader.dispose();
    }

    @Override
    public void render() {
        GL20 gl = Gdx.gl20;
        gl.glViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
        Gdx.gl.glClearColor(0.7f, 0, 0, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shader.bind();
        shader.setUniformi("u_texture", 0);
        texture.bind();
        vbo.bind(shader);
        indices.bind();
        gl.glDrawElements(GL20.GL_TRIANGLES, 3, GL20.GL_UNSIGNED_SHORT, indices.getBuffer(false).position());
        indices.unbind();
        vbo.unbind(shader);
    }

    @Override
    public void create() {
        //@off
        String vertexShader =
                "attribute vec4 a_position;    \n"
                        + "attribute vec4 a_color;\n"
                        + "attribute vec2 a_texCoords;\n"
                        + "varying vec4 v_color;"
                        + "varying vec2 v_texCoords;" + "void main()                  \n"
                        + "{                            \n"
                        + "   v_color = vec4(a_color.x, a_color.y, a_color.z, 1); \n"
                        + "   v_texCoords = a_texCoords; \n"
                        + "   gl_Position =  a_position;  \n"
                        + "}                            \n";
        String fragmentShader =
                "#ifdef GL_ES\n"
                        + "precision mediump float;\n"
                        + "#endif\n"
                        + "varying vec4 v_color;\n"
                        + "varying vec2 v_texCoords;\n"
                        + "uniform sampler2D u_texture;\n"
                        + "void main()                                  \n"
                        + "{                                            \n"
                        + "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n"
                        + "}";
        //@on

        shader = new ShaderProgram(vertexShader, fragmentShader);
        if (Gdx.gl30 != null) {
            vbo = new VertexBufferObjectWithVAO(true, 3, new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"),
                    new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoords"),
                    new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_color"));
        } else {

            vbo = new VertexBufferObject(true, 3, new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"),
                    new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoords"),
                    new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_color"));
        }
        float[] vertices = new float[]{-1, -1, 0, 0, Color.toFloatBits(1f, 0f, 0f, 1f), 0, 1, 0.5f, 1.0f,
                Color.toFloatBits(0f, 1f, 0f, 1f), 1, -1, 1, 0, Color.toFloatBits(0f, 0f, 1f, 1f)};
        vbo.setVertices(vertices, 0, vertices.length);
        indices = new IndexBufferObject(3);
        indices.setIndices(new short[]{0, 1, 2}, 0, 3);

        texture = new Texture(Gdx.files.internal("data/badlogic.jpg"));
    }

    @Override
    public void resume() {
        vbo.invalidate();
    }
}
