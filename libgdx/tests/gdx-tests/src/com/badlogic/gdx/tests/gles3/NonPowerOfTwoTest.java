package com.badlogic.gdx.tests.gles3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Added during WebGL2 implementation. Non-POT mipmap texture support was added in WebGL2 This test renders a non-POT texture. On
 * WebGL1 you will receive a non-POT error.
 */
public class NonPowerOfTwoTest extends GdxTest {
    SpriteBatch batch;
    Texture texture;

    @Override
    public void create() {
        batch = new SpriteBatch();
        texture = new Texture(Gdx.files.internal("data/heightmap.png"), true);
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();
        batch.draw(texture, 0, 0, texture.getWidth(), texture.getHeight());
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

    @Override
    public void dispose() {
        texture.dispose();
        batch.dispose();
    }
}
