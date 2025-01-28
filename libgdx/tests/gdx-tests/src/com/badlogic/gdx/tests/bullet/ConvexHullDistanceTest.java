package com.badlogic.gdx.tests.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;
import com.badlogic.gdx.physics.bullet.collision.ContactResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btShapeHull;

/**
 * , didum
 */
public class ConvexHullDistanceTest extends BaseBulletTest {
    private ConvexHullDistance distance;
    private ShapeRenderer shapeRenderer;

    public static btConvexHullShape createConvexHullShape(final Model model, boolean optimize) {
        final Mesh mesh = model.meshes.get(0);
        final btConvexHullShape shape = new btConvexHullShape(mesh.getVerticesBuffer(false), mesh.getNumVertices(),
                mesh.getVertexSize());
        if (!optimize) return shape;
        // now optimize the shape
        final btShapeHull hull = new btShapeHull(shape);
        hull.buildHull(shape.getMargin());
        final btConvexHullShape result = new btConvexHullShape(hull);
        // delete the temporary shape
        shape.dispose();
        hull.dispose();
        return result;
    }

    @Override
    public void create() {
        super.create();

        final Model carModel = objLoader.loadModel(Gdx.files.internal("data/car.obj"));
        disposables.add(carModel);
        carModel.materials.get(0).clear();
        carModel.materials.get(0).set(ColorAttribute.createDiffuse(Color.WHITE), ColorAttribute.createSpecular(Color.WHITE));
        world.addConstructor("car", new BulletConstructor(carModel, 5f, createConvexHullShape(carModel, true)));

        // Create the entities
        world.add("ground", 0f, 0f, 0f).setColor(0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(),
                0.25f + 0.5f * (float) Math.random(), 1f);

        for (float y = 10f; y < 50f; y += 5f)
            world.add("car", -2f + (float) Math.random() * 4f, y, -2f + (float) Math.random() * 4f).setColor(
                    0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(), 1f);
        distance = new ConvexHullDistance();
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        shoot(x, y);
        return true;
    }

    @Override
    public void render() {
        super.render();

        // Draw the lines of the distances

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(1, 1, 0, 1);

        for (int i = 0; i < world.entities.size; i++) {
            btCollisionObject collisionObject0 = world.entities.get(i).body;

            for (int j = 0; j < world.entities.size; j++) {

                if (i != j) {
                    btCollisionObject collisionObject1 = world.entities.get(j).body;
                    distance.calculateDistance(collisionObject0, collisionObject1);
                    shapeRenderer.line(distance.getVector3()[0], distance.getVector3()[1]);
                }
            }
        }

        shapeRenderer.end();
    }

    private class ConvexHullDistance {
        Vector3[] vectors = new Vector3[]{new Vector3(), new Vector3()};
        private btDefaultCollisionConfiguration collisionConfiguration;
        private btCollisionDispatcher dispatcher;
        private btDbvtBroadphase pairCache;
        private btCollisionWorld collisionWorld;

        public ConvexHullDistance() {
            collisionConfiguration = new btDefaultCollisionConfiguration();
            dispatcher = new btCollisionDispatcher(collisionConfiguration);
            pairCache = new btDbvtBroadphase();
            collisionWorld = new btCollisionWorld(dispatcher, pairCache, collisionConfiguration);
        }

        public Vector3[] getVector3() {
            return vectors;
        }

        public void calculateDistance(btCollisionObject colObjA, btCollisionObject colObjB) {
            DistanceInternalResultCallback result = new DistanceInternalResultCallback();

            Collision.setGContactBreakingThreshold(100f);
            collisionWorld.contactPairTest(colObjA, colObjB, result);
            Collision.setGContactBreakingThreshold(0.02f);
        }

        private class DistanceInternalResultCallback extends ContactResultCallback {
            public DistanceInternalResultCallback() {

            }

            @Override
            public float addSingleResult(btManifoldPoint cp, btCollisionObjectWrapper colObj0Wrap, int partId0, int index0,
                                         btCollisionObjectWrapper colObj1Wrap, int partId1, int index1) {

                cp.getPositionWorldOnA(vectors[0]);
                cp.getPositionWorldOnB(vectors[1]);

                return 1.f;
            }
        }
    }
}
