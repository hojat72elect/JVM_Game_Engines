package com.badlogic.gdx.backends.lwjgl3;

import com.badlogic.gdx.utils.GdxNativesLoader;

public final class Lwjgl3NativesLoader {

    static {
        System.setProperty("org.lwjgl.input.Mouse.allowNegativeMouseCoords", "true");
    }

    static public void load() {
        GdxNativesLoader.load();
    }
}
