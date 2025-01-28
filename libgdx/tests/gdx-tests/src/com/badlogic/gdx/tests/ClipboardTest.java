package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.tests.utils.GdxTest;

public class ClipboardTest extends GdxTest {

    Stage stage;
    TextArea textArea;
    TextButton buttonCopy;
    TextButton buttonPaste;
    TextButton buttonHasContents;

    @Override
    public void create() {
        stage = new Stage();
        Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        textArea = new TextArea("", skin);
        buttonCopy = new TextButton("Copy", skin);
        buttonPaste = new TextButton("Paste", skin);
        buttonHasContents = new TextButton("Has Contents: " + Gdx.app.getClipboard().hasContents(), skin);

        textArea.setSize(Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight() / 3f);

        textArea.setPosition(Gdx.graphics.getWidth() / 2f - textArea.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f - textArea.getHeight() / 2f);
        buttonCopy.setPosition(Gdx.graphics.getWidth() / 4f - buttonCopy.getWidth() / 2f,
                Gdx.graphics.getHeight() / 4f - buttonCopy.getHeight() / 2f);
        buttonPaste.setPosition(3 * Gdx.graphics.getWidth() / 4f - buttonPaste.getWidth() / 2f,
                Gdx.graphics.getHeight() / 4f - buttonPaste.getHeight() / 2f);
        buttonHasContents.setPosition(Gdx.graphics.getWidth() / 2f - buttonHasContents.getWidth() / 2f,
                Gdx.graphics.getHeight() / 4f - buttonHasContents.getHeight() / 2f);

        buttonCopy.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.getClipboard().setContents(textArea.getText());
            }
        });
        buttonPaste.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                textArea.setText(Gdx.app.getClipboard().getContents());
            }
        });
        buttonHasContents.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buttonHasContents.setText("Has Contents: " + Gdx.app.getClipboard().hasContents());
            }
        });

        stage.addActor(textArea);
        stage.addActor(buttonCopy);
        stage.addActor(buttonPaste);
        stage.addActor(buttonHasContents);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render() {
        stage.act();
        stage.draw();
    }
}
