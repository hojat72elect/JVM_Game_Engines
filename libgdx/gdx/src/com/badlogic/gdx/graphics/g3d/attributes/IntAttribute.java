package com.badlogic.gdx.graphics.g3d.attributes;

import com.badlogic.gdx.graphics.g3d.Attribute;

public class IntAttribute extends Attribute {
    public static final String CullFaceAlias = "cullface";
    public static final long CullFace = register(CullFaceAlias);
    public int value;

    public IntAttribute(long type) {
        super(type);
    }

    public IntAttribute(long type, int value) {
        super(type);
        this.value = value;
    }

    /**
     * create a cull face attribute to be used in a material
     *
     * @param value cull face value, possible values are 0 (render both faces), GL_FRONT_AND_BACK (render nothing), GL_BACK (render
     *              front faces only), GL_FRONT (render back faces only), or -1 to inherit default
     * @return an attribute
     */
    public static IntAttribute createCullFace(int value) {
        return new IntAttribute(CullFace, value);
    }

    @Override
    public Attribute copy() {
        return new IntAttribute(type, value);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 983 * result + value;
        return result;
    }

    @Override
    public int compareTo(Attribute o) {
        if (type != o.type) return (int) (type - o.type);
        return value - ((IntAttribute) o).value;
    }
}
