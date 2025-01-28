package com.badlogic.gdx.backends.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;

/**
 * Interface that abstracts the Android application class usages, so that libGDX can be used with a fragment (or with any other
 * client code)
 */
public interface AndroidApplicationBase extends Application {

    static final int MINIMUM_SDK = 19;

    /**
     * The application or activity context
     *
     * @return the {@link Context}
     */
    Context getContext();

    /**
     * A set of usable runnables
     *
     * @return the {@link Runnable} array
     */
    Array<Runnable> getRunnables();

    /**
     * The currently executed runnables
     *
     * @return the {@link Runnable} array
     */
    Array<Runnable> getExecutedRunnables();

    /**
     * Method signifies an intent of the caller to execute some action on the UI Thread.
     *
     * @param runnable The runnable to be executed
     */
    void runOnUiThread(Runnable runnable);

    /**
     * Method signifies an intent to start an activity, may be the default method of the {@link Activity} class
     *
     * @param intent The {@link Intent} for starting an activity
     */
    void startActivity(Intent intent);

    /**
     * Returns the {@link AndroidInput} object associated with this {@link AndroidApplicationBase}
     *
     * @return the {@link AndroidInput} object
     */
    @Override
    AndroidInput getInput();

    /**
     * Returns the {@link LifecycleListener} array associated with this {@link AndroidApplicationBase}
     *
     * @return the array of {@link LifecycleListener}'s
     */
    SnapshotArray<LifecycleListener> getLifecycleListeners();

    /**
     * Returns the Window associated with the application
     *
     * @return The {@link Window} associated with the application
     */
    Window getApplicationWindow();

    /**
     * Returns the WindowManager associated with the application
     *
     * @return The {@link WindowManager} associated with the application
     */
    WindowManager getWindowManager();

    /**
     * Activates Android 4.4 KitKat's 'Immersive Mode' feature.
     *
     * @param b Whether or not to use immersive mode
     */
    void useImmersiveMode(boolean b);

    /**
     * Returns the Handler object created by the application
     *
     * @return The {@link Handler} object created by the application
     */
    Handler getHandler();

    /**
     * Returns the AndroidAudio to be used by the application
     *
     * @return the created {@link AndroidAudio}
     */
    AndroidAudio createAudio(Context context, AndroidApplicationConfiguration config);

    /**
     * Returns the AndroidInput to be used by the application
     *
     * @return the created {@link AndroidInput}
     */
    AndroidInput createInput(Application activity, Context context, Object view, AndroidApplicationConfiguration config);
}
