package com.badlogic.gdx.backends.android;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.android.surfaceview.FillResolutionStrategy;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.SnapshotArray;

/**
 * An implementation of the {@link Application} interface to be used with an AndroidLiveWallpaperService. Not directly
 * constructable, instead the {@link AndroidLiveWallpaperService} will create this class internally.
 */
public class AndroidLiveWallpaper implements AndroidApplicationBase {

    protected final Array<Runnable> runnables = new Array<>();
    protected final Array<Runnable> executedRunnables = new Array<>();
    protected final SnapshotArray<LifecycleListener> lifecycleListeners = new SnapshotArray<>(
            LifecycleListener.class);
    protected AndroidLiveWallpaperService service;
    protected AndroidGraphicsLiveWallpaper graphics;
    protected AndroidInput input;
    protected AndroidAudio audio;
    protected AndroidFiles files;
    protected AndroidNet net;
    protected AndroidClipboard clipboard;
    protected ApplicationListener listener;
    protected boolean firstResume = true;
    protected int logLevel = LOG_INFO;
    protected ApplicationLogger applicationLogger;
    protected volatile Color[] wallpaperColors = null;

    public AndroidLiveWallpaper(AndroidLiveWallpaperService service) {
        this.service = service;
    }

    public void initialize(ApplicationListener listener, AndroidApplicationConfiguration config) {
        if (this.getVersion() < MINIMUM_SDK) {
            throw new GdxRuntimeException("libGDX requires Android API Level " + MINIMUM_SDK + " or later.");
        }
        GdxNativesLoader.load();
        setApplicationLogger(new AndroidApplicationLogger());
        graphics = new AndroidGraphicsLiveWallpaper(this, config,
                config.resolutionStrategy == null ? new FillResolutionStrategy() : config.resolutionStrategy
        );

        // factory in use, but note: AndroidInputFactory causes exceptions when obfuscated: java.lang.RuntimeException: Couldn't
        // construct AndroidInput, this should never happen, proguard deletes constructor used only by reflection
        input = createInput(this, this.getService(), graphics.view, config);
        // input = new AndroidInput(this, this.getService(), null, config);

        audio = createAudio(this.getService(), config);
        files = createFiles();
        net = new AndroidNet(this, config);
        this.listener = listener;
        clipboard = new AndroidClipboard(this.getService());

        // Unlike activity, fragment and daydream applications there's no need for a specialized audio listener.
        // See description in onPause method.

        Gdx.app = this;
        Gdx.input = input;
        Gdx.audio = audio;
        Gdx.files = files;
        Gdx.graphics = graphics;
        Gdx.net = net;
    }

    public void onPause() {
        if (AndroidLiveWallpaperService.DEBUG)
            Log.d(AndroidLiveWallpaperService.TAG, " > AndroidLiveWallpaper - onPause()");

        // IMPORTANT!
        // jw: graphics.pause is never called, graphics.pause works on most devices but not on all..
        // for example on Samsung Galaxy Tab (GT-P6800) on android 4.0.4 invoking graphics.pause causes "Fatal Signal 11"
        // near mEglHelper.swap() in GLSurfaceView while processing next onPause event.
        // See related issue:
        // http://code.google.com/p/libgdx/issues/detail?id=541
        // the problem with graphics.pause occurs while using OpenGL 2.0 and original GLSurfaceView while rotating device
        // in lwp preview
        // in my opinion it is a bug of android not libgdx, even example Cubic live wallpaper from
        // Android SDK crashes on affected devices.......... and on some configurations of android emulator too.
        //
        // My wallpaper was rejected on Samsung Apps because of this issue, so I decided to disable graphics.pause..
        // also I moved audio lifecycle methods from AndroidGraphicsLiveWallpaper into this class

        // graphics.pause();
        // if (AndroidLiveWallpaperService.DEBUG)
        // Log.d(AndroidLiveWallpaperService.TAG, " > AndroidLiveWallpaper - onPause() application paused!");
        audio.pause();

        input.onPause();

        if (graphics != null) {
            graphics.onPauseGLSurfaceView();
        }

        if (AndroidLiveWallpaperService.DEBUG)
            Log.d(AndroidLiveWallpaperService.TAG, " > AndroidLiveWallpaper - onPause() done!");
    }

    public void onResume() {
        Gdx.app = this;
        Gdx.input = input;
        Gdx.audio = audio;
        Gdx.files = files;
        Gdx.graphics = graphics;
        Gdx.net = net;

        input.onResume();

        if (graphics != null) {
            graphics.onResumeGLSurfaceView();
        }

        if (!firstResume) {
            audio.resume();
            graphics.resume();
        } else
            firstResume = false;
    }

    public void onDestroy() {

        // it is too late to call graphics.destroy - it needs live gl GLThread and gl context, otherwise it will cause of deadlock
        // if (graphics != null) {
        // graphics.clearManagedCaches();
        // graphics.destroy();
        // }

        // so we do what we can..
        if (graphics != null) {
            // not necessary - already called in AndroidLiveWallpaperService.onDeepPauseApplication
            // app.graphics.clearManagedCaches();

            // kill the GLThread managed by GLSurfaceView
            graphics.onDestroyGLSurfaceView();
        }

        if (audio != null) {
            // dispose audio and free native resources, mandatory since graphics.pause is never called in live wallpaper
            audio.dispose();
        }
    }

    @Override
    public WindowManager getWindowManager() {
        return service.getWindowManager();
    }

    public AndroidLiveWallpaperService getService() {
        return service;
    }

    @Override
    public ApplicationListener getApplicationListener() {
        return listener;
    }

    @Override
    public void postRunnable(Runnable runnable) {
        synchronized (runnables) {
            runnables.add(runnable);
        }
    }

    @Override
    public Audio getAudio() {
        return audio;
    }

    @Override
    public Files getFiles() {
        return files;
    }

    @Override
    public Graphics getGraphics() {
        return graphics;
    }

    @Override
    public AndroidInput getInput() {
        return input;
    }

    @Override
    public Net getNet() {
        return net;
    }

    @Override
    public ApplicationType getType() {
        return ApplicationType.Android;
    }

    @Override
    public int getVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    @Override
    public long getJavaHeap() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    @Override
    public long getNativeHeap() {
        return Debug.getNativeHeapAllocatedSize();
    }

    @Override
    public Preferences getPreferences(String name) {
        return new AndroidPreferences(service.getSharedPreferences(name, Context.MODE_PRIVATE));
    }

    @Override
    public Clipboard getClipboard() {
        return clipboard;
    }

    @Override
    public void debug(String tag, String message) {
        if (logLevel >= LOG_DEBUG) getApplicationLogger().debug(tag, message);
    }

    @Override
    public void debug(String tag, String message, Throwable exception) {
        if (logLevel >= LOG_DEBUG) getApplicationLogger().debug(tag, message, exception);
    }

    @Override
    public void log(String tag, String message) {
        if (logLevel >= LOG_INFO) getApplicationLogger().log(tag, message);
    }

    @Override
    public void log(String tag, String message, Throwable exception) {
        if (logLevel >= LOG_INFO) getApplicationLogger().log(tag, message, exception);
    }

    @Override
    public void error(String tag, String message) {
        if (logLevel >= LOG_ERROR) getApplicationLogger().error(tag, message);
    }

    @Override
    public void error(String tag, String message, Throwable exception) {
        if (logLevel >= LOG_ERROR) getApplicationLogger().error(tag, message, exception);
    }

    @Override
    public int getLogLevel() {
        return logLevel;
    }

    @Override
    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public ApplicationLogger getApplicationLogger() {
        return applicationLogger;
    }

    @Override
    public void setApplicationLogger(ApplicationLogger applicationLogger) {
        this.applicationLogger = applicationLogger;
    }

    @Override
    public void exit() {
        // no-op
    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        synchronized (lifecycleListeners) {
            lifecycleListeners.add(listener);
        }
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        synchronized (lifecycleListeners) {
            lifecycleListeners.removeValue(listener, true);
        }
    }

    @Override
    public Context getContext() {
        return service;
    }

    @Override
    public Array<Runnable> getRunnables() {
        return runnables;
    }

    @Override
    public Array<Runnable> getExecutedRunnables() {
        return executedRunnables;
    }

    @Override
    public SnapshotArray<LifecycleListener> getLifecycleListeners() {
        return lifecycleListeners;
    }

    @Override
    public void startActivity(Intent intent) {
        service.startActivity(intent);
    }

    @Override
    public Window getApplicationWindow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Handler getHandler() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AndroidAudio createAudio(Context context, AndroidApplicationConfiguration config) {
        if (!config.disableAudio)
            return new DefaultAndroidAudio(context, config);
        else
            return new DisabledAndroidAudio();
    }

    @Override
    public AndroidInput createInput(Application activity, Context context, Object view, AndroidApplicationConfiguration config) {
        return new DefaultAndroidInput(this, this.getService(), graphics.view, config);
    }

    protected AndroidFiles createFiles() {
        // added initialization of android local storage: /data/data/<app package>/files/
        this.getService().getFilesDir(); // workaround for Android bug #10515463
        return new DefaultAndroidFiles(this.getService().getAssets(), this.getService(), true);
    }

    @Override
    public void runOnUiThread(Runnable runnable) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            // The current thread is not the UI thread.
            // Let's post the runnable to the event queue of the UI thread.
            new Handler(Looper.getMainLooper()).post(runnable);
        } else {
            // The current thread is the UI thread already.
            // Let's execute the runnable immediately.
            runnable.run();
        }
    }

    @Override
    public void useImmersiveMode(boolean b) {
        throw new UnsupportedOperationException();
    }

    /**
     * Notify the wallpaper engine that the significant colors of the wallpaper have changed. This method may be called before
     * initializing the live wallpaper.
     *
     * @param primaryColor   The most visually significant color.
     * @param secondaryColor The second most visually significant color.
     * @param tertiaryColor  The third most visually significant color.
     */
    public void notifyColorsChanged(Color primaryColor, Color secondaryColor, Color tertiaryColor) {
        if (Build.VERSION.SDK_INT < 27) return;
        final Color[] colors = new Color[3];
        colors[0] = new Color(primaryColor);
        colors[1] = new Color(secondaryColor);
        colors[2] = new Color(tertiaryColor);
        wallpaperColors = colors;
        AndroidLiveWallpaperService.AndroidWallpaperEngine engine = service.linkedEngine;
        if (engine != null) engine.notifyColorsChanged();
    }
}
