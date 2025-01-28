package com.jme3.gde.behaviortrees.nodes.impl;

import com.badlogic.gdx.ai.btree.Task;
import com.jme3.gde.behaviortrees.nodes.SequentialNodePanel;
import com.jme3.gde.core.editor.nodes.Diagram;
import java.awt.Color;

/**
 *
 * @author marc
 */
public class SelectorNodePanel extends SequentialNodePanel {
    
    public SelectorNodePanel(Diagram dia, Task task, int numOutputs) {
        super(dia, task, numOutputs, "Selector");
        backgroundColor = new Color(220, 40, 220);
        //setToolTipText(bundle.getString("Sequential.Description"));
        setSize(getPreferredSize()); // BUT WHY?
        revalidate();
        repaint();
    }
}
