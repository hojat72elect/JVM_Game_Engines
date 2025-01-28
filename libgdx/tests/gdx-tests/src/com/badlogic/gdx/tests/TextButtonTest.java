package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.ScreenUtils;

public class TextButtonTest extends GdxTest {
    private Stage stage;
    private Skin skin;

    @Override
    public void create() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        for (int i = 0; i < 1; i++) {
            TextButton t = new TextButton("Button" + i, skin);
            t.setX(MathUtils.random(0, Gdx.graphics.getWidth()));
            t.setY(MathUtils.random(0, Gdx.graphics.getHeight()));
            t.setWidth(MathUtils.random(50, 200));
            t.setHeight(MathUtils.random(0, 100));
            stage.addActor(t);
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);
        stage.draw();
        Gdx.app.log("X", "FPS: " + Gdx.graphics.getFramesPerSecond());
        SpriteBatch spriteBatch = (SpriteBatch) stage.getBatch();
        Gdx.app.log("X", "render calls: " + spriteBatch.totalRenderCalls);
        spriteBatch.totalRenderCalls = 0;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
