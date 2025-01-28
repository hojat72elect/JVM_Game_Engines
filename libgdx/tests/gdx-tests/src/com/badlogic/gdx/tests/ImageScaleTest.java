package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;

public class ImageScaleTest extends GdxTest {
    Stage stage;
    Texture texture;

    public void create() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        texture = new Texture("data/group-debug.png");
        Image image = new Image(texture);
        image.setScaling(Scaling.fit);
        image.setBounds(100, 100, 400, 200);
        stage.addActor(image);

        Image image2 = new Image(texture);
        image2.setScaling(Scaling.fit);
        image.setBounds(100, 100, 400, 200);
        image2.setOrigin(200, 100);
        image2.setScale(0.5f);
        stage.addActor(image2);

    }

    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        texture.dispose();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
}
