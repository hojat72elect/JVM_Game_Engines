package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MultitouchTest extends GdxTest {
    ShapeRenderer renderer;
    ScreenViewport viewport;
    OrthographicCamera camera;
    long startTime = TimeUtils.nanoTime();

    Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.WHITE, Color.PINK, Color.ORANGE, Color.YELLOW, Color.MAGENTA,
            Color.CYAN, Color.LIGHT_GRAY, Color.GRAY, Color.DARK_GRAY};

    Vector2 tp = new Vector2();

    @Override
    public void render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeType.Filled);
        int size = Math.max(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) / 10;
        for (int i = 0; i < 10; i++) {
            if (!Gdx.input.isTouched(i)) continue;
            viewport.unproject(tp.set(Gdx.input.getX(i), Gdx.input.getY(i)));
            Color color = colors[i % colors.length];
            renderer.setColor(color);
            float sSize = size * Gdx.input.getPressure(i);
            renderer.triangle(tp.x, tp.y + sSize, tp.x + sSize, tp.y - sSize, tp.x - sSize, tp.y - sSize);
        }
        renderer.end();
    }

    @Override
    public void create() {
        Gdx.app.log("Multitouch", "multitouch supported: " + Gdx.input.isPeripheralAvailable(Peripheral.MultitouchScreen));
        renderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }
}
