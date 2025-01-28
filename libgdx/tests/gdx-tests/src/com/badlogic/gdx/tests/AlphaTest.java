package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.tests.utils.GdxTest;

public class AlphaTest extends GdxTest {
    SpriteBatch batch;
    Texture texture;

    @Override
    public void create() {
        Pixmap pixmap = new Pixmap(256, 256, Format.RGBA8888);
        pixmap.setColor(1, 0, 0, 1);
        pixmap.fill();

        texture = new Texture(pixmap, false);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        batch = new SpriteBatch();
        pixmap.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        int color = pixmap.getPixel(0, pixmap.getHeight() - 1);
        Gdx.app.log("AlphaTest", Integer.toHexString(color));
        pixmap.dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
        texture.dispose();
    }
}
