/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.scenecomposer.gizmo.light;

import com.jme3.light.PointLight;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author dokthar
 */
public class LightRadiusUpdate extends AbstractControl{
    
    private PointLight light;
    private float lastRad = -1f;
    

    public LightRadiusUpdate(PointLight l) {
        light = l; 
    }

    @Override
    protected void controlUpdate(float f) {
        float r = light.getRadius();
        
        if (lastRad != r) {
            lastRad = r;
            if(getSpatial() != null){
                getSpatial().setLocalScale(lastRad);
            }
        }
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
    
}
