/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.scenecomposer.gizmo;

import com.jme3.asset.AssetManager;
import com.jme3.gde.core.sceneexplorer.nodes.AbstractSceneExplorerNode;
import com.jme3.gde.core.sceneexplorer.nodes.JmeAudioNode;
import com.jme3.gde.core.sceneexplorer.nodes.JmeLight;
import com.jme3.gde.scenecomposer.gizmo.audio.AudioGizmoFactory;
import com.jme3.gde.scenecomposer.gizmo.light.LightGizmoFactory;
import com.jme3.scene.Spatial;

/**
 *
 * @author dokthar
 */
public class GizmoFactory {

    public static Spatial createGizmo(AssetManager assetManager, AbstractSceneExplorerNode node) {
        if (node instanceof JmeLight) {
            return LightGizmoFactory.createGizmo(assetManager, (JmeLight) node);
        } else if (node instanceof JmeAudioNode) {
            return AudioGizmoFactory.createGizmo(assetManager, (JmeAudioNode) node);
        }

        return null;
    }

}
