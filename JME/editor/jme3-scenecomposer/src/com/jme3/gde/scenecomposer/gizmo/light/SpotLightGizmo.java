/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.scenecomposer.gizmo.light;

import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.gde.core.sceneexplorer.nodes.JmeSpotLight;
import com.jme3.gde.scenecomposer.gizmo.NodeCallback;
import com.jme3.light.SpotLight;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 *
 * @author dokthar
 */
public class SpotLightGizmo extends NodeCallback {

    private final SpotLight light;
    private final JmeSpotLight jmeLight;

    public SpotLightGizmo(JmeSpotLight jmelight) {
        super("spot light callback", true, true, false);
        jmeLight = jmelight;
        light = jmeLight.getLookup().lookup(SpotLight.class);
    }

    @Override
    public void onTranslation(Vector3f oldTranslation, Vector3f newTranslation) {
        light.setPosition(getWorldTranslation());
        jmeLight.setValue("position", light.getPosition());
    }

    private final float eps = 0.0000125f;

    @Override
    public void onResize(Vector3f oldScale, Vector3f newScale) {
        float x = FastMath.abs(newScale.x);
        float y = FastMath.abs(newScale.y);
        float z = FastMath.abs(newScale.z);

        light.setSpotRange(y);
        light.setSpotInnerAngle(x);
        light.setSpotOuterAngle(z);

        jmeLight.setValue("spotInnerAngle", light.getSpotInnerAngle());
        jmeLight.setValue("spotOuterAngle", light.getSpotOuterAngle());
        jmeLight.setValue("spotRange", light.getSpotRange());
    }

    @Override
    public void onRotation(Quaternion oldRotation, Quaternion newRotation) {
        light.setDirection(newRotation.mult(Vector3f.UNIT_Y));
        jmeLight.setValue("direction", light.getDirection());
    }

    @Override
    public Vector3f getLocalScale() {
        float i = light.getSpotInnerAngle();
        float r = light.getSpotRange();
        float o = light.getSpotOuterAngle();
        return new Vector3f(i, r, o);
    }

    @Override
    public Vector3f getWorldScale() {
        float i = light.getSpotInnerAngle();
        float r = light.getSpotRange();
        float o = light.getSpotOuterAngle();
        return new Vector3f(i, r, o);
    }

    @Override
    public BoundingVolume getWorldBound() {
        return new BoundingSphere(light.getSpotRange(), light.getPosition());
    }

}
