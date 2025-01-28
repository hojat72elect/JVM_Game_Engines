package com.badlogic.gdx.tests;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.tests.utils.GdxTest;

public class GroupFadeTest extends GdxTest {

    Texture texture;
    Stage stage;

    @Override
    public void create() {
        texture = new Texture(Gdx.files.internal("data/badlogicsmall.jpg"));
        stage = new Stage();

        for (int i = 0; i < 100; i++) {
            Image img = new Image(new TextureRegion(texture));
            img.setX((float) Math.random() * 480);
            img.setY((float) Math.random() * 320);
            img.getColor().a = (float) Math.random() * 0.5f + 0.5f;
            stage.addActor(img);
        }

        stage.getRoot().addAction(forever(sequence(fadeOut(3), fadeIn(3))));
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        texture.dispose();
        stage.dispose();
    }
}
