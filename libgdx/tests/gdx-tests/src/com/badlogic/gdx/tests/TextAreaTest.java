package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.ScreenUtils;

public class TextAreaTest extends GdxTest {
    private Stage stage;
    private Skin skin;

    @Override
    public void create() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        skin.getFont("default-font").setFixedWidthGlyphs("0123456789");
        TextArea textArea = new TextArea("Text Area\n1111111111\n0123456789\nEssentially, a text field\nwith\nmultiple\nlines.\n"
                + "It can even handle very loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong lines.",
                skin);
        textArea.setX(10);
        textArea.setY(10);
        textArea.setWidth(200);
        textArea.setHeight(200);

        TextField textField = new TextField("Text field", skin);
        textField.setX(10);
        textField.setY(220);
        textField.setWidth(200);
        textField.setHeight(30);
        stage.addActor(textArea);
        stage.addActor(textField);

        Gdx.input.setCatchKey(Input.Keys.TAB, true);
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
