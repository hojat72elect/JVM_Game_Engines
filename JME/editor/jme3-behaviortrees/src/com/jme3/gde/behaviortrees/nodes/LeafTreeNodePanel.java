package com.jme3.gde.behaviortrees.nodes;

import com.badlogic.gdx.ai.btree.Task;
import com.jme3.gde.behaviortrees.editor.TreeNodePanel;
import com.jme3.gde.core.editor.nodes.Diagram;

/**
 *
 * @author MeFisto94
 */
public class LeafTreeNodePanel extends TreeNodePanel {

    public LeafTreeNodePanel(Diagram dia, Task task, String classPackage, String className) {
        super(task, dia, NodeType.LeafTask, 1, 0, className);
        setToolTipText(classPackage + "." + className);
        setSize(getPreferredSize()); // BUT WHY?
        revalidate();
        repaint();
    }
    
    public LeafTreeNodePanel(Diagram dia, Task task, String classPackage, String className, TreeNodePanel guardedPanel) {
        super(task, dia, NodeType.LeafTask, 1, 0, className, guardedPanel);
        setToolTipText(classPackage + "." + className);
        setSize(getPreferredSize()); // BUT WHY?
        revalidate();
        repaint();
    }
    
}
