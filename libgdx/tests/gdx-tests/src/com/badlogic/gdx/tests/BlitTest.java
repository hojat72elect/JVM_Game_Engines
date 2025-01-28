package com.badlogic.gdx.tests;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.ScreenUtils;

public class BlitTest extends GdxTest {

    Texture rgb888;
    Texture rgba8888;
    Texture psRgb888;
    Texture psRgba8888;
    SpriteBatch batch;

    public void create() {
        rgb888 = new Texture("data/bobrgb888-32x32.png");
        rgba8888 = new Texture("data/bobargb8888-32x32.png");
        psRgb888 = new Texture("data/alpha.png");
        psRgba8888 = new Texture("data/rgb.png");
        batch = new SpriteBatch();
    }

    public void render() {
        ScreenUtils.clear(0.4f, 0.4f, 0.4f, 1);

        batch.begin();
        batch.draw(rgb888, 0, 0);
        batch.draw(rgba8888, 60, 0);
        batch.draw(psRgb888, 0, 60);
        batch.draw(psRgba8888, psRgb888.getWidth() + 20, 60);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        rgb888.dispose();
        rgba8888.dispose();
        psRgb888.dispose();
        psRgba8888.dispose();
    }
}
