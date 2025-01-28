package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.tests.utils.GdxTest;

public class AccelerometerTest extends GdxTest {
    BitmapFont font;
    SpriteBatch batch;

    @Override
    public void create() {
        font = new BitmapFont(Gdx.files.internal("data/lsans-15.fnt"), false);
        batch = new SpriteBatch();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        font.draw(batch,
                "accel: [" + Gdx.input.getAccelerometerX() + "," + Gdx.input.getAccelerometerY() + "," + Gdx.input.getAccelerometerZ()
                        + "]\n" + "gyros: [" + Gdx.input.getGyroscopeX() + "," + Gdx.input.getGyroscopeY() + "," + Gdx.input.getGyroscopeZ()
                        + "]\n" + "orientation: " + Gdx.input.getNativeOrientation() + "\n" + "rotation: " + Gdx.input.getRotation() + "\n"
                        + "wh: " + Gdx.graphics.getDisplayMode() + "\n",
                0, 100);
        batch.end();
    }

    @Override
    public void dispose() {
        font.dispose();
        batch.dispose();
    }
}
