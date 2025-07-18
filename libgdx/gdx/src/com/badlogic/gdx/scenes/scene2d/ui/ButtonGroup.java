package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.utils.Array;

import org.jetbrains.annotations.Nullable;

/**
 * Manages a group of buttons to enforce a minimum and maximum number of checked buttons. This enables "radio button"
 * functionality and more. A button may only be in one group at a time.
 * <p>
 * The {@link #canCheck(Button, boolean)} method can be overridden to control if a button check or uncheck is allowed.
 */
public class ButtonGroup<T extends Button> {
    private final Array<T> buttons = new Array();
    private final Array<T> checkedButtons = new Array(1);
    private int minCheckCount, maxCheckCount = 1;
    private boolean uncheckLast = true;
    private T lastChecked;

    public ButtonGroup() {
        minCheckCount = 1;
    }

    @SafeVarargs
    public ButtonGroup(T... buttons) {
        minCheckCount = 0;
        add(buttons);
        minCheckCount = 1;
    }

    public void add(T button) {
        if (button == null) throw new IllegalArgumentException("button cannot be null.");
        button.buttonGroup = null;
        boolean shouldCheck = button.isChecked() || buttons.size < minCheckCount;
        button.setChecked(false);
        button.buttonGroup = this;
        buttons.add(button);
        button.setChecked(shouldCheck);
    }

    @SafeVarargs
    public final void add(T... buttons) {
        if (buttons == null) throw new IllegalArgumentException("buttons cannot be null.");
        for (T button : buttons) add(button);
    }

    public void remove(T button) {
        if (button == null) throw new IllegalArgumentException("button cannot be null.");
        button.buttonGroup = null;
        buttons.removeValue(button, true);
        checkedButtons.removeValue(button, true);
    }

    @SafeVarargs
    public final void remove(T... buttons) {
        if (buttons == null) throw new IllegalArgumentException("buttons cannot be null.");
        for (T button : buttons) remove(button);
    }

    public void clear() {
        buttons.clear();
        checkedButtons.clear();
    }

    /**
     * Called when a button is checked or unchecked. If overridden, generally changing button checked states should not be done
     * from within this method.
     *
     * @return True if the new state should be allowed.
     */
    protected boolean canCheck(T button, boolean newState) {
        if (button.isChecked == newState) return false;

        if (!newState) {
            // Keep button checked to enforce minCheckCount.
            if (checkedButtons.size <= minCheckCount) return false;
            checkedButtons.removeValue(button, true);
        } else {
            // Keep button unchecked to enforce maxCheckCount.
            if (maxCheckCount != -1 && checkedButtons.size >= maxCheckCount) {
                if (!uncheckLast) return false;
                for (int tries = 0; ; ) { // Try multiple times to allow the button states to settle.
                    int old = minCheckCount;
                    minCheckCount = 0;
                    lastChecked.setChecked(false); // May have listeners that change button states.
                    minCheckCount = old;
                    if (button.isChecked == newState) return false;
                    if (checkedButtons.size < maxCheckCount) break;
                    if (tries++ > 10) return false; // Unable to uncheck another button.
                }
            }
            checkedButtons.add(button);
            lastChecked = button;
        }

        return true;
    }

    /**
     * Sets all buttons' {@link Button#isChecked()} to false, regardless of {@link #setMinCheckCount(int)}.
     */
    public void uncheckAll() {
        int old = minCheckCount;
        minCheckCount = 0;
        for (int i = 0, n = buttons.size; i < n; i++) {
            T button = buttons.get(i);
            button.setChecked(false);
        }
        minCheckCount = old;
    }

    /**
     * @return The first checked button, or null.
     */
    public @Nullable T getChecked() {
        if (checkedButtons.size > 0) return checkedButtons.get(0);
        return null;
    }

    /**
     * Sets the first {@link TextButton} with the specified text to checked.
     */
    public void setChecked(String text) {
        if (text == null) throw new IllegalArgumentException("text cannot be null.");
        for (int i = 0, n = buttons.size; i < n; i++) {
            T button = buttons.get(i);
            if (button instanceof TextButton && text.contentEquals(((TextButton) button).getText())) {
                button.setChecked(true);
                return;
            }
        }
    }

    /**
     * @return The first checked button index, or -1.
     */
    public int getCheckedIndex() {
        if (checkedButtons.size > 0) return buttons.indexOf(checkedButtons.get(0), true);
        return -1;
    }

    public Array<T> getAllChecked() {
        return checkedButtons;
    }

    public Array<T> getButtons() {
        return buttons;
    }

    /**
     * Sets the minimum number of buttons that must be checked. Default is 1.
     */
    public void setMinCheckCount(int minCheckCount) {
        this.minCheckCount = minCheckCount;
    }

    /**
     * Sets the maximum number of buttons that can be checked. Set to -1 for no maximum. Default is 1.
     */
    public void setMaxCheckCount(int maxCheckCount) {
        if (maxCheckCount == 0) maxCheckCount = -1;
        this.maxCheckCount = maxCheckCount;
    }

    /**
     * If true, when the maximum number of buttons are checked and an additional button is checked, the last button to be checked
     * is unchecked so that the maximum is not exceeded. If false, additional buttons beyond the maximum are not allowed to be
     * checked. Default is true.
     */
    public void setUncheckLast(boolean uncheckLast) {
        this.uncheckLast = uncheckLast;
    }
}
