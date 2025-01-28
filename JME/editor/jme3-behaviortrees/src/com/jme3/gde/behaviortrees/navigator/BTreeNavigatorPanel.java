/*
 *  Copyright (c) 2009-2018 jMonkeyEngine
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are
 *  met:
 * 
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 *  * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.gde.behaviortrees.navigator;

import com.jme3.gde.behaviortrees.BTreeDataObject;
import com.jme3.gde.behaviortrees.editor.BTreeNodeEditorElement;
import com.jme3.gde.behaviortrees.navigator.nodes.TreeNodePanelNode;
import com.jme3.gde.behaviortrees.nodes.CustomBeanTreeView;
import java.util.Collection;
import javax.swing.JComponent;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;

/**
 * This Panel is responsible for populating the "Navigator" with a view over the
 * Scene. This means one can select specific nodes in the Navigator, which is
 * not really practical but required for the Properties to work it seems.
 * 
 * @author MeFisto94
 */
@NavigatorPanel.Registration(mimeType = "text/gdx-ai-btree", displayName = "Behavior Tree")
@SuppressWarnings({"unchecked", "rawtypes"})
public class BTreeNavigatorPanel extends TopComponent /* JPanel*/ implements NavigatorPanel, ExplorerManager.Provider, Lookup.Provider {

    /**
     * template for finding data in given context.
     */
    private static final Lookup.Template<BTreeDataObject> MY_DATA = new Lookup.Template<BTreeDataObject>(BTreeDataObject.class);

    /**
     * current context to work on
     */
    private Lookup.Result<BTreeDataObject> curContext;
    private final Lookup lookup;
    /**
     * listener to context changes
     */
    private LookupListener contextL;
    private final ExplorerManager mgr = new ExplorerManager();

    private BTreeDataObject data;
    private BTreeNodeEditorElement topComponent;
    
    /**
     * Creates new form MatDefNavigatorPanel
     */
    public BTreeNavigatorPanel() {
        initComponents();
        lookup = ExplorerUtils.createLookup(mgr, getActionMap());
        associateLookup(lookup);
    }

    @Override
    public String getDisplayHint() {
        return "BehaviorTree outline view";
    }

    @Override
    public String getDisplayName() {
        return "Navigator Panel";
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void panelActivated(Lookup context) {
        // lookup context and listen to result to get notified about context changes
        curContext = context.lookup(MY_DATA);
        //lookup = context;
        curContext.addLookupListener(getContextListener());
        // get actual data and recompute content
        Collection<? extends BTreeDataObject> data = curContext.allInstances();
        setNewContent(data);
        ExplorerUtils.activateActions(mgr, true);
    }

    @Override
    public void panelDeactivated() {
        Collection<? extends BTreeDataObject>  data = curContext.allInstances();
        if (!data.isEmpty()) {
            BTreeDataObject obj = (BTreeDataObject) data.iterator().next();
            obj.getLookupContents().remove(this);
        }
        curContext.removeLookupListener(getContextListener());
        curContext = null;
        mgr.setRootContext(Node.EMPTY);
        ExplorerUtils.activateActions(mgr, false);
    }

    @Override
    public Lookup getLookup() {
        // go with default activated Node strategy
        return lookup;
    }
    
    public void selectionChanged(Node[] nodes, ExplorerManager em) {
        if (data != null) {
            BTreeNodeEditorElement ele = data.getTopComponent();
            if (ele != null) {
                ele.selectionChanged(nodes);
            }
        }
    }

    /**
     * *********** non - public part ***********
     */
    private void setNewContent(Collection<? extends BTreeDataObject>  newData) {
        if (!newData.isEmpty()) {
            BTreeDataObject data = (BTreeDataObject) newData.iterator().next();
            data.getLookupContents().add(this);
            if (data.isLoaded()) {
                updateData(data);
            } else {
                mgr.setRootContext(Node.EMPTY);
            }
        }
    }

    /**
     * Accessor for listener to context
     */
    private LookupListener getContextListener() {
        if (contextL == null) {
            contextL = new ContextListener();
        }
        return contextL;
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    public void updateData(BTreeDataObject data) {
        this.data = data;        
        
        if (data != null && data.getRootNodePanel() != null) {
            mgr.setRootContext(new TreeNodePanelNode(data.getRootNodePanel(), data.getLookup()));
        } else {
            mgr.setRootContext(Node.EMPTY);
        }
        
        try {
            mgr.setSelectedNodes(new Node[] {});
            ExplorerUtils.activateActions(mgr, true);
        } catch (Exception e) {
            
        }
    }

    /**
     * Listens to changes of context and triggers proper action
     */
    private class ContextListener implements LookupListener {
        @Override
        public void resultChanged(LookupEvent ev) {
            Collection<? extends BTreeDataObject>  data = (Collection<? extends BTreeDataObject>)((Lookup.Result<?> ) ev.getSource()).allInstances();
            setNewContent(data);
        }
    } // end of ContextListener

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new CustomBeanTreeView(this);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

}
