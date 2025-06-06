package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.JointDef.JointType;

public abstract class Joint {
    // @off
	/*JNI
#include <Box2D/Box2D.h> 
	 */
    /**
     * world
     **/
    private final World world;
    /**
     * temporary float array
     **/
    private final float[] tmp = new float[2];
    /**
     * Get the anchor point on bodyA in world coordinates.
     */
    private final Vector2 anchorA = new Vector2();
    /**
     * Get the anchor point on bodyB in world coordinates.
     */
    private final Vector2 anchorB = new Vector2();
    /**
     * Get the reaction force on body2 at the joint anchor in Newtons.
     */
    private final Vector2 reactionForce = new Vector2();
    /**
     * the address of the joint
     **/
    protected long addr;
    /**
     * joint edge a
     **/
    protected JointEdge jointEdgeA;
    /**
     * joint edge b
     **/
    protected JointEdge jointEdgeB;
    /**
     * user data
     **/
    private Object userData;

    /**
     * Constructs a new joint
     *
     * @param addr the address of the joint
     */
    protected Joint(World world, long addr) {
        this.world = world;
        this.addr = addr;
    }

    /**
     * Get the type of the concrete joint.
     */
    public JointType getType() {
        int type = jniGetType(addr);
        if (type > 0 && type < JointType.valueTypes.length)
            return JointType.valueTypes[type];
        else
            return JointType.Unknown;
    }

    private native int jniGetType(long addr); /*
		b2Joint* joint = (b2Joint*)addr;
		return joint->GetType();
	*/

    /**
     * Get the first body attached to this joint.
     */
    public Body getBodyA() {
        return world.bodies.get(jniGetBodyA(addr));
    }

    private native long jniGetBodyA(long addr); /*
		b2Joint* joint = (b2Joint*)addr;
		return (jlong)joint->GetBodyA();
	*/

    /**
     * Get the second body attached to this joint.
     */
    public Body getBodyB() {
        return world.bodies.get(jniGetBodyB(addr));
    }

    private native long jniGetBodyB(long addr); /*
		b2Joint* joint = (b2Joint*)addr;
		return (jlong)joint->GetBodyB();
	*/

    public Vector2 getAnchorA() {
        jniGetAnchorA(addr, tmp);
        anchorA.x = tmp[0];
        anchorA.y = tmp[1];
        return anchorA;
    }

    private native void jniGetAnchorA(long addr, float[] anchorA); /*
		b2Joint* joint = (b2Joint*)addr;
		b2Vec2 a = joint->GetAnchorA();
		anchorA[0] = a.x;
		anchorA[1] = a.y;
	*/

    public Vector2 getAnchorB() {
        jniGetAnchorB(addr, tmp);
        anchorB.x = tmp[0];
        anchorB.y = tmp[1];
        return anchorB;
    }

    private native void jniGetAnchorB(long addr, float[] anchorB); /*
		b2Joint* joint = (b2Joint*)addr;
		b2Vec2 a = joint->GetAnchorB();
		anchorB[0] = a.x;
		anchorB[1] = a.y;
	*/

    public boolean getCollideConnected() {
        return jniGetCollideConnected(addr);
    }

    private native boolean jniGetCollideConnected(long addr); /*
		b2Joint* joint = (b2Joint*) addr;
		return joint->GetCollideConnected();
	*/

    public Vector2 getReactionForce(float inv_dt) {
        jniGetReactionForce(addr, inv_dt, tmp);
        reactionForce.x = tmp[0];
        reactionForce.y = tmp[1];
        return reactionForce;
    }

    private native void jniGetReactionForce(long addr, float inv_dt, float[] reactionForce); /*
		b2Joint* joint = (b2Joint*)addr;
		b2Vec2 f = joint->GetReactionForce(inv_dt);
		reactionForce[0] = f.x;
		reactionForce[1] = f.y;
	*/

    /**
     * Get the reaction torque on body2 in N*m.
     */
    public float getReactionTorque(float inv_dt) {
        return jniGetReactionTorque(addr, inv_dt);
    }

    private native float jniGetReactionTorque(long addr, float inv_dt); /*
		b2Joint* joint = (b2Joint*)addr;
		return joint->GetReactionTorque(inv_dt);
	*/

// /// Get the next joint the world joint list.
// b2Joint* GetNext();
//

    /**
     * Get the user data
     */
    public Object getUserData() {
        return userData;
    }

    /**
     * Set the user data
     */
    public void setUserData(Object userData) {
        this.userData = userData;
    }

    /**
     * Short-cut function to determine if either body is inactive.
     */
    public boolean isActive() {
        return jniIsActive(addr);
    }

    private native boolean jniIsActive(long addr); /*
		b2Joint* joint = (b2Joint*)addr;
		return joint->IsActive();
	*/
}
