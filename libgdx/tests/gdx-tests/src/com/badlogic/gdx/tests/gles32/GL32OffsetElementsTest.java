package com.badlogic.gdx.tests.gles32;

import java.nio.ShortBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.tests.utils.GdxTestConfig;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;

@GdxTestConfig(requireGL32 = true)
public class GL32OffsetElementsTest extends GdxTest {
    static String vsCode = "attribute vec4 a_position;\n" + //
            "attribute vec4 a_color;\n" + //
            "uniform mat4 u_projTrans;\n" + //
            "varying vec4 v_color;\n" + //
            "void main(){\n" + //
            "    v_color = a_color;\n" + //
            "    gl_Position =  u_projTrans * a_position;\n" + //
            "}"; //
    static String fsCode = "varying vec4 v_color;\n" + //
            "void main(){\n" + //
            "    gl_FragColor = v_color;\n" + //
            "}"; //
    private ShortBuffer indices;
    private Mesh mesh;
    private ShaderProgram shader;
    private Matrix4 transform = new Matrix4();
    private float time;

    public void create() {
        indices = BufferUtils.newShortBuffer(6);
        indices.put(new short[]{
                // @off
                0, 1, 2,
                2, 1, 3
                // @on
        });
        indices.flip();

        mesh = new Mesh(true, 6, 0, VertexAttribute.Position(), VertexAttribute.ColorUnpacked());
        mesh.setVertices(new float[]{
                // @off
                0, 0, 0, 1, 1, 1, 1,
                1, 0, 0, 1, 1, 1, 1,
                0, 1, 0, 1, 1, 1, 1,
                1, 1, 0, 1, 1, 1, 1,
                0, 2, 0, 1, 1, 1, 1,
                1, 2, 0, 1, 1, 1, 1
                // @on
        });

        shader = new ShaderProgram(vsCode, fsCode);
        if (!shader.isCompiled()) throw new GdxRuntimeException(shader.getLog());
    }

    @Override
    public void dispose() {
        mesh.dispose();
        shader.dispose();
    }

    @Override
    public void render() {
        time += Gdx.graphics.getDeltaTime();

        int baseVertex = ((int) time) % 3;

        ScreenUtils.clear(Color.CLEAR);

        shader.bind();
        transform.setToOrtho2D(-4, -4, 8, 8);
        shader.setUniformMatrix("u_projTrans", transform);

        mesh.bind(shader);
        Gdx.gl32.glDrawElementsBaseVertex(GL20.GL_TRIANGLES, 6, GL20.GL_UNSIGNED_SHORT, indices, baseVertex);
        mesh.unbind(shader);
    }
}
