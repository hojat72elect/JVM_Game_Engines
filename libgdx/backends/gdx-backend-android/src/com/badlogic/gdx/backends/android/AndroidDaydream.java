package com.badlogic.gdx.backends.android;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.service.dreams.DreamService;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.badlogic.gdx.utils.SnapshotArray;

/**
 * An implementation of the {@link Application} interface for Android. Create an {@link Activity} that derives from this class.
 * In the Activity#onCreate(Bundle) method call the {@link #initialize(ApplicationListener)} method specifying the configuration
 * for the {@link GLSurfaceView}.
 */
public class AndroidDaydream extends DreamService implements AndroidApplicationBase {

    protected final Array<Runnable> runnables = new Array<>();
    protected final Array<Runnable> executedRunnables = new Array<>();
    protected final SnapshotArray<LifecycleListener> lifecycleListeners = new SnapshotArray<>(
            LifecycleListener.class);
    protected AndroidGraphics graphics;
    protected AndroidInput input;
    protected AndroidAudio audio;
    protected AndroidFiles files;
    protected AndroidNet net;
    protected AndroidClipboard clipboard;
    protected ApplicationListener listener;
    protected Handler handler;
    protected boolean firstResume = true;
    protected int logLevel = LOG_INFO;
    protected ApplicationLogger applicationLogger;

    /**
     * This method has to be called in the Activity#onCreate(Bundle) method. It sets up all the things necessary to get input,
     * render via OpenGL and so on. Uses a default {@link AndroidApplicationConfiguration}.
     *
     * @param listener the {@link ApplicationListener} implementing the program logic
     */
    public void initialize(ApplicationListener listener) {
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(listener, config);
    }

    /**
     * This method has to be called in the Activity#onCreate(Bundle) method. It sets up all the things necessary to get input,
     * render via OpenGL and so on. You can configure other aspects of the application with the rest of the fields in the
     * {@link AndroidApplicationConfiguration} instance.
     *
     * @param listener the {@link ApplicationListener} implementing the program logic
     * @param config   the {@link AndroidApplicationConfiguration}, defining various settings of the application (use accelerometer,
     *                 etc.).
     */
    public void initialize(ApplicationListener listener, AndroidApplicationConfiguration config) {
        init(listener, config, false);
    }

    /**
     * This method has to be called in the Activity#onCreate(Bundle) method. It sets up all the things necessary to get input,
     * render via OpenGL and so on. Uses a default {@link AndroidApplicationConfiguration}.
     * <p>
     * Note: you have to add the returned view to your layout!
     *
     * @param listener the {@link ApplicationListener} implementing the program logic
     * @return the {@link GLSurfaceView} of the application
     */
    public View initializeForView(ApplicationListener listener) {
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        return initializeForView(listener, config);
    }

    /**
     * This method has to be called in the Activity#onCreate(Bundle) method. It sets up all the things necessary to get input,
     * render via OpenGL and so on. You can configure other aspects of the application with the rest of the fields in the
     * {@link AndroidApplicationConfiguration} instance.
     * <p>
     * Note: you have to add the returned view to your layout!
     *
     * @param listener the {@link ApplicationListener} implementing the program logic
     * @param config   the {@link AndroidApplicationConfiguration}, defining various settings of the application (use accelerometer,
     *                 etc.).
     * @return the {@link GLSurfaceView} of the application
     */
    public View initializeForView(ApplicationListener listener, AndroidApplicationConfiguration config) {
        init(listener, config, true);
        return graphics.getView();
    }

    private void init(ApplicationListener listener, AndroidApplicationConfiguration config, boolean isForView) {
        GdxNativesLoader.load();
        setApplicationLogger(new AndroidApplicationLogger());
        graphics = new AndroidGraphics(this, config,
                config.resolutionStrategy == null ? new FillResolutionStrategy() : config.resolutionStrategy);
        input = createInput(this, this, graphics.view, config);
        audio = createAudio(this, config);
        files = createFiles();
        net = new AndroidNet(this, config);
        this.listener = listener;
        this.handler = new Handler();
        this.clipboard = new AndroidClipboard(this);

        // Add a specialized audio lifecycle listener
        addLifecycleListener(new LifecycleListener() {

            @Override
            public void resume() {
                audio.resume();
            }

            @Override
            public void pause() {
                audio.pause();
            }

            @Override
            public void dispose() {
                audio.dispose();
                audio = null;
            }
        });

        Gdx.app = this;
        Gdx.input = this.getInput();
        Gdx.audio = this.getAudio();
        Gdx.files = this.getFiles();
        Gdx.graphics = this.getGraphics();
        Gdx.net = this.getNet();

        if (!isForView) {
            setFullscreen(true);
            setContentView(graphics.getView(), createLayoutParams());
        }

        createWakeLock(config.useWakelock);

        // detect an already connected bluetooth keyboardAvailable
        if (getResources().getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS)
            input.setKeyboardAvailable(true);
    }

    protected FrameLayout.LayoutParams createLayoutParams() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        return layoutParams;
    }

    protected void createWakeLock(boolean use) {
        if (use) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onDreamingStopped() {
        boolean isContinuous = graphics.isContinuousRendering();
        graphics.setContinuousRendering(true);
        graphics.pause();

        input.onDreamingStopped();

        graphics.clearManagedCaches();
        graphics.destroy();
        graphics.setContinuousRendering(isContinuous);

        graphics.onPauseGLSurfaceView();

        super.onDreamingStopped();
    }

    @Override
    public void onDreamingStarted() {
        Gdx.app = this;
        Gdx.input = this.getInput();
        Gdx.audio = this.getAudio();
        Gdx.files = this.getFiles();
        Gdx.graphics = this.getGraphics();
        Gdx.net = this.getNet();

        input.onDreamingStarted();

        if (graphics != null) {
            graphics.onResumeGLSurfaceView();
        }

        if (!firstResume) {
            graphics.resume();
        } else
            firstResume = false;
        super.onDreamingStarted();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public ApplicationListener getApplicationListener() {
        return listener;
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
        return new AndroidPreferences(getSharedPreferences(name, Context.MODE_PRIVATE));
    }

    @Override
    public Clipboard getClipboard() {
        return clipboard;
    }

    @Override
    public void postRunnable(Runnable runnable) {
        synchronized (runnables) {
            runnables.add(runnable);
            Gdx.graphics.requestRendering();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        boolean keyboardAvailable = config.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO;
        input.setKeyboardAvailable(keyboardAvailable);
    }

    @Override
    public void exit() {
        handler.post(AndroidDaydream.this::finish);
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
        return this;
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
    public Window getApplicationWindow() {
        return this.getWindow();
    }

    @Override
    public Handler getHandler() {
        return this.handler;
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
        return new DefaultAndroidInput(this, this, graphics.view, config);
    }

    protected AndroidFiles createFiles() {
        this.getFilesDir(); // workaround for Android bug #10515463
        return new DefaultAndroidFiles(this.getAssets(), this, true);
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
}
