package com.badlogic.gdx.tests.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

public class Chain extends Box2DTest {

    @Override
    protected void createWorld(World world) {
        Body ground;

        {
            BodyDef bd = new BodyDef();
            ground = world.createBody(bd);

            EdgeShape shape = new EdgeShape();
            shape.set(new Vector2(-40, 0), new Vector2(40, 0));

            ground.createFixture(shape, 0.0f);
            shape.dispose();
        }

        {
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(0.6f, 0.125f);

            FixtureDef fd = new FixtureDef();
            fd.shape = shape;
            fd.density = 20.0f;
            fd.friction = 0.2f;

            RevoluteJointDef jd = new RevoluteJointDef();
            jd.collideConnected = false;

            float y = 25.0f;
            Body prevBody = ground;

            for (int i = 0; i < 30; i++) {
                BodyDef bd = new BodyDef();
                bd.type = BodyType.DynamicBody;
                bd.position.set(0.5f + i, y);
                Body body = world.createBody(bd);
                body.createFixture(fd);

                Vector2 anchor = new Vector2(i, y);
                jd.initialize(prevBody, body, anchor);
                world.createJoint(jd);
                prevBody = body;
            }

            shape.dispose();
        }
    }
}
