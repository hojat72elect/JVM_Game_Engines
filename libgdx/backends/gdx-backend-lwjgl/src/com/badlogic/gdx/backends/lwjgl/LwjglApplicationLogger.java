package com.badlogic.gdx.backends.lwjgl;

import com.badlogic.gdx.ApplicationLogger;

import org.jetbrains.annotations.NotNull;

/**
 * Default implementation of {@link ApplicationLogger} for Lwjgl
 */
public class LwjglApplicationLogger implements ApplicationLogger {

    @Override
    public void log(@NotNull String tag, @NotNull String message) {
        System.out.println("[" + tag + "] " + message);
    }

    @Override
    public void log(@NotNull String tag, @NotNull String message, Throwable exception) {
        System.out.println("[" + tag + "] " + message);
        exception.printStackTrace(System.out);
    }

    @Override
    public void error(@NotNull String tag, @NotNull String message) {
        System.err.println("[" + tag + "] " + message);
    }

    @Override
    public void error(@NotNull String tag, @NotNull String message, Throwable exception) {
        System.err.println("[" + tag + "] " + message);
        exception.printStackTrace(System.err);
    }

    @Override
    public void debug(@NotNull String tag, @NotNull String message) {
        System.out.println("[" + tag + "] " + message);
    }

    @Override
    public void debug(@NotNull String tag, @NotNull String message, Throwable exception) {
        System.out.println("[" + tag + "] " + message);
        exception.printStackTrace(System.out);
    }
}
