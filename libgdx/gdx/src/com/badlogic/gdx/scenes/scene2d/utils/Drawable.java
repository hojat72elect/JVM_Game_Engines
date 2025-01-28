package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * A drawable knows how to draw itself at a given rectangular size. It provides padding sizes and a minimum size so that other
 * code can determine how to size and position content.
 */
public interface Drawable {
    /**
     * Draws this drawable at the specified bounds. The drawable should be tinted with {@link Batch#getColor()}, possibly by
     * mixing its own color.
     */
    public void draw(Batch batch, float x, float y, float width, float height);

    public float getLeftWidth();

    public void setLeftWidth(float leftWidth);

    public float getRightWidth();

    public void setRightWidth(float rightWidth);

    public float getTopHeight();

    public void setTopHeight(float topHeight);

    public float getBottomHeight();

    public void setBottomHeight(float bottomHeight);

    public float getMinWidth();

    public void setMinWidth(float minWidth);

    public float getMinHeight();

    public void setMinHeight(float minHeight);
}
