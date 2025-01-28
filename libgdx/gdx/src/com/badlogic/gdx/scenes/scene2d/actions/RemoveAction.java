package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Action;

/**
 * Removes an action from an actor.
 */
public class RemoveAction extends Action {
    private Action action;

    public boolean act(float delta) {
        target.removeAction(action);
        return true;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void reset() {
        super.reset();
        action = null;
    }
}
