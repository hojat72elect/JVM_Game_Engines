package com.badlogic.gdx.graphics.g3d.attributes;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.NumberUtils;

public class DepthTestAttribute extends Attribute {
    public final static String Alias = "depthStencil";
    public final static long Type = register(Alias);

    protected static long Mask = Type;
    /**
     * The depth test function, or 0 to disable depth test (default: GL10.GL_LEQUAL)
     */
    public int depthFunc;
    /**
     * Mapping of near clipping plane to window coordinates (default: 0)
     */
    public float depthRangeNear;
    /**
     * Mapping of far clipping plane to window coordinates (default: 1)
     */
    public float depthRangeFar;
    /**
     * Whether to write to the depth buffer (default: true)
     */
    public boolean depthMask;
    public DepthTestAttribute() {
        this(GL20.GL_LEQUAL);
    }

    public DepthTestAttribute(boolean depthMask) {
        this(GL20.GL_LEQUAL, depthMask);
    }

    public DepthTestAttribute(final int depthFunc) {
        this(depthFunc, true);
    }

    public DepthTestAttribute(int depthFunc, boolean depthMask) {
        this(depthFunc, 0, 1, depthMask);
    }

    public DepthTestAttribute(int depthFunc, float depthRangeNear, float depthRangeFar) {
        this(depthFunc, depthRangeNear, depthRangeFar, true);
    }

    public DepthTestAttribute(int depthFunc, float depthRangeNear, float depthRangeFar, boolean depthMask) {
        this(Type, depthFunc, depthRangeNear, depthRangeFar, depthMask);
    }

    public DepthTestAttribute(final long type, int depthFunc, float depthRangeNear, float depthRangeFar, boolean depthMask) {
        super(type);
        if (!is(type)) throw new GdxRuntimeException("Invalid type specified");
        this.depthFunc = depthFunc;
        this.depthRangeNear = depthRangeNear;
        this.depthRangeFar = depthRangeFar;
        this.depthMask = depthMask;
    }

    public DepthTestAttribute(final DepthTestAttribute rhs) {
        this(rhs.type, rhs.depthFunc, rhs.depthRangeNear, rhs.depthRangeFar, rhs.depthMask);
    }

    public final static boolean is(final long mask) {
        return (mask & Mask) != 0;
    }

    @Override
    public Attribute copy() {
        return new DepthTestAttribute(this);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 971 * result + depthFunc;
        result = 971 * result + NumberUtils.floatToRawIntBits(depthRangeNear);
        result = 971 * result + NumberUtils.floatToRawIntBits(depthRangeFar);
        result = 971 * result + (depthMask ? 1 : 0);
        return result;
    }

    @Override
    public int compareTo(Attribute o) {
        if (type != o.type) return (int) (type - o.type);
        DepthTestAttribute other = (DepthTestAttribute) o;
        if (depthFunc != other.depthFunc) return depthFunc - other.depthFunc;
        if (depthMask != other.depthMask) return depthMask ? -1 : 1;
        if (!MathUtils.isEqual(depthRangeNear, other.depthRangeNear))
            return depthRangeNear < other.depthRangeNear ? -1 : 1;
        if (!MathUtils.isEqual(depthRangeFar, other.depthRangeFar)) return depthRangeFar < other.depthRangeFar ? -1 : 1;
        return 0;
    }
}
