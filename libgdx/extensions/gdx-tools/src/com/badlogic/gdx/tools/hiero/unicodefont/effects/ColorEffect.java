package com.badlogic.gdx.tools.hiero.unicodefont.effects;

import com.badlogic.gdx.tools.hiero.unicodefont.Glyph;
import com.badlogic.gdx.tools.hiero.unicodefont.UnicodeFont;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Makes glyphs a solid color.
 */
public class ColorEffect implements ConfigurableEffect {
    private Color color = Color.white;

    public ColorEffect() {
    }

    public void draw(BufferedImage image, Graphics2D g, UnicodeFont unicodeFont, Glyph glyph) {
        g.setColor(color);
        try {
            g.fill(glyph.getShape()); // Java2D fails on some glyph shapes?!
        } catch (Throwable ignored) {
        }
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        if (color == null) throw new IllegalArgumentException("color cannot be null.");
        this.color = color;
    }

    public String toString() {
        return "Color";
    }

    public List getValues() {
        List values = new ArrayList();
        values.add(EffectUtil.colorValue("Color", color));
        return values;
    }

    public void setValues(List values) {
        for (Object o : values) {
            Value value = (Value) o;
            if (value.getName().equals("Color")) {
                setColor((Color) value.getObject());
            }
        }
    }
}
