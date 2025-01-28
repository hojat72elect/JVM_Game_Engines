package com.badlogic.gdx.tools.hiero.unicodefont.effects;

import com.badlogic.gdx.tools.hiero.unicodefont.Glyph;
import com.badlogic.gdx.tools.hiero.unicodefont.UnicodeFont;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

/**
 * Applys a {@link BufferedImageOp} filter to glyphs. Many filters can be fond here: http://www.jhlabs.com/ip/filters/index.html
 */
public class FilterEffect implements Effect {
    private BufferedImageOp filter;

    public FilterEffect() {
    }

    public FilterEffect(BufferedImageOp filter) {
        this.filter = filter;
    }

    public void draw(BufferedImage image, Graphics2D g, UnicodeFont unicodeFont, Glyph glyph) {
        BufferedImage scratchImage = EffectUtil.getScratchImage();
        filter.filter(image, scratchImage);
        image.getGraphics().drawImage(scratchImage, 0, 0, null);
    }

    public BufferedImageOp getFilter() {
        return filter;
    }

    public void setFilter(BufferedImageOp filter) {
        this.filter = filter;
    }
}
