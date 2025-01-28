package com.jme3.gde.core.sceneexplorer.nodes.editor;

import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import java.beans.PropertyEditorSupport;

public class GravityPropertyEditor extends PropertyEditorSupport {

    private final PhysicsRigidBody instance;

    public GravityPropertyEditor(PhysicsRigidBody instance) {
        this.instance = instance;
    }

    @Override
    public String getAsText() {
        // Convert the current value to a string representation
        Vector3f gravity = (Vector3f) getValue();
        return gravity != null ? gravity.toString() : "(0.0,0.0,0.0)";
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        // Convert the given string to the property value
        // In this example, assuming the input is a comma-separated list of floats
        String[] values = text.replace("(", "").replace(")", "").split(",");
        if (values.length == 3) {
            try {
                float x = Float.parseFloat(values[0]);
                float y = Float.parseFloat(values[1]);
                float z = Float.parseFloat(values[2]);
                Vector3f gravity = new Vector3f(x, y, z);
                setValue(gravity);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid format for gravity string");
            }
        } else {
            throw new IllegalArgumentException("Invalid format for gravity string");
        }
    }

    @Override
    public void setValue(Object value) {
        // Set the value to the actual property using the unconventional setter
        // (Assuming you have access to the PhysicsRigidBody instance)
        instance.setGravity((Vector3f) value);
    }

    @Override
    public Object getValue() {
        // Retrieve the value from the actual property using the unconventional getter
        // (Assuming you have access to the PhysicsRigidBody instance)
        return instance.getGravity(new Vector3f());
    }
    
}