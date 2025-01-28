package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.tests.utils.GdxTest;

public class TextInputDialogTest extends GdxTest {
    String message;
    SpriteBatch batch;
    BitmapFont font;
    int inputType = 0;

    public void create() {
        message = "Touch screen for dialog";
        batch = new SpriteBatch();
        font = new BitmapFont();
    }

    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        font.draw(batch, message, 10, 40);
        batch.end();

        if (Gdx.input.justTouched()) {
            Gdx.input.getTextInput(new TextInputListener() {
                @Override
                public void input(String text) {
                    message = "message: " + text + ", type: " + Input.OnscreenKeyboardType.values()[inputType]
                            + ", touch screen for new dialog";
                }

                @Override
                public void canceled() {
                    message = "cancled by user";
                }
            }, "enter something funny", "funny", "something funny", Input.OnscreenKeyboardType.values()[inputType]);
            inputType = (inputType + 1) % Input.OnscreenKeyboardType.values().length;
        }
    }
}
