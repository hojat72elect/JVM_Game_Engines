package com.badlogic.gdx.tests.g3d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.tests.utils.GdxTest;

public class AnisotropyTest extends GdxTest {
    Texture brick;
    SpriteBatch batch;
    PerspectiveCamera cam = new PerspectiveCamera();
    int targetAnisotropicLevel = 1;
    Matrix4 tmp = new Matrix4();

    @Override
    public void create() {
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_LESS);

        brick = new Texture(Gdx.files.internal("data/g3d/materials/brick_diffuse_01.png"), true);
        brick.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        brick.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        batch = new SpriteBatch();

        Gdx.gl.glClearColor(0, 0.2f, 0.2f, 1);
    }

    @Override
    public void dispose() {
        batch.dispose();
        brick.dispose();
    }

    private void changeAnisotropy() {
        targetAnisotropicLevel *= 2;
        if (targetAnisotropicLevel > 16) targetAnisotropicLevel = 1;
        float value = brick.setAnisotropicFilter(targetAnisotropicLevel);
        Gdx.app.log("anisotropic value", Float.toString(value));
    }

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) changeAnisotropy();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        for (int i = 0; i < 5; i++) {
            tmp.idt().translate(0, -2f + i, 0).rotate(Vector3.X, 28 * i);
            batch.setTransformMatrix(tmp);
            batch.draw(brick, -0.5f, -0.5f, 1, 1);
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.near = 0.1f;
        cam.far = 10;
        cam.position.set(0, 0, 4);
        cam.update();
    }
}
