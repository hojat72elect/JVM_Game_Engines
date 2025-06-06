package com.badlogic.gdx.tests.bullet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.bullet.dynamics.FilterableVehicleRaycaster;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btVehicleRaycaster;

public class VehicleFilterTest extends VehicleTest {

    static final short FILTER_GROUP = (short) (1 << 11);
    static final short FILTER_MASK = FILTER_GROUP;

    @Override
    protected btVehicleRaycaster getRaycaster() {
        FilterableVehicleRaycaster raycaster = new FilterableVehicleRaycaster((btDynamicsWorld) world.collisionWorld);
        raycaster.setCollisionFilterGroup(FILTER_GROUP);
        raycaster.setCollisionFilterMask(FILTER_MASK);
        return raycaster;
    }

    @Override
    public void create() {
        super.create();
        chassis.setColor(Color.BLUE);
    }

    @Override
    public BulletWorld createWorld() {
        // Force all objects to same collision group and filter
        return new BulletWorld() {
            @Override
            public void add(final BulletEntity entity) {
                world.entities.add(entity);
                if (entity.body != null) {
                    if (entity.body instanceof btRigidBody)
                        ((btDiscreteDynamicsWorld) collisionWorld).addRigidBody((btRigidBody) entity.body, FILTER_GROUP, FILTER_MASK);
                    else
                        collisionWorld.addCollisionObject(entity.body, FILTER_GROUP, FILTER_MASK);
                    // Store the index of the entity in the collision object.
                    entity.body.setUserValue(entities.size - 1);
                }
            }
        };
    }
}
