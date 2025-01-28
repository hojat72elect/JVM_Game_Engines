package com.jme3.gde.behaviortrees.nodes.impl;

import com.badlogic.gdx.ai.btree.Task;
import com.jme3.gde.behaviortrees.editor.TreeNodePanel;
import com.jme3.gde.core.editor.nodes.Diagram;

/**
 *
 * @author MeFisto94
 */
public class RootNodePanel extends TreeNodePanel {

    public RootNodePanel(Diagram dia, Task task) {
        super(task, dia, NodeType.Root, 0, 1, "Root");
        setToolTipText("Root");
        setSize(getPreferredSize()); // BUT WHY?
        revalidate();
        repaint();
    }
    
}
