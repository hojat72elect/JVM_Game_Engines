package com.badlogic.gdx.tests.gles31;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.GL31;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.tests.utils.GdxTestConfig;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;

import java.nio.IntBuffer;

/**
 * see https://www.khronos.org/opengl/wiki/Vertex_Rendering#Indirect_rendering
 * <p>
 * Example of indirect commands. Note that commands could be defined directly in GPU via a comput shader. Also note that multi
 * draw (glMultiDrawArraysIndirect) requires an extension to GLES 3.1
 */
@GdxTestConfig(requireGL31 = true)
public class GL31IndirectDrawingNonIndexedTest extends GdxTest {
    private static final int commandInts = 4;
    private static final int commandStride = commandInts * 4;
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
    int nbCommands = 2;
    private int drawCommands;
    private Mesh mesh;
    private ShaderProgram shader;
    private final Matrix4 transform = new Matrix4();
    private float time;

    @Override
    public void create() {

        drawCommands = Gdx.gl.glGenBuffer();
        IntBuffer buffer = BufferUtils.newIntBuffer(commandInts * nbCommands);
        buffer.put(new int[]{ //
                3, // count
                1, // primCount
                0, // first
                0 // reserved
        });

        buffer.put(new int[]{ //
                3, // count
                1, // primCount
                3, // first
                0 // reserved
        });
        buffer.flip();

        Gdx.gl.glBindBuffer(GL31.GL_DRAW_INDIRECT_BUFFER, drawCommands);
        Gdx.gl.glBufferData(GL31.GL_DRAW_INDIRECT_BUFFER, nbCommands * commandStride, buffer, GL30.GL_DYNAMIC_DRAW);
        Gdx.gl.glBindBuffer(GL31.GL_DRAW_INDIRECT_BUFFER, 0);

        mesh = new Mesh(true, 6, 0, VertexAttribute.Position(), VertexAttribute.ColorUnpacked());
        mesh.setVertices(new float[]{ //
                0, 0, 0, 1, 1, 1, 1, //
                1, 0, 0, 1, 1, 1, 1, //
                0, 1, 0, 1, 1, 1, 1, //

                0, 1, 0, 1, 1, 1, 1, //
                1, 0, 0, 1, 1, 1, 1, //
                1, 1, 0, 1, 1, 1, 1, //
        });

        shader = new ShaderProgram(vsCode, fsCode);
        if (!shader.isCompiled()) throw new GdxRuntimeException(shader.getLog());
    }

    @Override
    public void dispose() {
        shader.dispose();
        mesh.dispose();
        Gdx.gl.glDeleteBuffer(drawCommands);
    }

    @Override
    public void render() {
        time += Gdx.graphics.getDeltaTime();
        int commandIndex = (int) time % nbCommands;

        ScreenUtils.clear(Color.CLEAR, true);

        shader.bind();
        transform.setToOrtho2D(-1, -1, 3, 3);
        shader.setUniformMatrix("u_projTrans", transform);

        mesh.bind(shader);

        Gdx.gl.glBindBuffer(GL31.GL_DRAW_INDIRECT_BUFFER, drawCommands);
        Gdx.gl31.glDrawArraysIndirect(GL20.GL_TRIANGLES, (long) commandStride * commandIndex);
        mesh.unbind(shader);
    }
}
