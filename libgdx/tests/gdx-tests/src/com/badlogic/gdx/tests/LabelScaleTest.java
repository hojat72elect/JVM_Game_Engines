package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.ScreenUtils;

public class LabelScaleTest extends GdxTest {
    Skin skin;
    Stage stage;
    SpriteBatch batch;
    Actor root;

    @Override
    public void create() {
        batch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        stage.addActor(table);
        table.setPosition(200, 65);

        Label label1 = new Label("This text is scaled 2x.", skin);
        label1.setFontScale(2);
        Label label2 = new Label(
                "This text is scaled. This text is scaled. This text is scaled. This text is scaled. This text is scaled. ", skin);
        label2.setWrap(true);
        label2.setFontScale(0.75f, 0.75f);

        table.debug();
        table.add(label1);
        table.row();
        table.add(label2).fill();
        table.pack();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
}
