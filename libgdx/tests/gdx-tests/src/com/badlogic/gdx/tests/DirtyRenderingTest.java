package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Demonstrates how to use non-continuous (aka dirty-only) rendering. The application will clear the screen with a random color
 * every frame it renders. Rendering requests are issued automatically if new input events arrive.
 */
public class DirtyRenderingTest extends GdxTest {
    @Override
    public void create() {
        // disable continuous rendering
        Gdx.graphics.setContinuousRendering(false);
        Gdx.app.log("DirtyRenderingTest", "created");
    }

    @Override
    public void resume() {
        Gdx.app.log("DirtyRenderingTest", "resumed");
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("DirtyRenderingTest", "resized");
    }

    @Override
    public void pause() {
        Gdx.app.log("DirtyRenderingTest", "paused");
    }

    @Override
    public void dispose() {
        Gdx.app.log("DirtyRenderingTest", "disposed");
    }

    @Override
    public void render() {
        ScreenUtils.clear(MathUtils.random(), MathUtils.random(), MathUtils.random(), MathUtils.random());
    }
}
