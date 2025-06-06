package com.badlogic.gdx.graphics.g3d.utils.shapebuilders;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ShortArray;

/**
 * Helper class with static methods to build sphere shapes using {@link MeshPartBuilder}.
 */
public class SphereShapeBuilder extends BaseShapeBuilder {
    private final static ShortArray tmpIndices = new ShortArray();
    private final static Matrix3 normalTransform = new Matrix3();

    public static void build(MeshPartBuilder builder, float width, float height, float depth, int divisionsU, int divisionsV) {
        build(builder, width, height, depth, divisionsU, divisionsV, 0, 360, 0, 180);
    }

    /**
     * @deprecated use {@link MeshPartBuilder#setVertexTransform(Matrix4)} instead of using the method signature taking a
     * matrix.
     */
    @Deprecated
    public static void build(MeshPartBuilder builder, final Matrix4 transform, float width, float height, float depth,
                             int divisionsU, int divisionsV) {
        build(builder, transform, width, height, depth, divisionsU, divisionsV, 0, 360, 0, 180);
    }

    public static void build(MeshPartBuilder builder, float width, float height, float depth, int divisionsU, int divisionsV,
                             float angleUFrom, float angleUTo, float angleVFrom, float angleVTo) {
        build(builder, matTmp1.idt(), width, height, depth, divisionsU, divisionsV, angleUFrom, angleUTo, angleVFrom, angleVTo);
    }

    /**
     * @deprecated use {@link MeshPartBuilder#setVertexTransform(Matrix4)} instead of using the method signature taking a
     * matrix.
     */
    @Deprecated
    public static void build(MeshPartBuilder builder, final Matrix4 transform, float width, float height, float depth,
                             int divisionsU, int divisionsV, float angleUFrom, float angleUTo, float angleVFrom, float angleVTo) {
        final boolean closedVFrom = MathUtils.isEqual(angleVFrom, 0f);
        final boolean closedVTo = MathUtils.isEqual(angleVTo, 180f);
        final float hw = width * 0.5f;
        final float hh = height * 0.5f;
        final float hd = depth * 0.5f;
        final float auo = MathUtils.degreesToRadians * angleUFrom;
        final float stepU = (MathUtils.degreesToRadians * (angleUTo - angleUFrom)) / divisionsU;
        final float avo = MathUtils.degreesToRadians * angleVFrom;
        final float stepV = (MathUtils.degreesToRadians * (angleVTo - angleVFrom)) / divisionsV;
        final float us = 1f / divisionsU;
        final float vs = 1f / divisionsV;
        float u = 0f;
        float v = 0f;
        float angleU = 0f;
        float angleV = 0f;
        VertexInfo curr1 = vertTmp3.set(null, null, null, null);
        curr1.hasUV = curr1.hasPosition = curr1.hasNormal = true;

        normalTransform.set(transform);

        final int s = divisionsU + 3;
        tmpIndices.clear();
        tmpIndices.ensureCapacity(divisionsU * 2);
        tmpIndices.size = s;
        int tempOffset = 0;

        builder.ensureVertices((divisionsV + 1) * (divisionsU + 1));
        builder.ensureRectangleIndices(divisionsU);
        for (int iv = 0; iv <= divisionsV; iv++) {
            angleV = avo + stepV * iv;
            v = vs * iv;
            final float t = MathUtils.sin(angleV);
            final float h = MathUtils.cos(angleV) * hh;
            for (int iu = 0; iu <= divisionsU; iu++) {
                angleU = auo + stepU * iu;
                if (iv == 0 && closedVFrom || iv == divisionsV && closedVTo) {
                    u = 1f - us * (iu - .5f);
                } else {
                    u = 1f - us * iu;
                }
                curr1.position.set(MathUtils.cos(angleU) * hw * t, h, MathUtils.sin(angleU) * hd * t);
                curr1.normal.set(curr1.position).mul(normalTransform).nor();
                curr1.position.mul(transform);
                curr1.uv.set(u, v);
                tmpIndices.set(tempOffset, builder.vertex(curr1));
                final int o = tempOffset + s;
                if ((iv > 0) && (iu > 0)) { // FIXME don't duplicate lines and points
                    if (iv == 1 && closedVFrom) {
                        builder.triangle(tmpIndices.get(tempOffset), tmpIndices.get((o - 1) % s),
                                tmpIndices.get((o - (divisionsU + 1)) % s));
                    } else if (iv == divisionsV && closedVTo) {
                        builder.triangle(tmpIndices.get(tempOffset), tmpIndices.get((o - (divisionsU + 2)) % s),
                                tmpIndices.get((o - (divisionsU + 1)) % s));
                    } else {
                        builder.rect(tmpIndices.get(tempOffset), tmpIndices.get((o - 1) % s),
                                tmpIndices.get((o - (divisionsU + 2)) % s), tmpIndices.get((o - (divisionsU + 1)) % s));
                    }
                }
                tempOffset = (tempOffset + 1) % tmpIndices.size;
            }
        }
    }
}
