package com.jme3.gde.core.sceneexplorer.nodes.gizmo;

import com.jme3.math.Vector3f;

/**
 * This is the Interface of DirectionalLightGizmo. It has to be used because
 * Gizmos are part of the SceneComposer Module but some classes which call them
 * are still part of the Core Module. This means the Core Module would induce
 * a dependency on the SceneComposer which is undesired.
 * On the long view one should move all scene composer classes in core to the
 * SceneComposer Module.
 * 
 * Actually: The Gizmos are SceneComposer (the 3d view) whereas the Nodes
 * of the SceneGraph are in the SceneExplorer (the tree view). Both could be 
 * merged into a Scene Module
 * 
 * @author MeFisto94
 */


public interface DirectionalLightGizmoInterface {
    public void onSetDirection(Vector3f direction);
}
