package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.tests.utils.GdxTest;

public class ImmediateModeRendererTest extends GdxTest {
    Matrix4 projMatrix = new Matrix4();
    ImmediateModeRenderer renderer;
    Texture texture;

    @Override
    public void dispose() {
        texture.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);
        texture.bind();
        renderer.begin(projMatrix, GL20.GL_TRIANGLES);
        renderer.texCoord(0, 0);
        renderer.color(1, 0, 0, 1);
        renderer.vertex(-0.5f, -0.5f, 0);
        renderer.texCoord(1, 0);
        renderer.color(0, 1, 0, 1);
        renderer.vertex(0.5f, -0.5f, 0);
        renderer.texCoord(0.5f, 1);
        renderer.color(0, 0, 1, 1);
        renderer.vertex(0f, 0.5f, 0);
        renderer.end();
    }

    @Override
    public void create() {
        renderer = new ImmediateModeRenderer20(false, true, 1);
        texture = new Texture(Gdx.files.internal("data/badlogic.jpg"));
    }
}
