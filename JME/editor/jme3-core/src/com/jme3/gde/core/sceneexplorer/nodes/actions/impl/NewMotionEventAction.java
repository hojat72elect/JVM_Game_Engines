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
package com.jme3.gde.core.sceneexplorer.nodes.actions.impl;

import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.gde.core.sceneexplorer.nodes.actions.AbstractNewControlAction;
import com.jme3.gde.core.sceneexplorer.nodes.actions.NewControlAction;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

/**
 * This Action is responsible to create a new Entry under "New Control ->"
 * @author MeFisto94
 */
@org.openide.util.lookup.ServiceProvider(service = NewControlAction.class)
public class NewMotionEventAction extends AbstractNewControlAction {

    public NewMotionEventAction() {
        name = "Motion Event";
    }

    @Override
    protected Control doCreateControl(Spatial spatial) {
        MotionEvent control = spatial.getControl(MotionEvent.class);
        if (control != null) {
            spatial.removeControl(control);
        }
        
        if (spatial.getParent() == null)
            spatial.removeFromParent(); // disallow the rootNode
        
        control = new MotionEvent();
        control.setLookAt(Vector3f.UNIT_Z, Vector3f.UNIT_Y); // TODO: DELETE WHEN ALPHA-4 IS OUT!!
        control.setRotation(Quaternion.IDENTITY); // TODO: DELETE WHEN ALPHA-4 IS OUT!!
        MotionPath mPath = new MotionPath();
        mPath.addWayPoint(Vector3f.ZERO.clone());
        mPath.addWayPoint(new Vector3f(0f, 1f, 0f));
        mPath.addWayPoint(new Vector3f(1f, 0f, 1f));
        control.setPath(mPath);
       
        return control;
    }
}
