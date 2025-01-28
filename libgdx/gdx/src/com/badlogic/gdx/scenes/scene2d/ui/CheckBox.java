package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.Scaling;

/**
 * A checkbox is a button that contains an image indicating the checked or unchecked state and a label.
 */
public class CheckBox extends TextButton {
    private Image image;
    private Cell imageCell;
    private CheckBoxStyle style;

    public CheckBox(@Null String text, Skin skin) {
        this(text, skin.get(CheckBoxStyle.class));
    }

    public CheckBox(@Null String text, Skin skin, String styleName) {
        this(text, skin.get(styleName, CheckBoxStyle.class));
    }

    public CheckBox(@Null String text, CheckBoxStyle style) {
        super(text, style);

        Label label = getLabel();
        label.setAlignment(Align.left);

        image = newImage();
        image.setDrawable(style.checkboxOff);

        clearChildren();
        imageCell = add(image);
        add(label);
        setSize(getPrefWidth(), getPrefHeight());
    }

    protected Image newImage() {
        return new Image((Drawable) null, Scaling.none);
    }

    /**
     * Returns the checkbox's style. Modifying the returned style may not have an effect until {@link #setStyle(ButtonStyle)} is
     * called.
     */
    public CheckBoxStyle getStyle() {
        return style;
    }

    public void setStyle(ButtonStyle style) {
        if (!(style instanceof CheckBoxStyle)) throw new IllegalArgumentException("style must be a CheckBoxStyle.");
        this.style = (CheckBoxStyle) style;
        super.setStyle(style);
    }

    public void draw(Batch batch, float parentAlpha) {
        image.setDrawable(getImageDrawable());
        super.draw(batch, parentAlpha);
    }

    protected @Null Drawable getImageDrawable() {
        if (isDisabled()) {
            if (isChecked && style.checkboxOnDisabled != null) return style.checkboxOnDisabled;
            return style.checkboxOffDisabled;
        }
        boolean over = isOver() && !isDisabled();
        if (isChecked && style.checkboxOn != null)
            return over && style.checkboxOnOver != null ? style.checkboxOnOver : style.checkboxOn;
        if (over && style.checkboxOver != null) return style.checkboxOver;
        return style.checkboxOff;
    }

    public Image getImage() {
        return image;
    }

    public Cell getImageCell() {
        return imageCell;
    }

    /**
     * The style for a select box, see {@link CheckBox}.
     */
    static public class CheckBoxStyle extends TextButtonStyle {
        public Drawable checkboxOn, checkboxOff;
        public @Null Drawable checkboxOnOver, checkboxOver, checkboxOnDisabled, checkboxOffDisabled;

        public CheckBoxStyle() {
        }

        public CheckBoxStyle(Drawable checkboxOff, Drawable checkboxOn, BitmapFont font, @Null Color fontColor) {
            this.checkboxOff = checkboxOff;
            this.checkboxOn = checkboxOn;
            this.font = font;
            this.fontColor = fontColor;
        }

        public CheckBoxStyle(CheckBoxStyle style) {
            super(style);
            checkboxOff = style.checkboxOff;
            checkboxOn = style.checkboxOn;

            checkboxOnOver = style.checkboxOnOver;
            checkboxOver = style.checkboxOver;
            checkboxOnDisabled = style.checkboxOnDisabled;
            checkboxOffDisabled = style.checkboxOffDisabled;
        }
    }
}
