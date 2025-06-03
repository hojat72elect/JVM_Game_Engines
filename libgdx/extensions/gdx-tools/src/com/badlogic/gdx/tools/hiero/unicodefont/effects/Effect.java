package com.badlogic.gdx.tools.hiero.unicodefont.effects;

import com.badlogic.gdx.tools.hiero.unicodefont.Glyph;
import com.badlogic.gdx.tools.hiero.unicodefont.UnicodeFont;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * A graphical effect that is applied to glyphs in a {@link UnicodeFont}.
 */
public interface Effect {
    /**
     * Called to draw the effect.
     */
    void draw(BufferedImage image, Graphics2D g, UnicodeFont unicodeFont, Glyph glyph);
}
