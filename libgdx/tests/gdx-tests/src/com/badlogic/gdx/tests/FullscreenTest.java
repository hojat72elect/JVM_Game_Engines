package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.ScreenUtils;

public class FullscreenTest extends GdxTest {
    SpriteBatch batch;
    Texture tex;
    boolean fullscreen = false;
    BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        tex = new Texture(Gdx.files.internal("data/badlogic.jpg"));
        DisplayMode[] modes = Gdx.graphics.getDisplayModes();
        for (DisplayMode mode : modes) {
            System.out.println(mode);
        }
        Gdx.app.log("FullscreenTest", Gdx.graphics.getBufferFormat().toString());
    }

    @Override
    public void resume() {

    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);

        batch.begin();
        batch.setColor(Gdx.input.getX() < Gdx.graphics.getSafeInsetLeft()
                || Gdx.input.getX() + tex.getWidth() > Gdx.graphics.getWidth() - Gdx.graphics.getSafeInsetRight() ? Color.RED
                : Color.WHITE);
        batch.draw(tex, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        font.draw(batch, Gdx.graphics.getWidth() + ", " + Gdx.graphics.getHeight(), 0, 20);
        batch.end();

        if (Gdx.input.justTouched()) {
            if (fullscreen) {
                Gdx.graphics.setWindowedMode(480, 320);
                batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                Gdx.gl.glViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
                fullscreen = false;
            } else {
                DisplayMode m = null;
                for (DisplayMode mode : Gdx.graphics.getDisplayModes()) {
                    if (m == null) {
                        m = mode;
                    } else {
                        if (m.width < mode.width) {
                            m = mode;
                        }
                    }
                }

                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                Gdx.gl.glViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
                fullscreen = true;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("FullscreenTest", "resized: " + width + ", " + height);
        Gdx.app.log("FullscreenTest", "safe insets: " + Gdx.graphics.getSafeInsetLeft() + "/" + Gdx.graphics.getSafeInsetRight());
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

    @Override
    public void pause() {
        Gdx.app.log("FullscreenTest", "paused");
    }

    @Override
    public void dispose() {
        Gdx.app.log("FullscreenTest", "disposed");
    }
}
