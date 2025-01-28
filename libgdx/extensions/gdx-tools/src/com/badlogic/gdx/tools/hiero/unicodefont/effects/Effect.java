package com.badlogic.gdx.tools.hiero.unicodefont.effects;

import com.badlogic.gdx.tools.hiero.unicodefont.Glyph;
import com.badlogic.gdx.tools.hiero.unicodefont.UnicodeFont;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A graphical effect that is applied to glyphs in a {@link UnicodeFont}.
 */
public interface Effect {
    /**
     * Called to draw the effect.
     */
    public void draw(BufferedImage image, Graphics2D g, UnicodeFont unicodeFont, Glyph glyph);
}
