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
    void draw(Batch batch, float x, float y, float width, float height);

    float getLeftWidth();

    void setLeftWidth(float leftWidth);

    float getRightWidth();

    void setRightWidth(float rightWidth);

    float getTopHeight();

    void setTopHeight(float topHeight);

    float getBottomHeight();

    void setBottomHeight(float bottomHeight);

    float getMinWidth();

    void setMinWidth(float minWidth);

    float getMinHeight();

    void setMinHeight(float minHeight);
}
