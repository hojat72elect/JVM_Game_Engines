/*
 *  Copyright (c) 2009-2016 jMonkeyEngine
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

import com.jme3.cinematic.events.AbstractCinematicEvent;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.gde.core.icons.IconList;
import java.awt.Image;
import java.io.IOException;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;

/**
 * This is the SceneExplorer Node (Display Component Class) for Motion Events
 *
 * @author MeFisto94
 */
@org.openide.util.lookup.ServiceProvider(service = SceneExplorerNode.class)
@SuppressWarnings({"unchecked", "rawtypes", "LeakingThisInConstructor"})
public class JmeMotionEvent extends JmeControl {

    private MotionEvent motionEvent;
    private static Image smallImage = IconList.motionEvent.getImage();

    public JmeMotionEvent() {
        super();
    }

    public JmeMotionEvent(MotionEvent motionEvent, DataObject dataObject, JmeMotionPathChildren children) {
        super(children, dataObject);
        this.motionEvent = motionEvent;
        control = motionEvent; // to have JmeControl work

        lookupContents.add(this);
        lookupContents.add(control);
        lookupContents.add(children);
        setName("MotionEvent");
        setDisplayName("Motion Event");
        children.setMotionEventControl(this);
    }

    @Override
    public Image getIcon(int type) {
        return smallImage;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return smallImage;
    }

    /**
     * This method creates the Property Sheet (i.e. the Contents for the
     * Properties Editor).<br>See {@link AbstractNode#createSheet() } for more
     * information
     *
     * @return The created Property Sheet
     */
    @Override
    protected Sheet createSheet() {
        Sheet sheet = new Sheet(); // Sheet.createDefault(); // Create a sheet with an empty set.

        Sheet.Set abstractSet = Sheet.createPropertiesSet();
        abstractSet.setName("AbstractCinematicEvent");
        abstractSet.setDisplayName("Abstract Cinematic Event (Superclass)");
        abstractSet.setShortDescription("This is the Superclass of MotionEvent: The Abstract Cinematic Event");

        createFields(AbstractCinematicEvent.class, abstractSet, motionEvent);
        sheet.put(abstractSet);

        Sheet.Set set = Sheet.createPropertiesSet(); // Create a Properties "Set"/Entry for that Sheet. (A category so to say)
        set.setDisplayName("Motion Event");
        set.setShortDescription("These are the Properties of this Motion Event");
        set.setName(MotionEvent.class.getName());

        MotionEvent obj = motionEvent;
        if (obj == null) {
            return sheet;
        }

        createFields(MotionEvent.class, set, obj);
        set.remove("Spatial"); // since we're a Control we don't set that value, we just belong to it.
        set.remove("Path");

        sheet.put(set);

        return sheet;

    }

    /**
     * Returns the class of the underlying Object of this Node.<br>This is how we
     * are related to things found in the SceneGraph
     *
     * @return {@link Class}
     */
    @Override
    public Class getExplorerObjectClass() {
        return MotionEvent.class;
    }

    /**
     * Returns the class of this Node
     *
     * @return {@link Class}
     */
    @Override
    public Class getExplorerNodeClass() {
        return JmeMotionEvent.class;
    }

    public MotionEvent getMotionEvent() {
        return motionEvent;
    }

    @Override
    public org.openide.nodes.Node[] createNodes(Object key, DataObject key2, boolean cookie) {
        JmeMotionPathChildren children = new JmeMotionPathChildren();
        children.setReadOnly(cookie);
        return new org.openide.nodes.Node[]{new JmeMotionEvent((MotionEvent) key, key2, children).setReadOnly(cookie)}; // If we would return null here, we would have the JmeControl default (i.e. auto-exposure of properties, no icon but also no createSheet method)
    }

    public void refreshChildren() {
        ((JmeMotionPathChildren) this.jmeChildren).refreshChildren(true);
        for (Object node : getChildren().getNodes()) {
            JmeMotionPath mPath = (JmeMotionPath) node;
            ((JmeVector3fChildren) mPath.getChildren()).refreshChildren(true);
        }
    }

    @Override
    public void destroy() throws IOException {
        for (Node n : getChildren().getNodes()) {
            ((JmeMotionPath) n).destroy();
        }
        super.destroy();
    }

    public void setModified(boolean immediate) {
        dataObject.setModified(immediate);
    }

    @Override
    public boolean isEnableable() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        if (motionEvent == null) {
            return false;
        } else {
            return motionEvent.isEnabled();
        }
    }

    @Override
    public boolean setEnabled(boolean enabled) {
        if (motionEvent == null) {
            return false;
        } else {
            motionEvent.setEnabled(enabled);
            return true;
        }
    }
}
