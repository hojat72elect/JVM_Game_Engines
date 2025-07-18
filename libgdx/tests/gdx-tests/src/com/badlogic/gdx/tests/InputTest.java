package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.tests.utils.GdxTest;

public class InputTest extends GdxTest implements InputProcessor {

    @Override
    public void create() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render() {
        if (Gdx.input.justTouched()) {
            Gdx.app.log("Input Test", "just touched, button: " + (Gdx.input.isButtonPressed(Buttons.LEFT) ? "left " : "")
                    + (Gdx.input.isButtonPressed(Buttons.MIDDLE) ? "middle " : "")
                    + (Gdx.input.isButtonPressed(Buttons.RIGHT) ? "right" : "") + (Gdx.input.isButtonPressed(Buttons.BACK) ? "back" : "")
                    + (Gdx.input.isButtonPressed(Buttons.FORWARD) ? "forward" : ""));
        }

        for (int i = 0; i < 10; i++) {
            if (Gdx.input.getDeltaX(i) != 0 || Gdx.input.getDeltaY(i) != 0) {
                Gdx.app.log("Input Test", "delta[" + i + "]: " + Gdx.input.getDeltaX(i) + ", " + Gdx.input.getDeltaY(i));
            }
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        Gdx.app.log("Input Test", "key down: " + keycode);
        if (keycode == Keys.G) Gdx.input.setCursorCatched(!Gdx.input.isCursorCatched());
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        Gdx.app.log("Input Test", "key typed: '" + character + "'");
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        Gdx.app.log("Input Test", "key up: " + keycode);
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        Gdx.app.log("Input Test", "touch down: " + x + ", " + y + ", button: " + getButtonString(button));
        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        Gdx.app.log("Input Test", "touch dragged: " + x + ", " + y + ", pointer: " + pointer);
        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        Gdx.app.log("Input Test", "touch up: " + x + ", " + y + ", button: " + getButtonString(button));
        return false;
    }

    @Override
    public boolean mouseMoved(int x, int y) {
        Gdx.app.log("Input Test", "touch moved: " + x + ", " + y);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        Gdx.app.log("Input Test", "scrolled: " + amountY);
        return false;
    }

    private String getButtonString(int button) {
        if (button == Buttons.LEFT) return "left";
        if (button == Buttons.RIGHT) return "right";
        if (button == Buttons.MIDDLE) return "middle";
        if (button == Buttons.BACK) return "back";
        if (button == Buttons.FORWARD) return "forward";
        return "unknown";
    }
}
