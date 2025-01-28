package com.jme3.gde.behaviortrees.nodes.impl;

import com.badlogic.gdx.ai.btree.Task;
import com.jme3.gde.behaviortrees.editor.TreeNodePanel;
import com.jme3.gde.core.editor.nodes.Diagram;

/**
 *
 * @author MeFisto94
 */
public class DecoratorNodePanel extends TreeNodePanel {

    public DecoratorNodePanel(Diagram dia, Task task) {
        super(task, dia, NodeType.Decorator, 1, 1, task.getClass().getSimpleName());
        setToolTipText(task.getClass().getSimpleName());
        setSize(getPreferredSize()); // BUT WHY?
        revalidate();
        repaint();
    }
    
}
