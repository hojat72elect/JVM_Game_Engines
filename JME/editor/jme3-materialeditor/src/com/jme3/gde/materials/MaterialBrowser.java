/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MaterialBrowser.java
 *
 * Created on 31 juil. 2011, 12:20:52
 */
package com.jme3.gde.materials;

import com.jme3.gde.core.assets.ProjectAssetManager;
import com.jme3.gde.core.util.TreeUtil;
import com.jme3.material.Material;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.openide.util.Exceptions;

/**
 * @author Nehon
 */
public class MaterialBrowser extends javax.swing.JDialog implements TreeSelectionListener, WindowListener {

    private final ProjectAssetManager assetManager;
    private final MaterialPropertyEditor editor;
    private final Preferences prefs;
    private static final String PREF_LAST_SELECTED = "lastSelectedMaterial";

    /** Creates new form MaterialBrowser */
    public MaterialBrowser(java.awt.Frame parent, boolean modal, ProjectAssetManager assetManager, MaterialPropertyEditor editor) {
        this.assetManager = assetManager;
        this.editor = editor;
        prefs = Preferences.userNodeForPackage(this.getClass());
        initComponents();
        loadAvailableMaterials();
        setSelectedMaterial((Material) editor.getValue());
        setLocationRelativeTo(null);
    }

    private void loadAvailableMaterials() {
        if (assetManager == null) {
            return;
        }
        String[] leaves = assetManager.getMaterials();
        List<String> leavesList = Arrays.asList(leaves);
        Collections.sort(leavesList);
        TreeUtil.createTree(jTree1, leavesList.toArray(String[]::new));
        TreeUtil.expandTree(jTree1, (TreeNode) jTree1.getModel().getRoot(), 1);
        jTree1.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTree1.addTreeSelectionListener(this);
    }

    private boolean setMaterial() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();

        if (node != null && node.isLeaf()) {
            String selected = TreeUtil.getPath(node.getUserObjectPath());
            selected = selected.substring(0, selected.lastIndexOf("/"));
            Material mat = assetManager.loadMaterial(selected);
            editor.setValue(mat);
            editor.setAsText(selected);
            return true;
        }

        return false;

    }

    private void setSelectedMaterial(Material material) {
        if (material != null) {
            Logger.getLogger(MaterialBrowser.class.getName()).log(Level.FINER, "Looking for Texture: {0}", material.getAssetName());
            String[] path = ("/" + material.getAssetName()).split("/");
            TreePath parent = new TreePath((TreeNode) jTree1.getModel().getRoot());
            jTree1.expandPath(TreeUtil.buildTreePath(jTree1, parent, path, 0, true));
            jTree1.getSelectionModel().setSelectionPath(TreeUtil.buildTreePath(jTree1, parent, path, 0, false));

        } else {
            String lastSelected = prefs.get(PREF_LAST_SELECTED, null);
            if (lastSelected != null) {
                TreePath parent = new TreePath((TreeNode) jTree1.getModel().getRoot());
                TreePath selectedTreePath = TreeUtil.buildTreePath(jTree1, parent, ("/"+lastSelected).split("/"), 0, true);
                jTree1.expandPath(selectedTreePath);
            }
        }
    }

    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();

        if (node == null) {
            return;
        }

        if (node.isLeaf()) {
            String selected = TreeUtil.getPath(node.getUserObjectPath());
            selected = selected.substring(0, selected.lastIndexOf("/"));

            materialPreviewWidget1.showMaterial(assetManager, selected);

            try {
                FileReader fr = new FileReader(assetManager.getAbsoluteAssetPath(selected));

                if (fr != null) {
                    char[] b = new char[5000];//preview 5000 char
                    fr.read(b);
                    materialTextPreview.setText(new String(b).trim());
                }
                prefs.put(PREF_LAST_SELECTED, selected);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            
        } else {
            materialPreviewWidget1.clear();
            materialTextPreview.setText("");
        }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        materialPreviewWidget1 = new com.jme3.gde.materials.multiview.widgets.MaterialPreviewWidget();
        jScrollPane2 = new javax.swing.JScrollPane();
        materialTextPreview = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(MaterialBrowser.class, "MaterialBrowser.title")); // NOI18N

        jTree1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTree1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTree1);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(materialPreviewWidget1, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(materialPreviewWidget1, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        materialTextPreview.setColumns(20);
        materialTextPreview.setEditable(false);
        materialTextPreview.setRows(5);
        jScrollPane2.setViewportView(materialTextPreview);

        okButton.setText(org.openide.util.NbBundle.getMessage(MaterialBrowser.class, "MaterialBrowser.okButton.text")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(org.openide.util.NbBundle.getMessage(MaterialBrowser.class, "MaterialBrowser.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cancelButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 343, Short.MAX_VALUE)
                .addComponent(okButton)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
    if (setMaterial()) {
        dispose();
        materialPreviewWidget1.cleanUp();
    }

}//GEN-LAST:event_okButtonActionPerformed

private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    dispose();
    materialPreviewWidget1.cleanUp();
}//GEN-LAST:event_cancelButtonActionPerformed

private void jTree1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTree1MouseClicked
    if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
        if (setMaterial()) {
            dispose();
        }
    }
}//GEN-LAST:event_jTree1MouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTree jTree1;
    private com.jme3.gde.materials.multiview.widgets.MaterialPreviewWidget materialPreviewWidget1;
    private javax.swing.JTextArea materialTextPreview;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        materialPreviewWidget1.cleanUp();
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }
}
