package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.OnscreenKeyboardType;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.tests.utils.GdxTest;

public class OnscreenKeyboardTest extends GdxTest implements InputProcessor {

    BitmapFont font;
    String text;
    SpriteBatch batch;
    OnscreenKeyboardType type = OnscreenKeyboardType.Default;

    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        text = "";
        Gdx.input.setInputProcessor(this);
    }

    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        font.draw(batch, "input [" + type + "]: " + text, 0, Gdx.graphics.getHeight());
        batch.end();

        if (Gdx.input.justTouched()) {
            type = OnscreenKeyboardType.values()[(type.ordinal() + 1) % OnscreenKeyboardType.values().length];
            Gdx.input.setOnscreenKeyboardVisible(true, type);
        }
    }

    @Override
    public boolean keyDown(int keycode) {

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if (character == '\b' && text.length() >= 1) {
            text = text.substring(0, text.length() - 1);
        } else if (character == '\n') {
            Gdx.input.setOnscreenKeyboardVisible(false);
        } else {
            text += character;
        }
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int x, int y) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // TODO Auto-generated method stub
        return false;
    }
}
