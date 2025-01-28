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
import com.jme3.gde.core.icons.IconList;
import com.jme3.gde.core.scene.SceneApplication;
import com.jme3.gde.core.sceneexplorer.SceneExplorerTopComponent;
import com.jme3.gde.core.sceneexplorer.nodes.JmeControl;
import com.jme3.gde.core.sceneexplorer.nodes.SceneExplorerNode;
import com.jme3.gde.core.sceneexplorer.nodes.actions.ControlsPopup;
import com.jme3.gde.core.sceneexplorer.nodes.actions.animation.AnimClipProperty;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.actions.DeleteAction;
import org.openide.awt.Actions;
import org.openide.explorer.ExplorerManager;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.actions.SystemAction;
import org.openide.windows.TopComponent;

/**
 * Visual representation of the AnimComposer Class in the Scene Explorer
 * @author MeFisto94
 */
@org.openide.util.lookup.ServiceProvider(service = SceneExplorerNode.class)
@SuppressWarnings({"unchecked", "rawtypes"})
public class JmeAnimComposer extends JmeControl {
    private AnimComposer animComposer;
    private final Map<String, JmeAnimClip> playingAnimation = new HashMap<>();
    private static Image smallImage = IconList.animControl.getImage();

    public JmeAnimComposer() {
    }

    public JmeAnimComposer(AnimComposer animComposer, JmeAnimComposerChildren children, DataObject obj) {
        super(children);
        dataObject = obj;
        children.setDataObject(dataObject);
        this.animComposer = animComposer;
        lookupContents.add(this);
        lookupContents.add(animComposer);
        setName("AnimComposer");
        children.setAnimComposer(this);
        control = animComposer;
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
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setDisplayName("AnimComposer");
        set.setName(AnimComposer.class.getName());

        if (animComposer != null) {
            set.put(new AnimClipProperty(animComposer));
            set.put(makeEmbedProperty(this, JmeAnimComposer.class, float.class, "getGlobalSpeed", "setGlobalSpeed", "Global Animation Speed"));
            sheet.put(set);
        } // else: Empty Sheet
        
        return sheet;
    }

    public JmeAnimClip getPlaying(String layer) {
        return playingAnimation.get(layer);
    }
    
    public void setAnimClip(String layer, JmeAnimClip anim) {
        if (playingAnimation.get(layer) != null) {
            playingAnimation.get(layer).stop();
        }
        playingAnimation.put(layer, anim);
    }
    
    public float getGlobalSpeed() {
        return animComposer.getGlobalSpeed();
    }

    public void setGlobalSpeed(final float speed) {
        try {
            SceneApplication.getApplication().enqueue(() -> {
                animComposer.setGlobalSpeed(speed);
                return null;
            }).get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
            new ControlsPopup(this),
            new StopAllAction(),
            SystemAction.get(DeleteAction.class),
        };
    }

    @Override
    public Class getExplorerObjectClass() {
        return AnimComposer.class;
    }

    @Override
    public Class getExplorerNodeClass() {
        return JmeAnimComposer.class;
    }

    @Override
    public Node[] createNodes(Object key, DataObject key2, boolean cookie) {
        JmeAnimComposerChildren children = new JmeAnimComposerChildren(this);
        return new Node[]{ new JmeAnimComposer((AnimComposer)key, children, key2)};
    }
    
    @Override
    public void refresh(boolean immediate) {
        ((JmeAnimComposerChildren) jmeChildren).refreshChildren(immediate);
        super.refresh(immediate);
    }
   
    private class StopAllAction extends AbstractAction {

        public StopAllAction() {
            super("Stop animations");
        }
                
        @Override
        public void actionPerformed(ActionEvent e) {
            for(JmeAnimClip layer: JmeAnimComposer.this.playingAnimation.values()) {
                layer.stop();
            }
        }
    }
    
}
