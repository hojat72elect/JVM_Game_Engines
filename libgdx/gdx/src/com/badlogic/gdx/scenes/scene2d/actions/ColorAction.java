package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;

import org.jetbrains.annotations.Nullable;

/**
 * Sets the actor's color (or a specified color), from the current to the new color. Note this action transitions from the color
 * at the time the action starts to the specified color.
 */
public class ColorAction extends TemporalAction {
    private final Color end = new Color();
    private float startR, startG, startB, startA;
    private @Nullable Color color;

    protected void begin() {
        if (color == null) color = target.getColor();
        startR = color.r;
        startG = color.g;
        startB = color.b;
        startA = color.a;
    }

    protected void update(float percent) {
        if (percent == 0)
            color.set(startR, startG, startB, startA);
        else if (percent == 1)
            color.set(end);
        else {
            float r = startR + (end.r - startR) * percent;
            float g = startG + (end.g - startG) * percent;
            float b = startB + (end.b - startB) * percent;
            float a = startA + (end.a - startA) * percent;
            color.set(r, g, b, a);
        }
    }

    public void reset() {
        super.reset();
        color = null;
    }

    public @Nullable Color getColor() {
        return color;
    }

    /**
     * Sets the color to modify. If null (the default), the {@link #getActor() actor's} {@link Actor#getColor() color} will be
     * used.
     */
    public void setColor(@Nullable Color color) {
        this.color = color;
    }

    public Color getEndColor() {
        return end;
    }

    /**
     * Sets the color to transition to. Required.
     */
    public void setEndColor(Color color) {
        end.set(color);
    }
}
