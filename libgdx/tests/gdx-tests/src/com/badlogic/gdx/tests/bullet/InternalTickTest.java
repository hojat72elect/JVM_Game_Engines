package com.badlogic.gdx.tests.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectArray;
import com.badlogic.gdx.physics.bullet.dynamics.InternalTickCallback;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;


public class InternalTickTest extends BaseBulletTest {
    final int BOXCOUNT_X = 5;
    final int BOXCOUNT_Y = 5;
    final int BOXCOUNT_Z = 1;
    final float BOXOFFSET_X = -2.5f;
    final float BOXOFFSET_Y = 0.5f;
    final float BOXOFFSET_Z = 0f;
    TestInternalTickCallback internalTickCallback;
    float toggleTime = 0f;
    boolean toggleAttach = false;

    @Override
    public void create() {
        super.create();

        internalTickCallback = new TestInternalTickCallback((btDynamicsWorld) world.collisionWorld);

        // Create the entities
        world.add("ground", 0f, 0f, 0f).setColor(0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(),
                0.25f + 0.5f * (float) Math.random(), 1f);

        for (int x = 0; x < BOXCOUNT_X; x++) {
            for (int y = 0; y < BOXCOUNT_Y; y++) {
                for (int z = 0; z < BOXCOUNT_Z; z++) {
                    world.add("box", BOXOFFSET_X + x, BOXOFFSET_Y + y, BOXOFFSET_Z + z).setColor(0.5f + 0.5f * (float) Math.random(),
                            0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(), 1f);
                }
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        if (internalTickCallback != null) internalTickCallback.dispose();
        internalTickCallback = null;
    }

    @Override
    public void render() {
        super.render();
        if (internalTickCallback == null) return;
        if ((toggleTime += Gdx.graphics.getDeltaTime()) > 1.0f) {
            toggleTime -= 1.0f;
            if (toggleAttach)
                internalTickCallback.detach();
            else
                internalTickCallback.attach();
            toggleAttach = !toggleAttach;
        }
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        shoot(x, y);
        return true;
    }

    static class TestInternalTickCallback extends InternalTickCallback {
        public TestInternalTickCallback(btDynamicsWorld dynamicsWorld) {
            super(dynamicsWorld, true);
        }

        @Override
        public void onInternalTick(btDynamicsWorld dynamicsWorld, float timeStep) {
            btCollisionObjectArray objs = dynamicsWorld.getCollisionObjectArray();
            dynamicsWorld.clearForces();
            int idx = 0;
            for (int i = 0; i < objs.size(); i++) {
                btRigidBody body = (btRigidBody) (objs.atConst(i));
                if (body == null || body.isStaticOrKinematicObject()) continue;
                body.applyGravity();
                body.applyCentralForce(tmpV1.set(0f, 8.0f + (float) (6.0 * Math.random()), 0f));
                idx++;
            }
        }
    }
}
