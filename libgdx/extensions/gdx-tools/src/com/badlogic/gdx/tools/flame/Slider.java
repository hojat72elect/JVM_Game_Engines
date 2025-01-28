package com.badlogic.gdx.tools.flame;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 *
 */
public class Slider extends JPanel {
    public JSpinner spinner;

    public Slider(float initialValue, final float min, final float max, float stepSize) {
        spinner = new JSpinner(new SpinnerNumberModel(initialValue, min, max, stepSize));
        setLayout(new BorderLayout());
        add(spinner);
    }

    public float getValue() {
        return ((Number) spinner.getValue()).floatValue();
    }

    public void setValue(float value) {
        spinner.setValue((double) value);
    }

    public void addChangeListener(ChangeListener listener) {
        spinner.addChangeListener(listener);
    }

    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.width = 75;
        size.height = 26;
        return size;
    }
}
