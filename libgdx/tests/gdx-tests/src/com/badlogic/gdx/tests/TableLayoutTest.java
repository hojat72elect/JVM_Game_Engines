package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.tests.utils.GdxTest;

public class TableLayoutTest extends GdxTest {
    Stage stage;

    public void create() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        Label nameLabel = new Label("Name:", skin);
        TextField nameText = new TextField("", skin);
        Label addressLabel = new Label("Address:", skin);
        TextField addressText = new TextField("", skin);

        Table table = new Table();
        stage.addActor(table);
        table.setSize(260, 195);
        table.setPosition(190, 142);
        // table.align(Align.right | Align.bottom);

        table.debug();

        TextureRegion upRegion = skin.getRegion("default-slider-knob");
        TextureRegion downRegion = skin.getRegion("default-slider-knob");
        BitmapFont buttonFont = skin.getFont("default-font");

        TextButton button = new TextButton("Button 1", skin);
        button.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("touchDown 1");
                return false;
            }
        });
        table.add(button);
        // table.setTouchable(Touchable.disabled);

        Table table2 = new Table();
        stage.addActor(table2);
        table2.setFillParent(true);
        table2.bottom();

        TextButton button2 = new TextButton("Button 2", skin);
        button2.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("2!");
            }
        });
        button2.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("touchDown 2");
                return false;
            }
        });
        table2.add(button2);
    }

    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
    }
}
