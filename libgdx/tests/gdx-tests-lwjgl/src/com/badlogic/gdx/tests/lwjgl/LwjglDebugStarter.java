package com.badlogic.gdx.tests.lwjgl;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tests.ScrollPaneScrollBarsTest;
import com.badlogic.gdx.tests.utils.GdxTest;

public class LwjglDebugStarter {
    public static void main(String[] argv) {

        GdxTest test = new ScrollPaneScrollBarsTest();
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.r = config.g = config.b = config.a = 8;

        config.width = 960;
        config.height = 600;

        new LwjglApplication(test, config);
    }
}
