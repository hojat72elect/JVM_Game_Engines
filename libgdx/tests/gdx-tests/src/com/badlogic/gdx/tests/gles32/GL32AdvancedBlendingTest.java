package com.badlogic.gdx.tests.gles32;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.tests.utils.GdxTestConfig;
import com.badlogic.gdx.utils.ScreenUtils;

@GdxTestConfig(requireGL32 = true)
public class GL32AdvancedBlendingTest extends GdxTest {
    // see https://www.khronos.org/registry/OpenGL-Refpages/es3/html/glBlendEquation.xhtml
    static int[] modes = {
            GL32.GL_MULTIPLY,
            GL32.GL_SCREEN,
            GL32.GL_OVERLAY,
            GL32.GL_DARKEN,
            GL32.GL_LIGHTEN,
            GL32.GL_COLORDODGE,
            GL32.GL_COLORBURN,
            GL32.GL_HARDLIGHT,
            GL32.GL_SOFTLIGHT,
            GL32.GL_DIFFERENCE,
            GL32.GL_EXCLUSION
    };
    int mode = 0;
    private Texture texture;
    private SpriteBatch batch;

    public void create() {
        texture = new Texture(Gdx.files.internal("data/badlogic.jpg"));
        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0, 0, 1, 1);
    }

    @Override
    public void dispose() {
        texture.dispose();
        batch.dispose();
    }

    @Override
    public void render() {
        if (Gdx.input.justTouched()) {
            mode = (mode + 1) % modes.length;
        }

        ScreenUtils.clear(Color.CLEAR);
        batch.begin();

        batch.draw(texture, 0, 0, 1, 1);

        batch.flush();

        Gdx.gl.glBlendEquation(modes[mode]);
        batch.draw(texture, 0, 0, .5f, .5f);

        batch.end();

        Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
    }
}
