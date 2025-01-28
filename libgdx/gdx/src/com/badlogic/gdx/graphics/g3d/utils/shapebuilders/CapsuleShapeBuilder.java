package com.badlogic.gdx.graphics.g3d.utils.shapebuilders;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;

import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Helper class with static methods to build capsule shapes using {@link MeshPartBuilder}.
 *
 * 
 */
public class CapsuleShapeBuilder extends BaseShapeBuilder {
    public static void build(MeshPartBuilder builder, float radius, float height, int divisions) {
        if (height < 2f * radius) throw new GdxRuntimeException("Height must be at least twice the radius");
        final float d = 2f * radius;
        CylinderShapeBuilder.build(builder, d, height - d, d, divisions, 0, 360, false);
        SphereShapeBuilder.build(builder, matTmp1.setToTranslation(0, .5f * (height - d), 0), d, d, d, divisions, divisions, 0, 360,
                0, 90);
        SphereShapeBuilder.build(builder, matTmp1.setToTranslation(0, -.5f * (height - d), 0), d, d, d, divisions, divisions, 0,
                360, 90, 180);
    }
}
