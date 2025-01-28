/*
 *  Copyright (c) 2009-2024 jMonkeyEngine
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
import com.jme3.anim.AnimLayer;
import com.jme3.gde.core.icons.IconList;
import com.jme3.gde.core.scene.SceneApplication;
import com.jme3.gde.core.sceneexplorer.nodes.AbstractSceneExplorerNode;
import com.jme3.gde.core.sceneexplorer.nodes.SceneExplorerNode;
import java.awt.Image;
import java.io.IOException;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import org.openide.actions.DeleteAction;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.actions.SystemAction;


/**
 * Representation of an AnimComposers AnimLayer
 * @author rickard
 */
@org.openide.util.lookup.ServiceProvider(service = SceneExplorerNode.class)
public class JmeAnimLayer extends AbstractSceneExplorerNode {
    
    private Image icon;
    private JmeAnimComposer jmeControl;
    private AnimLayer layer;
    
    public JmeAnimLayer() {
        
    }

    public JmeAnimLayer(JmeAnimComposer animComposer, AnimLayer layer, DataObject dataObject) {
        super();
        this.jmeControl = animComposer;
        this.layer = layer;
        this.dataObject = dataObject;
        lookupContents.add(this);
        lookupContents.add(layer);
        setName(layer.getName());
        icon = IconList.important.getImage();
    }
    
    @Override
    public Image getIcon(int type) {
        return icon;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return icon;
    }
    
    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setDisplayName("AnimLayer");
        set.setName(AnimLayer.class.getName());
        if (layer != null) {
            sheet.put(set);
        } // else: Empty Sheet
        
        return sheet;
    }
    
    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
            SystemAction.get(DeleteAction.class),
        };
    }

    @Override
    public boolean canDestroy() {
         return !getName().equals((AnimComposer.DEFAULT_LAYER)) && !jmeControl.isReadOnly();
    }
    
    @Override
    public void destroy() throws IOException {
        super.destroy();  
        final AnimComposer control = jmeControl.getLookup().lookup(AnimComposer.class);

        lookupContents.remove(JmeAnimLayer.this.layer);
        lookupContents.remove(this);
        SceneApplication.getApplication().enqueue( () -> {
            control.removeLayer(this.getName());
            SwingUtilities.invokeLater(() -> jmeControl.refresh(false));
        });
        setChanged();
    }
    
    public void setChanged() {
        fireSave(true);
    }
    
    @Override
    public Class getExplorerObjectClass() {
        return AnimLayer.class;
    }
    
    @Override
    public Class getExplorerNodeClass() {
        return JmeAnimLayer.class;
    }
    
    @Override
    public Node[] createNodes(Object key, DataObject key2, boolean cookie) {
        JmeAnimLayer jsc = new JmeAnimLayer(jmeControl, (AnimLayer)key, key2);
        return new Node[]{jsc};
    }
}
