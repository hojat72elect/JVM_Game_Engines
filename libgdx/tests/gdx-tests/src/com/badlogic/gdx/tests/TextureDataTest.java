package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.tests.utils.GdxTest;

public class TextureDataTest extends GdxTest {
    private SpriteBatch spriteBatch;
    private Texture texture;

    public void create() {
        spriteBatch = new SpriteBatch();
// texture = new Texture(new PixmapTextureData(new Pixmap(Gdx.files.internal("data/t8890.png")), null, false, true));
        texture = new Texture(new FileTextureData(Gdx.files.internal("data/t8890.png"), null, null, false));
    }

    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        spriteBatch.draw(texture, 100, 100);
        spriteBatch.end();
    }

    public boolean needsGL20() {
        return false;
    }
}
