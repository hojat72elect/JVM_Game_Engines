/*
 *  Copyright (c) 2009-2024jMonkeyEngine
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
package com.jme3.gde.core.sceneexplorer.nodes;

import com.jme3.gde.core.scene.SceneApplication;
import com.jme3.gde.core.sceneexplorer.nodes.actions.ControlsPopup;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.swing.Action;
import org.openide.actions.DeleteAction;
import org.openide.loaders.DataObject;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.actions.SystemAction;

/**
 * The JmeControl implements the Base Behavior of each Control-Node
 * @author MeFisto94
 */
public abstract class JmeControl extends AbstractSceneExplorerNode {

    protected Control control;
    
    public JmeControl() {
        super();
    }

    public JmeControl(Children children, DataObject dataObject) {
        super(children, dataObject);
    }

    public JmeControl(DataObject dataObject) {
        super(dataObject);
    }

    public JmeControl(Children children) {
        super(children);
    }
    
    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
                    //                    SystemAction.get(CopyAction.class),
                    //                    SystemAction.get(CutAction.class),
                    //                    SystemAction.get(PasteAction.class),
                    new ControlsPopup(this),
                    SystemAction.get(DeleteAction.class)
                };
    }

    @Override
    public boolean canDestroy() {
        return !readOnly;
    }
    
    @Override
    public void destroy() throws IOException {
        super.destroy();
        
        if (control == null || getParentNode() == null)
            return;
        
        final Spatial spat = getParentNode().getLookup().lookup(Spatial.class);
        try {
            fireSave(true);
            SceneApplication.getApplication().enqueue(() -> {
                spat.removeControl(control);
                return null;
            }).get();
            ((AbstractSceneExplorerNode) getParentNode()).refresh(true);
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    /* Controls have a parental node containing them */
    @Override
    public void fireSave(boolean modified) {
        super.fireSave(true);
        final Node parent = getParentNode();
        if (parent instanceof AbstractSceneExplorerNode par) {
            par.fireSave(modified);
        }
    }
    
    /**
     * Enable/Disable the Control.
     * This only works for extended AbstractControls!!
     * Also see: {@link #isEnabled() }
     * @param enabled Whether the Control should be enabled or disabled
     * @return If we had success (false when an Exception occurred or no {@link Control} assigned or not of type {@link AbstractControl} )
     */
    public boolean setEnabled(final boolean enabled) {
        if (!isEnableable())
            return false;
        try {
            SceneApplication.getApplication().enqueue(() -> {
                ((AbstractControl)control).setEnabled(enabled);
                return null;
            }).get();
           
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }
        
        return true;
    }
    
    /**
     * Returns whether this Control is enabled or disabled.
     * <b>Note:</b> When the Control doesn't extend AbstractControl, FALSE is returned.
     * @return -
     */
    public boolean isEnabled()
    {
        if (isEnableable()) {
            return ((AbstractControl)control).isEnabled();
        } else
            return false;
    }
    
    public boolean isEnableable() {
        return control instanceof AbstractControl;
    }
}