/*
 * Copyright (c) 2009-2018 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.gde.behaviortrees.navigator.nodes;

import com.jme3.gde.behaviortrees.editor.TreeNodePanel;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author MeFisto94
 */
public class TreeNodePanelNode extends AbstractTaskNode {
    TreeNodePanel panel;
    
    public TreeNodePanelNode(TreeNodePanel tnp, Children children, Lookup lookup) {
        super(tnp.getTask(), children, lookup);
        if (tnp.isGuard()) {
            setName("[GUARD]: " + tnp.getName());
        } else {
            setName(tnp.getName());
        }
        this.panel = tnp;
    }

    public TreeNodePanelNode(TreeNodePanel tnp, Lookup lookup) {
        this(tnp, Children.create(new ChildFactory<TreeNodePanel>() {
            @Override
            protected boolean createKeys(List<TreeNodePanel> list) {
                if (tnp.getGuardPanel() != null) {
                    list.add(tnp.getGuardPanel());
                }
                
                list.addAll(tnp.getChildren());
                return true;
            }

            @Override
            protected Node createNodeForKey(TreeNodePanel key) {
                if (key.getChildren().isEmpty() && key.getGuardPanel() == null) {
                    return new TreeNodePanelNode(key, Children.LEAF, lookup);
                } else {
                    return new TreeNodePanelNode(key, lookup);
                }
            }
        }, false), lookup);
    }

    public TreeNodePanel getPanel() {
        return panel;
    }
    
}
