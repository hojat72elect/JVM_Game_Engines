package com.jme3.gde.behaviortrees.nodes.impl;

import com.badlogic.gdx.ai.btree.Task;
import com.jme3.gde.behaviortrees.nodes.SequentialNodePanel;
import com.jme3.gde.core.editor.nodes.Diagram;

/**
 *
 * @author MeFisto94
 */
public class SequenceNodePanel extends SequentialNodePanel {
    
    public SequenceNodePanel(Diagram dia, Task task, int numOutputs) {
        super(dia, task, numOutputs, "Sequence");
        //setToolTipText(bundle.getString("Sequential.Description"));
        setSize(getPreferredSize()); // BUT WHY?
        revalidate();
        repaint();
    }
}
