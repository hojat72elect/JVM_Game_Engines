package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.tests.utils.GdxTest;

/**
 * Test that unchecked exceptions thrown from a runnable get posted and terminate the app.
 */
public class RunnablePostTest extends GdxTest {

    private static final String TAG = "RunnablePostTest";
    static boolean expectIt = false;

    static private final Thread.UncaughtExceptionHandler exHandler = (t, e) -> {
        if (expectIt) {
            Gdx.app.log(TAG, "PASSED: " + e.getMessage());
        } else {
            Gdx.app.log(TAG, "FAILED!  Unexpected exception received.");
            e.printStackTrace(System.err);
        }
    };

    public void create() {
        Thread.setDefaultUncaughtExceptionHandler(exHandler);
    }

    @Override
    public void render() {
        if (Gdx.input.justTouched()) {
            expectIt = true;
            Gdx.app.postRunnable(() -> {
                throw new RuntimeException("This is a test of the uncaught exception handler.");
            });
        }
    }
}
