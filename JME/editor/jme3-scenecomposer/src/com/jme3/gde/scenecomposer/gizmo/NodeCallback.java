/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.scenecomposer.gizmo;

import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * This Class allows "hooking" transformation events of a Node.
 * As such it is used to update gizmos and others to react to
 * Property Panel Input.
 * 
 * @author dokthar
 */
public abstract class NodeCallback extends Node {

    private final boolean applyTranslation;
    private final boolean applyRotation;
    private final boolean applyScale;

    public NodeCallback(String name) {
        this(name, true, true, true);
    }

    public NodeCallback(String name, boolean applyTranslation, boolean applyRotation, boolean applyScale) {
        super(name);
        this.applyTranslation = applyTranslation;
        this.applyRotation = applyRotation;
        this.applyScale = applyScale;
    }

    @Override
    public void setLocalRotation(Matrix3f rotation) {
        onRotation(getLocalRotation(), new Quaternion().fromRotationMatrix(rotation));
        if (applyRotation) {
            super.setLocalRotation(rotation);
        }
    }

    @Override
    public void setLocalRotation(Quaternion quaternion) {
        onRotation(getLocalRotation(), quaternion);
        if (applyRotation) {
            super.setLocalRotation(quaternion);
        }
    }

    @Override
    public void setLocalScale(Vector3f localScale) {
        onResize(getLocalScale(), localScale);
        if (applyScale) {
            super.setLocalScale(localScale);
        }
    }

    @Override
    public void setLocalScale(float localScale) {
        onResize(getLocalScale(), new Vector3f(localScale, localScale, localScale));
        if (applyScale) {
            super.setLocalScale(localScale);
        }
    }

    @Override
    public void setLocalScale(float x, float y, float z) {
        onResize(getLocalScale(), new Vector3f(x, y, z));
        if (applyScale) {
            super.setLocalScale(x, y, z);
        }
    }

    @Override
    public void setLocalTransform(Transform t) {
        onTranslation(getLocalTranslation(), t.getTranslation());
        onRotation(getLocalRotation(), t.getRotation());
        onResize(getLocalScale(), t.getScale());

        if (applyRotation || applyScale || applyTranslation) {
            super.setLocalTransform(t);
        }
    }

    @Override
    public void setLocalTranslation(Vector3f localTranslation) {
        onTranslation(getLocalTranslation(), localTranslation);
        if (applyTranslation) {
            super.setLocalTranslation(localTranslation);
        }
    }

    @Override
    public void setLocalTranslation(float x, float y, float z) {
        onTranslation(getLocalTranslation(), new Vector3f(x, y, z));
        if (applyTranslation) {
            super.setLocalTranslation(x, y, z);
        }
    }

    @Override
    public Spatial move(Vector3f offset) {
        onTranslation(getLocalTranslation(), getLocalTranslation().add(offset));
        if (applyTranslation) {
            super.move(offset);
        }
        return this;
    }

    @Override
    public Spatial move(float x, float y, float z) {
        onTranslation(getLocalTranslation(), getLocalTranslation().add(x, y, z));
        if (applyTranslation) {
            super.move(x, y, z);
        }
        return this;
    }

    @Override
    public Spatial rotate(Quaternion rot) {
        onRotation(getLocalRotation(), getLocalRotation().mult(rot));
        if (applyRotation) {
            super.rotate(rot);
        }
        return this;
    }

    @Override
    public Spatial scale(float s) {
        onResize(getLocalScale(), getLocalScale().mult(s));
        if (applyScale) {
            super.scale(s);
        }
        return this;
    }

    @Override
    public Spatial scale(float x, float y, float z) {
        onResize(getLocalScale(), getLocalScale().mult(new Vector3f(x, y, z)));
        if (applyScale) {
            super.scale(x, y, z);
        }
        return this;
    }

    public void silentLocalTranslation(Vector3f translation) {
        super.setLocalTranslation(translation);
    }

    public void silentLocalRotation(Quaternion rotation) {
        super.setLocalRotation(rotation);
    }

    public void silentLocalScale(Vector3f scale) {
        super.setLocalScale(scale);
    }

    public abstract void onTranslation(Vector3f oldTranslation, Vector3f newTranslation);

    public abstract void onResize(Vector3f oldScale, Vector3f newScale);

    public abstract void onRotation(Quaternion oldRotation, Quaternion newRotation);

}
