/*
 *  Copyright (c) 2009-2019 jMonkeyEngine
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
package com.jme3.gde.core.sceneexplorer.nodes.actions;

import com.jme3.gde.core.assets.ProjectAssetManager;
import com.jme3.gde.core.scene.SceneApplication;
import com.jme3.gde.core.sceneexplorer.nodes.JmeNode;
import com.jme3.gde.core.undoredo.AbstractUndoableSceneEdit;
import com.jme3.gde.core.undoredo.SceneUndoRedoManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.awt.event.ActionEvent;
import java.util.concurrent.Callable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;

/**
 *
 * @author normenhansen
 */
public abstract class AbstractNewSpatialAction implements NewSpatialAction {

    protected String name = "*";
    protected ProjectAssetManager pm;

    /**
     * Implement this method to create a spatial which can be added to 
     * the Scene Graph as Spatial.
     * @param parent The Parent Node.
     * @return The newly created spatial
     */
    protected abstract Spatial doCreateSpatial(Node parent);
    
    /**
     * Implement this method to prepare creating a spatial (things than can be
     * done outside of the RenderThread like Dialogs, Loading, ...)
     * @return Whether the call to {@link #doCreateSpatial(com.jme3.scene.Node) } shall be done.
     */
    protected abstract boolean prepareCreateSpatial();

    protected Action makeAction(final JmeNode rootNode, final DataObject dataObject) {
        pm = rootNode.getLookup().lookup(ProjectAssetManager.class);
        final Node node = rootNode.getLookup().lookup(Node.class);
        return new AbstractAction(name) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (prepareCreateSpatial()) {
                    SceneApplication.getApplication().enqueue(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            final Spatial attachSpatial = doCreateSpatial(node);
                            if (node != null && attachSpatial != null) {
                                node.attachChild(attachSpatial);
                                Lookup.getDefault().lookup(SceneUndoRedoManager.class).addEdit(this, new AbstractUndoableSceneEdit() {

                                    @Override
                                    public void sceneUndo() throws CannotUndoException {
                                        attachSpatial.removeFromParent();
                                    }

                                    @Override
                                    public void sceneRedo() throws CannotRedoException {
                                        node.attachChild(attachSpatial);
                                    }

                                    @Override
                                    public void awtRedo() {
                                        dataObject.setModified(true);
                                        rootNode.refresh(true);
                                    }

                                    @Override
                                    public void awtUndo() {
                                        dataObject.setModified(true);
                                        rootNode.refresh(true);
                                    }
                                });
                                setModified();
                            }
                            return null;
                        }
                    });
                }
            }

            protected void setModified() {
                java.awt.EventQueue.invokeLater(() -> {
                    dataObject.setModified(true);
                    rootNode.refresh(true);
                });
            }
        };
    }

    @Override
    public Action getAction(JmeNode rootNode, DataObject dataObject) {
        return makeAction(rootNode, dataObject);
    }
}
