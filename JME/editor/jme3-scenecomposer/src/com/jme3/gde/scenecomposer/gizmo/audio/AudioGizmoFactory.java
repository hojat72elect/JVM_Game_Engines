/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.scenecomposer.gizmo.audio;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.gde.core.sceneexplorer.nodes.JmeAudioNode;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;

/**
 *
 * @author dokthar
 */
public class AudioGizmoFactory {

    public static Spatial createGizmo(AssetManager assetManager, JmeAudioNode node) {
        AudioNode audio = node.getLookup().lookup(AudioNode.class);
        if (audio == null) {
            return null;
        }
        
        return createAudioGizmo(assetManager, node, audio);

        // This marker is not part of the scene, but is part of the tools node. 
        //return audio;
    }

    private static Spatial createAudioGizmo(AssetManager assetManager, JmeAudioNode node, AudioNode audio) {
        Node gizmo = new Node("Audio node Gizmo");
        Spatial marker = createAudioMarker(assetManager);
        gizmo.attachChild(marker);
        
        gizmo.addControl(new AudioMarkerControl(audio));
        return gizmo;
    }
    
    /**
     * Updates the marker's position whenever the audio node has moved. It is
     * also a BillboardControl, so this marker always faces the camera
     */
    protected static class AudioMarkerControl extends BillboardControl {

        private final AudioNode audio;
        private final Vector3f lastPos = new Vector3f();
        private final Vector3f audioPos;

        AudioMarkerControl(AudioNode a) {
            super();
            audio = a;
            audioPos = audio.getPosition();
        }

        @Override
        protected void controlUpdate(float f) {
            super.controlUpdate(f);
            Spatial marker = getSpatial();
            if (marker != null && !audioPos.equals(lastPos)) {
                lastPos.set(audioPos);
                marker.getParent().worldToLocal(lastPos, lastPos);
                marker.setLocalTranslation(lastPos);
            }
        }

        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {
            super.controlRender(rm, vp);
        }
    }

    /**
     * A marker on the screen that shows where an audio node is.
     */
    protected static Geometry createAudioMarker(AssetManager assetManager) {
        Quad q = new Quad(0.5f, 0.5f);
        Geometry audioMarker = new Geometry("light bulb", q);
        audioMarker.move(-q.getHeight() / 2f, -q.getWidth() / 2f, 0);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = assetManager.loadTexture("com/jme3/gde/scenecomposer/audionode.gif");
        mat.setTexture("ColorMap", tex);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        audioMarker.setMaterial(mat);
        audioMarker.setQueueBucket(RenderQueue.Bucket.Transparent);

        return audioMarker;
    }
}
