package com.badlogic.gdx.scenes.scene2d.actions;

/**
 * Multiplies the delta of an action.
 */
public class TimeScaleAction extends DelegateAction {
    private float scale;

    protected boolean delegate(float delta) {
        if (action == null) return true;
        return action.act(delta * scale);
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
