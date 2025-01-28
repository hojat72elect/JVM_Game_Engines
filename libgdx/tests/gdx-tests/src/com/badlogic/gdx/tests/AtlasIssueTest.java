package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.tests.utils.GdxTest;

public class AtlasIssueTest extends GdxTest {
    SpriteBatch batch;
    Sprite sprite;
    TextureAtlas atlas;
    BitmapFont font;

    public void create() {
        batch = new SpriteBatch();
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, 855, 480));
        atlas = new TextureAtlas(Gdx.files.internal("data/issue_pack"), Gdx.files.internal("data/"));
        sprite = atlas.createSprite("map");
        font = new BitmapFont(Gdx.files.internal("data/font.fnt"), Gdx.files.internal("data/font.png"), false);
        Gdx.gl.glClearColor(0, 1, 0, 1);
    }

    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        sprite.draw(batch);
        font.draw(batch, "fps:" + Gdx.graphics.getFramesPerSecond(), 26, 65);
        batch.end();
    }

    public boolean needsGL20() {
        return false;
    }

    @Override
    public void dispose() {
        batch.dispose();
        atlas.dispose();
        font.dispose();
    }
}
