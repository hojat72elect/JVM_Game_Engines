package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.TimeUtils;

public class DeltaTimeTest extends GdxTest {

    long lastFrameTime;

    @Override
    public void create() {
        lastFrameTime = TimeUtils.nanoTime();
    }

    @Override
    public void render() {
        long frameTime = TimeUtils.nanoTime();
        float deltaTime = (frameTime - lastFrameTime) / 1000000000.0f;
        lastFrameTime = frameTime;

        Gdx.app.log("DeltaTimeTest", "delta: " + deltaTime + ", gdx delta: " + Gdx.graphics.getDeltaTime());
    }
}
