package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;

/**
 * Encodes a Box2D transform. We are lazy so we only store a 4 float wide array. First two floats are the position of the
 * b2Transform struct. Next two floats are the cosine and sine of the rotation angle.
 */
public class Transform {
    public static final int POS_X = 0;
    public static final int POS_Y = 1;
    public static final int COS = 2;
    public static final int SIN = 3;

    public float[] vals = new float[4];

    private final Vector2 position = new Vector2();
    private final Vector2 orientation = new Vector2();

    public Transform() {

    }

    /**
     * Constructs a new Transform instance with the given position and angle
     *
     * @param position the position
     * @param angle    the angle in radians
     */
    public Transform(Vector2 position, float angle) {
        setPosition(position);
        setRotation(angle);
    }

    /**
     * Constructs a new Transform instance with the given position and orientation
     *
     * @param position    the position
     * @param orientation where the transform is pointing
     */
    public Transform(Vector2 position, Vector2 orientation) {
        setPosition(position);
        setOrientation(orientation);
    }

    /**
     * Transforms the given vector by this transform
     *
     * @param v the vector
     */
    public Vector2 mul(Vector2 v) {
        float x = vals[POS_X] + vals[COS] * v.x + -vals[SIN] * v.y;
        float y = vals[POS_Y] + vals[SIN] * v.x + vals[COS] * v.y;

        v.x = x;
        v.y = y;
        return v;
    }

    /**
     * @return the position, modification of the vector has no effect on the Transform
     */
    public Vector2 getPosition() {
        return position.set(vals[0], vals[1]);
    }

    /**
     * Sets the position of this transform
     *
     * @param pos the position
     */
    public void setPosition(Vector2 pos) {
        this.vals[POS_X] = pos.x;
        this.vals[POS_Y] = pos.y;
    }

    public float getRotation() {
        return (float) Math.atan2(vals[SIN], vals[COS]);
    }

    /**
     * Sets the rotation of this transform
     *
     * @param angle angle in radians
     */
    public void setRotation(float angle) {
        float c = (float) Math.cos(angle), s = (float) Math.sin(angle);
        vals[COS] = c;
        vals[SIN] = s;
    }

    /**
     * @return A vector 2 pointing to where the body is facing
     */
    public Vector2 getOrientation() {
        return orientation.set(vals[COS], vals[SIN]);
    }

    /**
     * Set where the body should "look at"
     */
    public void setOrientation(Vector2 orientation) {
        this.vals[COS] = orientation.x;
        this.vals[SIN] = orientation.y;
    }
}
