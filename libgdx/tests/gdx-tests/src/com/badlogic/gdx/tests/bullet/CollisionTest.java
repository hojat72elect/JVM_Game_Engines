package com.badlogic.gdx.tests.bullet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.bullet.collision.ContactResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;


public class CollisionTest extends ShootTest {
    BulletEntity projectile;
    Array<BulletEntity> hits = new Array<BulletEntity>();
    Array<BulletEntity> contacts = new Array<BulletEntity>();
    Array<Color> colors = new Array<Color>();
    TestContactResultCallback contactCB;
    private final Pool<Color> colorPool = new Pool<Color>() {
        @Override
        protected Color newObject() {
            return new Color();
        }
    };

    public void updateContactInfo() {
        int n = world.dispatcher.getNumManifolds();
        for (int i = 0; i < n; i++) {
            btPersistentManifold manifold = world.dispatcher.getManifoldByIndexInternal(i);
            btCollisionObject objA = manifold.getBody0();
            btCollisionObject objB = manifold.getBody1();
            if (objA != ground.body && objB != ground.body) {
                if (objA.userData != null && objA.userData instanceof BulletEntity) {
                    BulletEntity ent = (BulletEntity) objA.userData;
                    if (ent != projectile && !contacts.contains(ent, true) && !hits.contains(ent, true))
                        contacts.add(ent);
                }
                if (objB.userData != null && objB.userData instanceof BulletEntity) {
                    BulletEntity ent = (BulletEntity) objB.userData;
                    if (ent != projectile && !contacts.contains(ent, true) && !hits.contains(ent, true))
                        contacts.add(ent);
                }
            }
        }
    }

    @Override
    public void create() {
        super.create();

        contactCB = new TestContactResultCallback();
    }

    @Override
    public void render() {
        process();
    }

    public void process() {
        Color color = null;
        update();
        hits.clear();
        contacts.clear();

        // Note that this might miss collisions, use InternalTickCallback to check for collision on every tick.
        // See InternalTickTest on how to implement it.

        // Check what the projectile hits
        if (projectile != null) {
            color = projectile.getColor();
            projectile.setColor(Color.RED);
            world.collisionWorld.contactTest(projectile.body, contactCB);
        }
        // Check for other collisions
        updateContactInfo();

        if (hits.size > 0) {
            for (int i = 0; i < hits.size; i++) {
                colors.add(colorPool.obtain().set(hits.get(i).getColor()));
                hits.get(i).setColor(Color.RED);
            }
        }
        if (contacts.size > 0) {
            for (int i = 0; i < contacts.size; i++) {
                colors.add(colorPool.obtain().set(contacts.get(i).getColor()));
                contacts.get(i).setColor(Color.BLUE);
            }
        }
        render(false);
        if (projectile != null) projectile.setColor(color);
        for (int i = 0; i < hits.size; i++)
            hits.get(i).setColor(colors.get(i));
        for (int i = 0; i < contacts.size; i++)
            contacts.get(i).setColor(colors.get(hits.size + i));
        colorPool.freeAll(colors);
        colors.clear();
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        projectile = shoot(x, y);
        return true;
    }

    @Override
    public void dispose() {
        super.dispose();
        projectile = null;
    }

    public class TestContactResultCallback extends ContactResultCallback {
        @Override
        public float addSingleResult(btManifoldPoint cp, btCollisionObjectWrapper colObj0Wrap, int partId0, int index0,
                                     btCollisionObjectWrapper colObj1Wrap, int partId1, int index1) {
            btCollisionObject other = colObj0Wrap.getCollisionObject() == projectile.body ? colObj1Wrap.getCollisionObject()
                    : colObj0Wrap.getCollisionObject();
            if (other != null && other.userData != null && other.userData instanceof BulletEntity) {
                BulletEntity ent = (BulletEntity) other.userData;
                if (ent != ground && !hits.contains(ent, true)) hits.add((BulletEntity) other.userData);
            }
            return 0f;
        }
    }
}
