package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;

import org.jetbrains.annotations.Nullable;

/**
 * A button with a child {@link Image} to display an image. This is useful when the button must be larger than the image and the
 * image centered on the button. If the image is the size of the button, a {@link Button} without any children can be used, where
 * the {@link Button.ButtonStyle#up}, {@link Button.ButtonStyle#down}, and {@link Button.ButtonStyle#checked} nine patches define
 * the image.
 */
public class ImageButton extends Button {
    private final Image image;
    private ImageButtonStyle style;

    public ImageButton(Skin skin) {
        this(skin.get(ImageButtonStyle.class));
        setSkin(skin);
    }

    public ImageButton(Skin skin, String styleName) {
        this(skin.get(styleName, ImageButtonStyle.class));
        setSkin(skin);
    }

    public ImageButton(ImageButtonStyle style) {
        super(style);
        image = newImage();
        add(image);
        setStyle(style);
        setSize(getPrefWidth(), getPrefHeight());
    }

    public ImageButton(@Nullable Drawable imageUp) {
        this(new ImageButtonStyle(null, null, null, imageUp, null, null));
    }

    public ImageButton(@Nullable Drawable imageUp, @Nullable Drawable imageDown) {
        this(new ImageButtonStyle(null, null, null, imageUp, imageDown, null));
    }

    public ImageButton(@Nullable Drawable imageUp, @Nullable Drawable imageDown, @Nullable Drawable imageChecked) {
        this(new ImageButtonStyle(null, null, null, imageUp, imageDown, imageChecked));
    }

    protected Image newImage() {
        return new Image(null, Scaling.fit);
    }

    public ImageButtonStyle getStyle() {
        return style;
    }

    public void setStyle(ButtonStyle style) {
        if (!(style instanceof ImageButtonStyle))
            throw new IllegalArgumentException("style must be an ImageButtonStyle.");
        this.style = (ImageButtonStyle) style;
        super.setStyle(style);

        if (image != null) updateImage();
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

    public void draw(Batch batch, float parentAlpha) {
        updateImage();
        super.draw(batch, parentAlpha);
    }

    public Image getImage() {
        return image;
    }

    public Cell getImageCell() {
        return getCell(image);
    }

    public String toString() {
        String name = getName();
        if (name != null) return name;
        String className = getClass().getName();
        int dotIndex = className.lastIndexOf('.');
        if (dotIndex != -1) className = className.substring(dotIndex + 1);
        return (className.indexOf('$') != -1 ? "ImageButton " : "") + className + ": " + image.getDrawable();
    }

    /**
     * The style for an image button, see {@link ImageButton}.
     */
    static public class ImageButtonStyle extends ButtonStyle {
        public @Nullable Drawable imageUp, imageDown, imageOver, imageDisabled;
        public @Nullable Drawable imageChecked, imageCheckedDown, imageCheckedOver;

        public ImageButtonStyle() {
        }

        public ImageButtonStyle(@Nullable Drawable up, @Nullable Drawable down, @Nullable Drawable checked, @Nullable Drawable imageUp,
                                @Nullable Drawable imageDown, @Nullable Drawable imageChecked) {
            super(up, down, checked);
            this.imageUp = imageUp;
            this.imageDown = imageDown;
            this.imageChecked = imageChecked;
        }

        public ImageButtonStyle(ImageButtonStyle style) {
            super(style);
            imageUp = style.imageUp;
            imageDown = style.imageDown;
            imageOver = style.imageOver;
            imageDisabled = style.imageDisabled;

            imageChecked = style.imageChecked;
            imageCheckedDown = style.imageCheckedDown;
            imageCheckedOver = style.imageCheckedOver;
        }

        public ImageButtonStyle(ButtonStyle style) {
            super(style);
        }
    }
}
