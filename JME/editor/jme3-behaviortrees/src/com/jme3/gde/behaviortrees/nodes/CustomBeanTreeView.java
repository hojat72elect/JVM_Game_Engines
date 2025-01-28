/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.behaviortrees.nodes;

import com.jme3.gde.behaviortrees.navigator.BTreeNavigatorPanel;
import java.beans.PropertyVetoException;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Node;

/**
 *
 * @author MeFisto94
 */
public class CustomBeanTreeView extends BeanTreeView {
    protected BTreeNavigatorPanel nav;
    
    public CustomBeanTreeView(BTreeNavigatorPanel nav) {
        this.nav = nav;
    }
    
    @Override
    protected void selectionChanged(Node[] nodes, ExplorerManager em) throws PropertyVetoException {
        super.selectionChanged(nodes, em);
        nav.selectionChanged(nodes, em);
    }
    
}
