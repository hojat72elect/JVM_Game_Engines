package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.tests.utils.GdxTest;

public class RotationTest extends GdxTest {

    Texture texture;
    TextureRegion region;
    SpriteBatch batch;

    @Override
    public void create() {
        texture = new Texture(Gdx.files.internal("data/black_marked_0.png"));
        region = new TextureRegion(texture);
        batch = new SpriteBatch();
        batch.getTransformMatrix().setToTranslation(30.5f, 30.5f, 0);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(texture, 0, 0);
        batch.draw(region, 128, 0, 64, 64, 128, 128, 1, 1, 90);
        batch.draw(region, 128, 128, 64, 64, 128, 128, 1, 1, 180);
        batch.draw(region, 0, 128, 64, 64, 128, 128, 1, 1, 270);
        batch.end();
    }

    @Override
    public void dispose() {
        texture.dispose();
        batch.dispose();
    }
}
