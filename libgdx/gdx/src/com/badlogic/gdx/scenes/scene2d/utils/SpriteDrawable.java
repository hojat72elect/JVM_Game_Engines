package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;

/**
 * Drawable for a {@link Sprite}.
 */
public class SpriteDrawable extends BaseDrawable implements TransformDrawable {
    private Sprite sprite;

    /**
     * Creates an uninitialized SpriteDrawable. The sprite must be set before use.
     */
    public SpriteDrawable() {
    }

    public SpriteDrawable(Sprite sprite) {
        setSprite(sprite);
    }

    public SpriteDrawable(SpriteDrawable drawable) {
        super(drawable);
        setSprite(drawable.sprite);
    }

    public void draw(Batch batch, float x, float y, float width, float height) {
        Color spriteColor = sprite.getColor();
        float oldColor = sprite.getPackedColor();
        sprite.setColor(spriteColor.mul(batch.getColor()));

        sprite.setRotation(0);
        sprite.setScale(1, 1);
        sprite.setBounds(x, y, width, height);
        sprite.draw(batch);

        sprite.setPackedColor(oldColor);
    }

    public void draw(Batch batch, float x, float y, float originX, float originY, float width, float height, float scaleX,
                     float scaleY, float rotation) {

        Color spriteColor = sprite.getColor();
        float oldColor = sprite.getPackedColor();
        sprite.setColor(spriteColor.mul(batch.getColor()));

        sprite.setOrigin(originX, originY);
        sprite.setRotation(rotation);
        sprite.setScale(scaleX, scaleY);
        sprite.setBounds(x, y, width, height);
        sprite.draw(batch);

        sprite.setPackedColor(oldColor);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        setMinWidth(sprite.getWidth());
        setMinHeight(sprite.getHeight());
    }

    /**
     * Creates a new drawable that renders the same as this drawable tinted the specified color.
     */
    public SpriteDrawable tint(Color tint) {
        Sprite newSprite;
        if (sprite instanceof AtlasSprite)
            newSprite = new AtlasSprite((AtlasSprite) sprite);
        else
            newSprite = new Sprite(sprite);
        newSprite.setColor(tint);
        newSprite.setSize(getMinWidth(), getMinHeight());
        SpriteDrawable drawable = new SpriteDrawable(newSprite);
        drawable.setLeftWidth(getLeftWidth());
        drawable.setRightWidth(getRightWidth());
        drawable.setTopHeight(getTopHeight());
        drawable.setBottomHeight(getBottomHeight());
        return drawable;
    }
}
