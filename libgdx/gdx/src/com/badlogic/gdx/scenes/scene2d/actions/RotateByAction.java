package com.badlogic.gdx.scenes.scene2d.actions;

/**
 * Sets the actor's rotation from its current value to a relative value.
 */
public class RotateByAction extends RelativeTemporalAction {
    private float amount;

    protected void updateRelative(float percentDelta) {
        target.rotateBy(amount * percentDelta);
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float rotationAmount) {
        amount = rotationAmount;
    }
}
