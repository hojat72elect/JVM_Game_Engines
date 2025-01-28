package com.badlogic.gdx.backends.lwjgl;

import com.badlogic.gdx.Input;

public interface LwjglInput extends Input {

    /**
     * Records input events such as mouse and keyboard (usually called every frame by the {@link LwjglApplication})
     */
    void update();

    /**
     * Process all recorded input events (usually called every frame by the {@link LwjglApplication})
     */
    void processEvents();
}
