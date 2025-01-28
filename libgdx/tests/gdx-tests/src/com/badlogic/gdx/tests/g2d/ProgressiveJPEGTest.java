package com.badlogic.gdx.tests.g2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Demonstrates support of progressive JPEG.
 */
public class ProgressiveJPEGTest extends GdxTest {
    private SpriteBatch batch;
    private Drawable drawable;
    private Texture texture;

    @Override
    public void create() {
        batch = new SpriteBatch();

        texture = new Texture(Gdx.files.internal("data/g2d/progressive-libgdx.jpg"));
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        drawable = new TextureRegionDrawable(new TextureRegion(texture));
    }

    @Override
    public void render() {
        ScreenUtils.clear(1, 1, 1, 1);

        batch.begin();
        drawable.draw(batch, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
    }

    @Override
    public void dispose() {
        texture.dispose();
        batch.dispose();
    }
}
