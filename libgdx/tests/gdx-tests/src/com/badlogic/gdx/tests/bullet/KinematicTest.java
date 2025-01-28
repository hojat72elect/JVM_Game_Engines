package com.badlogic.gdx.tests.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;


public class KinematicTest extends BaseBulletTest {
    final static Vector3 position1 = new Vector3(5f, 0.5f, 0f);
    final static Vector3 position2 = new Vector3(8f, 0.5f, 0f);
    final static Vector3 position3 = new Vector3(10f, 0.5f, 0f);
    BulletEntity kinematicBox, kinematicBox1, kinematicBox2, kinematicBox3;
    Vector3 position;
    float angle;

    @Override
    public void create() {
        super.create();

        // Create the entities
        world.add("ground", 0f, 0f, 0f).setColor(0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(),
                0.25f + 0.5f * (float) Math.random(), 1f);

        kinematicBox1 = world.add("staticbox", position1.x, position1.y, position1.z);
        kinematicBox1.setColor(Color.RED);
        kinematicBox1.body
                .setCollisionFlags(kinematicBox1.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
        kinematicBox2 = world.add("staticbox", position2.x, position2.y, position2.z);
        kinematicBox2.setColor(Color.RED);
        kinematicBox2.body
                .setCollisionFlags(kinematicBox2.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
        ;
        kinematicBox3 = world.add("staticbox", position3.x, position3.y, position3.z);
        kinematicBox3.setColor(Color.RED);
        kinematicBox3.body
                .setCollisionFlags(kinematicBox3.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
        ;
        // This makes bullet call btMotionState#getWorldTransform on every update:
        kinematicBox3.body.setActivationState(Collision.DISABLE_DEACTIVATION);
        angle = 360f;
    }

    @Override
    public void render() {
        angle = angle + Gdx.graphics.getDeltaTime() * 360f / 5f;
        kinematicBox3.transform.idt().rotate(Vector3.Y, 360f - 2f * angle).translate(position3);

        if (angle >= 360f) {
            angle = 0;
            kinematicBox = (kinematicBox == kinematicBox1) ? kinematicBox2 : kinematicBox1;
            position = (position == position1) ? position2 : position1;
        }
        kinematicBox.transform.idt().rotate(Vector3.Y, angle).translate(position);
        // This makes bullet call btMotionState#getWorldTransform once:
        kinematicBox.body.setActivationState(Collision.ACTIVE_TAG);

        super.render();
    }

    @Override
    public void dispose() {
        kinematicBox = kinematicBox1 = kinematicBox2 = kinematicBox3 = null;
        position = null;
        super.dispose();
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        shoot(x, y);
        return true;
    }
}
