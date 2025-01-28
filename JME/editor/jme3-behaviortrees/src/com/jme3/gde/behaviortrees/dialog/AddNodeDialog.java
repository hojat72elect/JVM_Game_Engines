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
package com.jme3.gde.behaviortrees.dialog;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.DynamicGuardSelector;
import com.badlogic.gdx.ai.btree.branch.Parallel;
import com.badlogic.gdx.ai.btree.branch.RandomSelector;
import com.badlogic.gdx.ai.btree.branch.RandomSequence;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.badlogic.gdx.ai.btree.decorator.AlwaysFail;
import com.badlogic.gdx.ai.btree.decorator.AlwaysSucceed;
import com.badlogic.gdx.ai.btree.decorator.Include;
import com.badlogic.gdx.ai.btree.decorator.Invert;
import com.badlogic.gdx.ai.btree.decorator.Random;
import com.badlogic.gdx.ai.btree.decorator.Repeat;
import com.badlogic.gdx.ai.btree.decorator.SemaphoreGuard;
import com.badlogic.gdx.ai.btree.decorator.UntilFail;
import com.badlogic.gdx.ai.btree.decorator.UntilSuccess;
import com.badlogic.gdx.ai.btree.leaf.Failure;
import com.badlogic.gdx.ai.btree.leaf.Success;
import com.badlogic.gdx.ai.btree.leaf.Wait;
import com.jme3.gde.behaviortrees.editor.BTreeNodeEditorElement;
import com.jme3.gde.behaviortrees.editor.TreeDiagram;
import com.jme3.gde.behaviortrees.editor.TreeNodePanel;
import com.jme3.gde.core.assets.ProjectAssetManager;
import com.jme3.gde.core.util.TreeUtil;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.TypeElement;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.source.ClassIndex;
import org.netbeans.api.java.source.ClassIndex.NameKind;
import org.netbeans.api.java.source.ClassIndex.SearchScope;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;

/**
 * This dialog borrows it's design from netbeans "New File" Dialogs, where you
 * have a tree panel to the left with all the available categories and a list
 * to the right with actual tasks (in this case)
 * 
 * @author MeFisto94
 * @author Nehon
 */
public class AddNodeDialog extends JDialog {

    private final TreeDiagram diagram;
    private final Point clickPosition;
    private final HashMap<String, TaskWrapper[]> pathContents;
    private final ProjectAssetManager mgr;
    
    private final Class<?>[] builtinClasses = new Class<?>[] {
        AlwaysFail.class,
        AlwaysSucceed.class,
        DynamicGuardSelector.class,
        Failure.class,
        Include.class,
        Invert.class,
        Parallel.class,
        Random.class,
        RandomSelector.class,
        RandomSequence.class,
        Repeat.class,
        Selector.class,
        SemaphoreGuard.class,
        Sequence.class,
        Success.class,
        UntilFail.class,
        UntilSuccess.class,
        Wait.class
    };

    /**
     * Creates new form AddNodeDialog
     */
    public AddNodeDialog(java.awt.Frame parent, boolean modal, ProjectAssetManager mgr, TreeDiagram diagram, Point clickPosition) {
        super(parent, modal);
        this.diagram = diagram;
        this.clickPosition = clickPosition;
        this.mgr = mgr;
        pathContents = new HashMap<>();
        initComponents();
        fillList(mgr);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        btnOk = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        nodeListPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        nodeList = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add a Shader Node");
        setModal(true);

        btnOk.setText("Ok");
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        btnCancel.setMnemonic(KeyEvent.VK_ESCAPE);
        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCancel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnOk)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnOk)
                .addComponent(btnCancel))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Node Overview"));

        jScrollPane3.setViewportView(jTree1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
        );

        jSplitPane1.setLeftComponent(jPanel1);
        jPanel1.getAccessibleContext().setAccessibleName("Node Overview");

        nodeListPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Select BehaviorTree Node"));

        nodeList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(nodeList);

        javax.swing.GroupLayout nodeListPanelLayout = new javax.swing.GroupLayout(nodeListPanel);
        nodeListPanel.setLayout(nodeListPanelLayout);
        nodeListPanelLayout.setHorizontalGroup(
            nodeListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nodeListPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                .addContainerGap())
        );
        nodeListPanelLayout.setVerticalGroup(
            nodeListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(nodeListPanel);
        nodeListPanel.getAccessibleContext().setAccessibleName("Select BehaviorTree Node");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.LEADING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jSplitPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        setVisible(false);
        
        try {
            Task task = nodeList.getSelectedValue().task.newInstance();
            TreeNodePanel tnp = BTreeNodeEditorElement.taskToPanel(diagram, task, null);
            diagram.addNode(tnp);
            tnp.setLocation(clickPosition);
            tnp.revalidate();
            repaint();
        } catch (IllegalAccessException | InstantiationException ex) {
            
        }
    }//GEN-LAST:event_btnOkActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTree jTree1;
    private javax.swing.JList<TaskWrapper> nodeList;
    private javax.swing.JPanel nodeListPanel;
    // End of variables declaration//GEN-END:variables

    private void fillList(final ProjectAssetManager mgr) {
        pathContents.put("Builtin/Branch", new TaskWrapper[] { 
            new TaskWrapper(DynamicGuardSelector.class),
            new TaskWrapper(Parallel.class), new TaskWrapper(RandomSelector.class),
            new TaskWrapper(RandomSequence.class), new TaskWrapper(Selector.class),
            new TaskWrapper(Sequence.class)
        });
        
        pathContents.put("Builtin/Leaf", new TaskWrapper[] { 
            new TaskWrapper(Failure.class), new TaskWrapper(Success.class),
            new TaskWrapper(Wait.class) 
        });

        pathContents.put("Builtin/Decorator", new TaskWrapper[] {
            new TaskWrapper(AlwaysFail.class), new TaskWrapper(AlwaysSucceed.class),
            new TaskWrapper(Include.class), new TaskWrapper(Invert.class),
            new TaskWrapper(Random.class), new TaskWrapper(Repeat.class),
            new TaskWrapper(SemaphoreGuard.class), new TaskWrapper(UntilFail.class),
            new TaskWrapper(UntilSuccess.class)
        });
        
        List<Class> taskClasses = getSources();
        taskClasses.forEach(c -> {
            String path = "Custom Tasks/" + c.getPackage().getName();
            if (pathContents.containsKey(path)) {
                // this is a bit bad but if pathContents' values would've been lists,
                // the builtin code would look worse.
                ArrayList<TaskWrapper> list = new ArrayList<>(pathContents.get(path).length + 1);
                list.addAll(Arrays.asList(pathContents.get(path)));
                list.add(new TaskWrapper(c));
                pathContents.put(path, list.toArray(new TaskWrapper[0]));
            } else {
            pathContents.put(path,
                    new TaskWrapper[] { new TaskWrapper(c)});
            }
        });
        
        /*String[] leaves = new String[] { "Builtin/Branch", 
            "Builtin/Decorator", "Builtin/Leaf", "Custom Tasks/Stupid Task 1"};*/
        String[] leaves = pathContents.keySet().toArray(new String[0]);
        TreeUtil.createTree(jTree1, leaves);
        TreeUtil.expandTree(jTree1, (TreeNode) jTree1.getModel().getRoot(), 10);
        
        jTree1.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTree1.addTreeSelectionListener((TreeSelectionEvent e) -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)jTree1.getLastSelectedPathComponent();
            
            if (node == null) {
                return;
            }
            
            if (node.isLeaf()) {
                //jTabbedPane1.removeAll();
                String path = TreeUtil.getPath(node.getUserObjectPath());
                path = path.substring(0, path.lastIndexOf("/"));
                
                TaskWrapper[] contents = pathContents.get(path);
                if (contents != null) {
                    DefaultListModel dlm = new DefaultListModel<>();
                    for (TaskWrapper tw: contents) {
                        dlm.addElement(tw);
                    }
                    nodeList.setModel(dlm);
                } else {
                    nodeList.setModel(new DefaultListModel<>());
                }
            }
        });
    }
    
    private Class findClass(String binaryName) {
        for (ClassLoader cl: mgr.getClassLoaders()) {
            try {
                return cl.loadClass(binaryName);
            } catch (ClassNotFoundException cnf) {
                
            }
        }
        
        return null;
    }
    
    private List<Class> getSources() {
        // Code taken from jme3-core/src/com/jme3/gde/core/sceneexplorer/nodes/actions/impl/NewCustomControlVisualPanel1.java
        Sources sources = mgr.getProject().getLookup().lookup(Sources.class);
        final List<Class> list = new ArrayList<>();
        if (sources != null) {
            SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
            if (groups != null) {
                for (SourceGroup sourceGroup : groups) {
                    final ClasspathInfo cpInfo = ClasspathInfo.create(ClassPath.getClassPath(sourceGroup.getRootFolder(), ClassPath.BOOT),
                            ClassPath.getClassPath(sourceGroup.getRootFolder(), ClassPath.COMPILE),
                            ClassPath.getClassPath(sourceGroup.getRootFolder(), ClassPath.SOURCE));

                    HashSet<SearchScope> set = new HashSet<>();
                    set.add(ClassIndex.SearchScope.SOURCE);
                    Set<ElementHandle<TypeElement>> types = cpInfo.getClassIndex().getDeclaredTypes("", NameKind.PREFIX, set);
                    for (Iterator<ElementHandle<TypeElement>> it = types.iterator(); it.hasNext();) {
                        final ElementHandle<TypeElement> elementHandle = it.next();
                        // here we start to deviate from the approach for controls,
                        // because the Generics in combination with openjdk8 (guessing)
                        // lead to an error in the compiler
                        // AND this code is much simpler anyway
                        String bn = elementHandle.getBinaryName();
                        Class c = findClass(bn);
                        
                        if (c == null || list.contains(c)) {
                            continue;
                        }
                        
                        if (Task.class.isAssignableFrom(c)) {
                            list.add(c);
                        }
                    }
                }
            }
        }
        return list;
    }
    
    private final class TaskWrapper {
        Class<? extends Task> task;
        String name;

        public TaskWrapper(Class<? extends Task> task, String name) {
            this.task = task;
            this.name = name;
        }
        
        public TaskWrapper(Class<? extends Task> task) {
            this(task, task.getSimpleName());
        }

        public Class<? extends Task> getTask() {
            return task;
        }
        
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return getName();
        }
    }
}
