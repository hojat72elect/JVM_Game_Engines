package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SystemCursorTest extends GdxTest {
    private Stage stage;
    private Skin skin;

    @Override
    public void create() {
        super.create();
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        for (final Cursor.SystemCursor cursor : Cursor.SystemCursor.values()) {
            TextButton button = new TextButton(cursor.name(), skin);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.graphics.setSystemCursor(cursor);
                }
            });
            table.add(button).row();
        }
    }

    @Override
    public void render() {
        super.render();
        ScreenUtils.clear(Color.BLACK);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
        skin.dispose();
    }
}
