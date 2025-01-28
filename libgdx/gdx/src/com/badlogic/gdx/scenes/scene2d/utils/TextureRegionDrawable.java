package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Drawable for a {@link TextureRegion}.
 */
public class TextureRegionDrawable extends BaseDrawable implements TransformDrawable {
    private TextureRegion region;

    /**
     * Creates an uninitialized TextureRegionDrawable. The texture region must be set before use.
     */
    public TextureRegionDrawable() {
    }

    public TextureRegionDrawable(Texture texture) {
        setRegion(new TextureRegion(texture));
    }

    public TextureRegionDrawable(TextureRegion region) {
        setRegion(region);
    }

    public TextureRegionDrawable(TextureRegionDrawable drawable) {
        super(drawable);
        setRegion(drawable.region);
    }

    public void draw(Batch batch, float x, float y, float width, float height) {
        batch.draw(region, x, y, width, height);
    }

    public void draw(Batch batch, float x, float y, float originX, float originY, float width, float height, float scaleX,
                     float scaleY, float rotation) {
        batch.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
    }

    public TextureRegion getRegion() {
        return region;
    }

    public void setRegion(TextureRegion region) {
        this.region = region;
        if (region != null) {
            setMinWidth(region.getRegionWidth());
            setMinHeight(region.getRegionHeight());
        }
    }

    /**
     * Creates a new drawable that renders the same as this drawable tinted the specified color.
     */
    public Drawable tint(Color tint) {
        Sprite sprite;
        if (region instanceof AtlasRegion)
            sprite = new AtlasSprite((AtlasRegion) region);
        else
            sprite = new Sprite(region);
        sprite.setColor(tint);
        sprite.setSize(getMinWidth(), getMinHeight());
        SpriteDrawable drawable = new SpriteDrawable(sprite);
        drawable.setLeftWidth(getLeftWidth());
        drawable.setRightWidth(getRightWidth());
        drawable.setTopHeight(getTopHeight());
        drawable.setBottomHeight(getBottomHeight());
        return drawable;
    }
}
