/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.behaviortrees.nodes.impl;

import com.badlogic.gdx.ai.btree.Task;
import com.jme3.gde.behaviortrees.editor.TreeNodePanel;
import com.jme3.gde.core.editor.nodes.Diagram;

/**
 * Wait, Failure, Success.
 * @author MeFisto94
 */
public class BuiltinLeafTask extends TreeNodePanel {

    public BuiltinLeafTask(Diagram dia, Task task) {
        super(task, dia, NodeType.BuiltinLeafTask, 1, 0, task.getClass().getSimpleName());
        setSize(getPreferredSize()); // BUT WHY?
        revalidate();
        repaint();
    }
}
