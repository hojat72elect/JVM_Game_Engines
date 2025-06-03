package com.badlogic.gdx.backends.android;

import com.badlogic.gdx.InputProcessor;

public interface InputProcessorLW extends InputProcessor {

    void touchDrop(int x, int y);
}
