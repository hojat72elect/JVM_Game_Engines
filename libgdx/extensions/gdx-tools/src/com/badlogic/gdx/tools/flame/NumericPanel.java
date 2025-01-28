package com.badlogic.gdx.tools.flame;

import com.badlogic.gdx.graphics.g3d.particles.values.NumericValue;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 *
 */
class NumericPanel extends ParticleValuePanel<NumericValue> {
    JSpinner valueSpinner;

    public NumericPanel(FlameMain editor, NumericValue value, String name, String description) {
        super(editor, name, description);
        setValue(value);
    }

    @Override
    public void setValue(NumericValue value) {
        super.setValue(value);
        if (value == null) return;
        setValue(valueSpinner, value.getValue());
    }

    protected void initializeComponents() {
        super.initializeComponents();
        JPanel contentPanel = getContentPanel();
        {
            JLabel label = new JLabel("Value:");
            contentPanel.add(label, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                    new Insets(0, 0, 0, 6), 0, 0));
        }
        {
            valueSpinner = new JSpinner(new SpinnerNumberModel(0, -99999, 99999, 0.1f));
            contentPanel.add(valueSpinner, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 0, 0), 0, 0));
        }
        valueSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                NumericPanel.this.value.setValue(((Number) valueSpinner.getValue()).floatValue());
            }
        });
    }
}
