package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Action;

/**
 * Adds an action to an actor.
 */
public class AddAction extends Action {
    private Action action;

    public boolean act(float delta) {
        target.addAction(action);
        return true;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void restart() {
        if (action != null) action.restart();
    }

    public void reset() {
        super.reset();
        action = null;
    }
}
