package com.jme3.gde.behaviortrees.nodes;

import com.badlogic.gdx.ai.btree.Task;
import com.jme3.gde.core.editor.nodes.Diagram;

/**
 * The Sequential node allows us to do multiple tasks one after another.
 * There is: Sequence [Until one fails], Selector [Until one succeeds], Parallel
 * etc (Every task extending com.badlogic.gdx.ai.btree.BranchTask)
 * @author MeFisto94
 */
public class SequentialNodePanel extends DynamicOutputNodePanel {
    
    public SequentialNodePanel(Diagram dia, Task task) {
        this(dia, task, 1);
    }
    
    public SequentialNodePanel(Diagram dia, Task task, NodeType nodeType, int numOutputs, String nameAndTitle) {
        super(dia, task, nodeType, 1, numOutputs, nameAndTitle);
        setToolTipText(bundle.getString("Sequential.Description"));
        setSize(getPreferredSize()); // BUT WHY?
        revalidate();
        repaint();
    }
    
    public SequentialNodePanel(Diagram dia, Task task, int numOutputs) {
        this(dia, task, NodeType.Sequential, numOutputs, task.getClass().getSimpleName());
    }
    
    public SequentialNodePanel(Diagram dia, Task task, int numOutputs, String nameAndTitle) {
        this(dia, task, NodeType.Sequential, numOutputs, nameAndTitle);
    }
}
