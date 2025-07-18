package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import org.jetbrains.annotations.Nullable;

/**
 * A button with a child {@link Image} and {@link Label}.
 *
 * @see ImageButton
 * @see TextButton
 * @see Button
 */
public class ImageTextButton extends Button {
    private final Image image;
    private Label label;
    private ImageTextButtonStyle style;

    public ImageTextButton(@Nullable String text, Skin skin) {
        this(text, skin.get(ImageTextButtonStyle.class));
        setSkin(skin);
    }

    public ImageTextButton(@Nullable String text, Skin skin, String styleName) {
        this(text, skin.get(styleName, ImageTextButtonStyle.class));
        setSkin(skin);
    }

    public ImageTextButton(@Nullable String text, ImageTextButtonStyle style) {
        super(style);
        this.style = style;

        defaults().space(3);

        image = newImage();

        label = newLabel(text, new LabelStyle(style.font, style.fontColor));
        label.setAlignment(Align.center);

        add(image);
        add(label);

        setStyle(style);

        setSize(getPrefWidth(), getPrefHeight());
    }

    protected Image newImage() {
        return new Image(null, Scaling.fit);
    }

    protected Label newLabel(String text, LabelStyle style) {
        return new Label(text, style);
    }

    public ImageTextButtonStyle getStyle() {
        return style;
    }

    public void setStyle(ButtonStyle style) {
        if (!(style instanceof ImageTextButtonStyle))
            throw new IllegalArgumentException("style must be a ImageTextButtonStyle.");
        this.style = (ImageTextButtonStyle) style;
        super.setStyle(style);

        if (image != null) updateImage();

        if (label != null) {
            ImageTextButtonStyle textButtonStyle = (ImageTextButtonStyle) style;
            LabelStyle labelStyle = label.getStyle();
            labelStyle.font = textButtonStyle.font;
            labelStyle.fontColor = getFontColor();
            label.setStyle(labelStyle);
        }
    }

    /**
     * Returns the appropriate image drawable from the style based on the current button state.
     */
    protected @Nullable Drawable getImageDrawable() {
        if (isDisabled() && style.imageDisabled != null) return style.imageDisabled;
        if (isPressed()) {
            if (isChecked() && style.imageCheckedDown != null) return style.imageCheckedDown;
            if (style.imageDown != null) return style.imageDown;
        }
        if (isOver()) {
            if (isChecked()) {
                if (style.imageCheckedOver != null) return style.imageCheckedOver;
            } else {
                if (style.imageOver != null) return style.imageOver;
            }
        }
        if (isChecked()) {
            if (style.imageChecked != null) return style.imageChecked;
            if (isOver() && style.imageOver != null) return style.imageOver;
        }
        return style.imageUp;
    }

    /**
     * Sets the image drawable based on the current button state. The default implementation sets the image drawable using
     * {@link #getImageDrawable()}.
     */
    protected void updateImage() {
        image.setDrawable(getImageDrawable());
    }

    /**
     * Returns the appropriate label font color from the style based on the current button state.
     */
    protected @Nullable Color getFontColor() {
        if (isDisabled() && style.disabledFontColor != null) return style.disabledFontColor;
        if (isPressed()) {
            if (isChecked() && style.checkedDownFontColor != null) return style.checkedDownFontColor;
            if (style.downFontColor != null) return style.downFontColor;
        }
        if (isOver()) {
            if (isChecked()) {
                if (style.checkedOverFontColor != null) return style.checkedOverFontColor;
            } else {
                if (style.overFontColor != null) return style.overFontColor;
            }
        }
        boolean focused = hasKeyboardFocus();
        if (isChecked()) {
            if (focused && style.checkedFocusedFontColor != null) return style.checkedFocusedFontColor;
            if (style.checkedFontColor != null) return style.checkedFontColor;
            if (isOver() && style.overFontColor != null) return style.overFontColor;
        }
        if (focused && style.focusedFontColor != null) return style.focusedFontColor;
        return style.fontColor;
    }

    public void draw(Batch batch, float parentAlpha) {
        updateImage();
        label.getStyle().fontColor = getFontColor();
        super.draw(batch, parentAlpha);
    }

    public Image getImage() {
        return image;
    }

    public Cell getImageCell() {
        return getCell(image);
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        getLabelCell().setActor(label);
        this.label = label;
    }

    public Cell getLabelCell() {
        return getCell(label);
    }

    public CharSequence getText() {
        return label.getText();
    }

    public void setText(CharSequence text) {
        label.setText(text);
    }

    public String toString() {
        String name = getName();
        if (name != null) return name;
        String className = getClass().getName();
        int dotIndex = className.lastIndexOf('.');
        if (dotIndex != -1) className = className.substring(dotIndex + 1);
        return (className.indexOf('$') != -1 ? "ImageTextButton " : "") + className + ": " + image.getDrawable() + " "
                + label.getText();
    }

    /**
     * The style for an image text button, see {@link ImageTextButton}.
     */
    static public class ImageTextButtonStyle extends TextButtonStyle {
        public @Nullable Drawable imageUp, imageDown, imageOver, imageDisabled;
        public @Nullable Drawable imageChecked, imageCheckedDown, imageCheckedOver;

        public ImageTextButtonStyle() {
        }

        public ImageTextButtonStyle(@Nullable Drawable up, @Nullable Drawable down, @Nullable Drawable checked, BitmapFont font) {
            super(up, down, checked, font);
        }

        public ImageTextButtonStyle(ImageTextButtonStyle style) {
            super(style);
            imageUp = style.imageUp;
            imageDown = style.imageDown;
            imageOver = style.imageOver;
            imageDisabled = style.imageDisabled;

            imageChecked = style.imageChecked;
            imageCheckedDown = style.imageCheckedDown;
            imageCheckedOver = style.imageCheckedOver;
        }

        public ImageTextButtonStyle(TextButtonStyle style) {
            super(style);
        }
    }
}
