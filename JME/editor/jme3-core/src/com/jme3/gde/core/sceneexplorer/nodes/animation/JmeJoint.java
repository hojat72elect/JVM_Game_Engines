/*
 *  Copyright (c) 2009-2020 jMonkeyEngine
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
package com.jme3.gde.core.sceneexplorer.nodes.animation;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.ArmatureMask;
import com.jme3.anim.Joint;
import com.jme3.gde.core.icons.IconList;
import com.jme3.gde.core.scene.SceneApplication;
import com.jme3.gde.core.sceneexplorer.nodes.AbstractSceneExplorerNode;
import com.jme3.gde.core.sceneexplorer.nodes.ClipboardSpatial;
import com.jme3.gde.core.sceneexplorer.nodes.SceneExplorerNode;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;
import javax.swing.Action;
import javax.swing.JOptionPane;
import org.openide.awt.Actions;
import org.openide.loaders.DataObject;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;

/**
 * Visual representation of the Joint Class in the Scene Explorer
 * @author MeFisto94
 */
@org.openide.util.lookup.ServiceProvider(service = SceneExplorerNode.class)
@SuppressWarnings({"unchecked", "rawtypes"})
public class JmeJoint extends AbstractSceneExplorerNode {
    private static Image smallImage = IconList.bone.getImage();
    private Joint joint;
    private JmeSkinningControl jmeSkinningControl;
    protected final DataFlavor BONE_FLAVOR = new DataFlavor(ClipboardSpatial.class, "Joint");

    public JmeJoint() {
    }

    public JmeJoint(JmeSkinningControl jmeSkinningControl, Joint joint, JmeJointChildren children) {
        super(children);
        this.jmeSkinningControl = jmeSkinningControl;
        getLookupContents().add(joint);
        getLookupContents().add(this);
        super.setName(joint.getName());
        this.joint = joint;
    }

    @Override
    public Image getIcon(int type) {
        return smallImage;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return smallImage;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setDisplayName("Joint");
        set.setName(Joint.class.getName());
        
        if (joint != null) {
            sheet.put(set);
        } // Otherwise: Empty Set
        
        return sheet;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
            Actions.alwaysEnabled(new AttachementNodeActionListener(), "Get attachement Node", "", false),
            Actions.alwaysEnabled(new ArmatureMaskActionListener(), "Create armature mask", "", false)
        };
    }

    @Override
    public Class getExplorerObjectClass() {
        return Joint.class;
    }

    @Override
    public Class getExplorerNodeClass() {
        return JmeJoint.class;
    }

    @Override
    public org.openide.nodes.Node[] createNodes(Object key, DataObject key2, boolean cookie) {
        JmeJointChildren children = new JmeJointChildren(jmeSkinningControl, (Joint)key);
        children.setReadOnly(cookie);
        children.setDataObject(key2);
        return new org.openide.nodes.Node[]{new JmeJoint(jmeSkinningControl, (Joint)key, children).setReadOnly(cookie)};
    }
    
    
    private class AttachementNodeActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            fireSave(true);
            try {
                SceneApplication.getApplication().enqueue(() -> 
                    jmeSkinningControl.getSkinningControl().getAttachmentsNode(joint.getName())
                ).get();
                
                ((AbstractSceneExplorerNode)jmeSkinningControl.getParentNode()).refresh(false);
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
    private class ArmatureMaskActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String name = JOptionPane.showInputDialog("Enter a name for the armature mask"); 
            if(name == null) {
                return;
            }
            fireSave(true);
            SceneApplication.getApplication().enqueue(() -> {
                final AnimComposer composer = jmeSkinningControl.getSkinningControl().getSpatial().getControl(AnimComposer.class);
                composer.makeLayer(name, ArmatureMask.createMask(jmeSkinningControl.getSkinningControl().getArmature(), joint.getName()));
                }
            );
            final JmeAnimComposer animComposer = (JmeAnimComposer) ((AbstractSceneExplorerNode)jmeSkinningControl.getParentNode()).getChildren().findChild("AnimComposer");
            animComposer.refresh(true);
            animComposer.fireSave(true);
        }
        
    }
}
