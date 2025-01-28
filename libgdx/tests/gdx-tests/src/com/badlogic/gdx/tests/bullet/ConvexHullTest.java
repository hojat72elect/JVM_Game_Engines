package com.badlogic.gdx.tests.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.collision.btShapeHull;


public class ConvexHullTest extends BaseBulletTest {

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
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        shoot(x, y);
        return true;
    }
}
