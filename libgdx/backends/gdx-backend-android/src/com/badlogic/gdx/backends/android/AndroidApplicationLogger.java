package com.badlogic.gdx.backends.android;

import android.util.Log;

import androidx.annotation.NonNull;

import com.badlogic.gdx.ApplicationLogger;

/**
 * Default implementation of {@link ApplicationLogger} for android
 */
public class AndroidApplicationLogger implements ApplicationLogger {

    @Override
    public void log(@NonNull String tag, @NonNull String message) {
        Log.i(tag, message);
    }

    @Override
    public void log(@NonNull String tag, @NonNull String message, @NonNull Throwable exception) {
        Log.i(tag, message, exception);
    }

    @Override
    public void error(@NonNull String tag, @NonNull String message) {
        Log.e(tag, message);
    }

    @Override
    public void error(@NonNull String tag, @NonNull String message, @NonNull Throwable exception) {
        Log.e(tag, message, exception);
    }

    @Override
    public void debug(@NonNull String tag, @NonNull String message) {
        Log.d(tag, message);
    }

    @Override
    public void debug(@NonNull String tag, @NonNull String message, @NonNull Throwable exception) {
        Log.d(tag, message, exception);
    }
}
