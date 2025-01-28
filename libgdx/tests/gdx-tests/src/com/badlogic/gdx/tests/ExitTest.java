package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.tests.utils.GdxTest;

public class ExitTest extends GdxTest {

    @Override
    public void render() {
        if (Gdx.input.justTouched()) Gdx.app.exit();
    }

    @Override
    public void pause() {
        Gdx.app.log("ExitTest", "paused");
    }

    @Override
    public void dispose() {
        Gdx.app.log("ExitTest", "disposed");
    }
}
