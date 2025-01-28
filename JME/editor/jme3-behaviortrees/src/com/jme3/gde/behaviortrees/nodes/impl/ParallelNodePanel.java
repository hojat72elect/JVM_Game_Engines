package com.jme3.gde.behaviortrees.nodes.impl;

import com.badlogic.gdx.ai.btree.Task;
import com.jme3.gde.behaviortrees.nodes.SequentialNodePanel;
import com.jme3.gde.core.editor.nodes.Diagram;

/**
 *
 * @author marc
 */
public class ParallelNodePanel extends SequentialNodePanel {
    public ParallelNodePanel(Diagram dia, Task task, int numOutputs) {
        super(dia, task, NodeType.Parallel, numOutputs, "Parallel");
        //setToolTipText(bundle.getString("Sequential.Description"));
        setSize(getPreferredSize()); // BUT WHY?
        revalidate();
        repaint();
    }
}
