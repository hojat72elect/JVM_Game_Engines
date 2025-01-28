package com.badlogic.gdx.graphics.g3d.model.data;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class ModelNodeAnimation {
    /**
     * the id of the node animated by this animation FIXME should be nodeId
     **/
    public String nodeId;
    /**
     * the keyframes, defining the translation of a node for a specific timestamp
     **/
    public Array<ModelNodeKeyframe<Vector3>> translation;
    /**
     * the keyframes, defining the rotation of a node for a specific timestamp
     **/
    public Array<ModelNodeKeyframe<Quaternion>> rotation;
    /**
     * the keyframes, defining the scaling of a node for a specific timestamp
     **/
    public Array<ModelNodeKeyframe<Vector3>> scaling;
}
