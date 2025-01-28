/*
 *  Copyright (c) 2009-2023 jMonkeyEngine
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
package com.jme3.gde.scenecomposer;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * In order to display the Camera Position and LookAt in Realtime,
 * we attach this AppState, so we have a callback on each frame
 * @author MeFisto94
 */
public class CameraPositionTrackerAppState extends BaseAppState {
    private final JLabel lblPos;
    private final JLabel lblLookAt;
    
    private final Vector3f position = new Vector3f();
    private final Vector3f direction = new Vector3f();
    
    public CameraPositionTrackerAppState(JLabel lblPos, JLabel lblLookAt) {
        this.lblPos = lblPos;
        this.lblLookAt = lblLookAt;
    }

    @Override
    protected void initialize(Application aplctn) { }

    @Override
    protected void cleanup(Application aplctn) { }

    @Override
    protected void onEnable() { }

    @Override
    protected void onDisable() { }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        
        final Camera cam = getApplication().getCamera();
        SwingUtilities.invokeLater(() -> {
            position.set(cam.getLocation());
            direction.set(cam.getDirection());
            lblPos.setText(SceneComposerUtil.trimDecimals(position));
            lblLookAt.setText(SceneComposerUtil.trimDecimals(direction));
        });
    }
    
}
