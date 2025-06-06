package com.badlogic.gdx.backends.headless;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.headless.mock.audio.MockAudio;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGraphics;
import com.badlogic.gdx.backends.headless.mock.input.MockInput;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * a headless implementation of a GDX Application primarily intended to be used in servers.
 */
public class HeadlessApplication implements Application {
    protected final ApplicationListener listener;
    protected final HeadlessFiles files;
    protected final HeadlessNet net;
    protected final MockAudio audio;
    protected final MockInput input;
    protected final MockGraphics graphics;
    protected final Array<Runnable> runnables = new Array<Runnable>();
    protected final Array<Runnable> executedRunnables = new Array<Runnable>();
    protected final Array<LifecycleListener> lifecycleListeners = new Array<LifecycleListener>();
    protected Thread mainLoopThread;
    protected boolean running = true;
    protected int logLevel = LOG_INFO;
    protected ApplicationLogger applicationLogger;
    ObjectMap<String, Preferences> preferences = new ObjectMap<String, Preferences>();
    private final String preferencesdir;

    public HeadlessApplication(ApplicationListener listener) {
        this(listener, null);
    }

    public HeadlessApplication(ApplicationListener listener, HeadlessApplicationConfiguration config) {
        if (config == null) config = new HeadlessApplicationConfiguration();

        HeadlessNativesLoader.load();
        setApplicationLogger(new HeadlessApplicationLogger());
        this.listener = listener;
        this.files = new HeadlessFiles();
        this.net = new HeadlessNet(config);
        // the following elements are not applicable for headless applications
        // they are only implemented as mock objects
        this.graphics = new MockGraphics();
        this.graphics.setForegroundFPS(config.updatesPerSecond);
        this.audio = new MockAudio();
        this.input = new MockInput();

        this.preferencesdir = config.preferencesDirectory;

        Gdx.app = this;
        Gdx.files = files;
        Gdx.net = net;
        Gdx.audio = audio;
        Gdx.graphics = graphics;
        Gdx.input = input;

        initialize();
    }

    private void initialize() {
        mainLoopThread = new Thread("HeadlessApplication") {
            @Override
            public void run() {
                try {
                    HeadlessApplication.this.mainLoop();
                } catch (Throwable t) {
                    if (t instanceof RuntimeException)
                        throw (RuntimeException) t;
                    else
                        throw new GdxRuntimeException(t);
                }
            }
        };
        mainLoopThread.start();
    }

    protected void mainLoop() {
        Array<LifecycleListener> lifecycleListeners = this.lifecycleListeners;

        listener.create();

        // unlike LwjglApplication, a headless application will eat up CPU in this while loop
        // it is up to the implementation to call Thread.sleep as necessary
        long t = TimeUtils.nanoTime() + graphics.getTargetRenderInterval();
        if (graphics.getTargetRenderInterval() >= 0f) {
            while (running) {
                final long n = TimeUtils.nanoTime();
                if (t > n) {
                    try {
                        long sleep = t - n;
                        Thread.sleep(sleep / 1000000, (int) (sleep % 1000000));
                    } catch (InterruptedException e) {
                    }
                    t = t + graphics.getTargetRenderInterval();
                } else
                    t = n + graphics.getTargetRenderInterval();

                executeRunnables();
                graphics.incrementFrameId();
                listener.render();
                graphics.updateTime();

                // If one of the runnables set running to false, for example after an exit().
                if (!running) break;
            }
        }

        synchronized (lifecycleListeners) {
            for (LifecycleListener listener : lifecycleListeners) {
                listener.pause();
                listener.dispose();
            }
        }
        listener.pause();
        listener.dispose();
    }

    public boolean executeRunnables() {
        synchronized (runnables) {
            for (int i = runnables.size - 1; i >= 0; i--)
                executedRunnables.add(runnables.get(i));
            runnables.clear();
        }
        if (executedRunnables.size == 0) return false;
        for (int i = executedRunnables.size - 1; i >= 0; i--)
            executedRunnables.removeIndex(i).run();
        return true;
    }

    @Override
    public ApplicationListener getApplicationListener() {
        return listener;
    }

    @Override
    public Graphics getGraphics() {
        return graphics;
    }

    @Override
    public Audio getAudio() {
        return audio;
    }

    @Override
    public Input getInput() {
        return input;
    }

    @Override
    public Files getFiles() {
        return files;
    }

    @Override
    public Net getNet() {
        return net;
    }

    @Override
    public ApplicationType getType() {
        return ApplicationType.HeadlessDesktop;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public long getJavaHeap() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    @Override
    public long getNativeHeap() {
        return getJavaHeap();
    }

    @Override
    public Preferences getPreferences(String name) {
        if (preferences.containsKey(name)) {
            return preferences.get(name);
        } else {
            Preferences prefs = new HeadlessPreferences(name, this.preferencesdir);
            preferences.put(name, prefs);
            return prefs;
        }
    }

    @Override
    public Clipboard getClipboard() {
        // no clipboards for headless apps
        return null;
    }

    @Override
    public void postRunnable(Runnable runnable) {
        synchronized (runnables) {
            runnables.add(runnable);
        }
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
        postRunnable(new Runnable() {
            @Override
            public void run() {
                running = false;
            }
        });
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
}
