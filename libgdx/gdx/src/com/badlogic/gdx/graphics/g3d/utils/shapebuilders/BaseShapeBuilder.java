package com.badlogic.gdx.graphics.g3d.utils.shapebuilders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.FlushablePool;

/**
 * This class allows to reduce the static allocation needed for shape builders. It contains all the objects used internally by
 * shape builders.
 * <p>
 * , xoppa
 */
public class BaseShapeBuilder {
    /* Color */
    protected static final Color tmpColor0 = new Color();
    protected static final Color tmpColor1 = new Color();
    protected static final Color tmpColor2 = new Color();
    protected static final Color tmpColor3 = new Color();
    protected static final Color tmpColor4 = new Color();

    /* Vector3 */
    protected static final Vector3 tmpV0 = new Vector3();
    protected static final Vector3 tmpV1 = new Vector3();
    protected static final Vector3 tmpV2 = new Vector3();
    protected static final Vector3 tmpV3 = new Vector3();
    protected static final Vector3 tmpV4 = new Vector3();
    protected static final Vector3 tmpV5 = new Vector3();
    protected static final Vector3 tmpV6 = new Vector3();
    protected static final Vector3 tmpV7 = new Vector3();

    /* VertexInfo */
    protected static final VertexInfo vertTmp0 = new VertexInfo();
    protected static final VertexInfo vertTmp1 = new VertexInfo();
    protected static final VertexInfo vertTmp2 = new VertexInfo();
    protected static final VertexInfo vertTmp3 = new VertexInfo();
    protected static final VertexInfo vertTmp4 = new VertexInfo();
    protected static final VertexInfo vertTmp5 = new VertexInfo();
    protected static final VertexInfo vertTmp6 = new VertexInfo();
    protected static final VertexInfo vertTmp7 = new VertexInfo();
    protected static final VertexInfo vertTmp8 = new VertexInfo();

    /* Matrix4 */
    protected static final Matrix4 matTmp1 = new Matrix4();

    private final static FlushablePool<Vector3> vectorPool = new FlushablePool<Vector3>() {
        @Override
        protected Vector3 newObject() {
            return new Vector3();
        }
    };

    private final static FlushablePool<Matrix4> matrices4Pool = new FlushablePool<Matrix4>() {
        @Override
        protected Matrix4 newObject() {
            return new Matrix4();
        }
    };

    /**
     * Obtain a temporary {@link Vector3} object, must be free'd using {@link #freeAll()}.
     */
    protected static Vector3 obtainV3() {
        return vectorPool.obtain();
    }

    /**
     * Obtain a temporary {@link Matrix4} object, must be free'd using {@link #freeAll()}.
     */
    protected static Matrix4 obtainM4() {
        final Matrix4 result = matrices4Pool.obtain();
        return result;
    }

    /**
     * Free all objects obtained using one of the `obtainXX` methods.
     */
    protected static void freeAll() {
        vectorPool.flush();
        matrices4Pool.flush();
    }
}
