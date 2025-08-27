package com.badlogic.gdx.tools.hiero.unicodefont.effects;

import com.badlogic.gdx.tools.hiero.unicodefont.Glyph;
import com.badlogic.gdx.tools.hiero.unicodefont.UnicodeFont;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Paints glyphs with a gradient fill.
 */
public class GradientEffect implements ConfigurableEffect {
    private Color topColor = Color.cyan, bottomColor = Color.blue;
    private int offset = 0;
    private float scale = 1;
    private boolean cyclic;

    public GradientEffect() {
    }

    public void draw(BufferedImage image, Graphics2D g, UnicodeFont unicodeFont, Glyph glyph) {
        int ascent = unicodeFont.getAscent();
        float height = (ascent) * scale;
        float top = -glyph.getYOffset() + unicodeFont.getDescent() + offset + ascent / 2F - height / 2;
        g.setPaint(new GradientPaint(0, top, topColor, 0, top + height, bottomColor, cyclic));
        g.fill(glyph.getShape());
    }

    public int getOffset() {
        return offset;
    }

    /**
     * Sets the pixel offset to move the gradient up or down. The gradient is normally centered on the glyph.
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String toString() {
        return "Gradient";
    }

    public List getValues() {
        List values = new ArrayList();
        values.add(EffectUtil.colorValue("Top color", topColor));
        values.add(EffectUtil.colorValue("Bottom color", bottomColor));
        values.add(EffectUtil.intValue("Offset", offset,
                "This setting allows you to move the gradient up or down. The gradient is normally centered on the glyph."));
        values.add(EffectUtil.floatValue("Scale", scale, 0, 10, "This setting allows you to change the height of the gradient by a"
                + "percentage. The gradient is normally the height of most glyphs in the font."));
        values.add(EffectUtil.booleanValue("Cyclic", cyclic, "If this setting is checked, the gradient will repeat."));
        return values;
    }

    public void setValues(List values) {
        for (Object o : values) {
            Value value = (Value) o;
            if (value.getName().equals("Top color")) {
                topColor = (Color) value.getObject();
            } else if (value.getName().equals("Bottom color")) {
                bottomColor = (Color) value.getObject();
            } else if (value.getName().equals("Offset")) {
                offset = (Integer) value.getObject();
            } else if (value.getName().equals("Scale")) {
                scale = (Float) value.getObject();
            } else if (value.getName().equals("Cyclic")) {
                cyclic = (Boolean) value.getObject();
            }
        }
    }
}
