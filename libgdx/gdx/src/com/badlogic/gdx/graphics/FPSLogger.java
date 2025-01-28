package com.badlogic.gdx.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * A simple helper class to log the frames per seconds achieved. Just invoke the {@link #log()} method in your rendering method.
 * The output will be logged once per second.
 */
public class FPSLogger {
    long startTime;
    int bound;

    public FPSLogger() {
        this(Integer.MAX_VALUE);
    }

    /**
     * @param bound only logs when they frames per second are less than the bound
     */
    public FPSLogger(int bound) {
        this.bound = bound;
        startTime = TimeUtils.nanoTime();
    }

    /**
     * Logs the current frames per second to the console.
     */
    public void log() {
        final long nanoTime = TimeUtils.nanoTime();
        if (nanoTime - startTime > 1000000000) /* 1,000,000,000ns == one second */ {
            final int fps = Gdx.graphics.getFramesPerSecond();
            if (fps < bound) {
                Gdx.app.log("FPSLogger", "fps: " + fps);
                startTime = nanoTime;
            }
        }
    }
}
