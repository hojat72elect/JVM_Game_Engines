package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ScreenUtils;

public class TextAreaTest2 extends GdxTest {
    private Stage stage;
    private Skin skin;

    @Override
    public void create() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        // Create a string that perfectly fills the float array used in the textarea float array
        FloatArray dummyArray = new FloatArray();
        String limit = "";
        // Minus one, because TextField adds a magic char
        for (int i = 0; i < dummyArray.items.length - 1; i++) {
            limit += "a";
        }

        TextArea textArea = new TextArea(limit, skin);
        textArea.setX(10);
        textArea.setY(10);
        textArea.setWidth(200);
        textArea.setHeight(200);

        stage.addActor(textArea);
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
