package com.badlogic.gdx.backends.android;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.EGLConfigChooser;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import androidx.annotation.NonNull;

import com.badlogic.gdx.AbstractGraphics;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20;
import com.badlogic.gdx.backends.android.surfaceview.GdxEglConfigChooser;
import com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.GL31;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureArray;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.SnapshotArray;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

/**
 * An implementation of {@link Graphics} for Android.
 */
public class AndroidGraphics extends AbstractGraphics implements Renderer {

    private static final String LOG_TAG = "AndroidGraphics";

    /**
     * When {@link AndroidFragmentApplication#onPause()} or {@link AndroidApplication#onPause()} call
     * {@link AndroidGraphics#pause()} they <b>MUST</b> enforce continuous rendering. If not, {@link #onDrawFrame(GL10)} will not
     * be called in the GLThread while {@link #pause()} is sleeping in the Android UI Thread which will cause the
     * {@link AndroidGraphics#pause} variable never be set to false. As a result, the {@link AndroidGraphics#pause()} method will
     * kill the current process to avoid ANR
     */
    static volatile boolean enforceContinuousRendering = false;
    protected final AndroidApplicationConfiguration config;
    final GLSurfaceView20 view;
    protected long lastFrameTime = System.nanoTime();
    protected float deltaTime = 0;
    protected long frameStart = System.nanoTime();
    protected long frameId = -1;
    protected int frames = 0;
    protected int fps;
    int width;
    int height;
    int safeInsetLeft, safeInsetTop, safeInsetBottom, safeInsetRight;
    AndroidApplicationBase app;
    GL20 gl20;
    GL30 gl30;
    EGLContext eglContext;
    GLVersion glVersion;
    String extensions;
    volatile boolean created = false;
    volatile boolean running = false;
    volatile boolean pause = false;
    volatile boolean resume = false;
    volatile boolean destroy = false;
    int[] value = new int[1];
    Object synch = new Object();
    private float ppiX = 0;
    private float ppiY = 0;
    private float ppcX = 0;
    private float ppcY = 0;
    private float density = 1;
    private BufferFormat bufferFormat = new BufferFormat(8, 8, 8, 0, 16, 0, 0, false);
    private boolean isContinuous = true;

    public AndroidGraphics(AndroidApplicationBase application, AndroidApplicationConfiguration config,
                           ResolutionStrategy resolutionStrategy) {
        this(application, config, resolutionStrategy, true);
    }

    public AndroidGraphics(AndroidApplicationBase application, AndroidApplicationConfiguration config,
                           ResolutionStrategy resolutionStrategy, boolean focusableView) {
        this.config = config;
        this.app = application;
        view = createGLSurfaceView(application, resolutionStrategy);
        preserveEGLContextOnPause();
        if (focusableView) {
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
        }
    }

    protected void preserveEGLContextOnPause() {
        view.setPreserveEGLContextOnPause(true);
    }

    protected GLSurfaceView20 createGLSurfaceView(AndroidApplicationBase application,
                                                  final ResolutionStrategy resolutionStrategy) {
        if (!checkGL20()) throw new GdxRuntimeException("libGDX requires OpenGL ES 2.0");

        EGLConfigChooser configChooser = getEglConfigChooser();
        GLSurfaceView20 view = new GLSurfaceView20(application.getContext(), resolutionStrategy, config.useGL30 ? 3 : 2);
        if (configChooser != null)
            view.setEGLConfigChooser(configChooser);
        else
            view.setEGLConfigChooser(config.r, config.g, config.b, config.a, config.depth, config.stencil);
        view.setRenderer(this);
        return view;
    }

    public void onPauseGLSurfaceView() {
        if (view != null) {
            view.onPause();
        }
    }

    public void onResumeGLSurfaceView() {
        if (view != null) {
            view.onResume();
        }
    }

    protected EGLConfigChooser getEglConfigChooser() {
        return new GdxEglConfigChooser(config.r, config.g, config.b, config.a, config.depth, config.stencil, config.numSamples);
    }

    protected void updatePpi() {
        DisplayMetrics metrics = new DisplayMetrics();
        app.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        ppiX = metrics.xdpi;
        ppiY = metrics.ydpi;
        ppcX = metrics.xdpi / 2.54f;
        ppcY = metrics.ydpi / 2.54f;
        density = metrics.density;
    }

    protected boolean checkGL20() {
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

        int[] version = new int[2];
        egl.eglInitialize(display, version);

        int EGL_OPENGL_ES2_BIT = 4;
        int[] configAttribs = {EGL10.EGL_RED_SIZE, 4, EGL10.EGL_GREEN_SIZE, 4, EGL10.EGL_BLUE_SIZE, 4, EGL10.EGL_RENDERABLE_TYPE,
                EGL_OPENGL_ES2_BIT, EGL10.EGL_NONE};

        EGLConfig[] configs = new EGLConfig[10];
        int[] num_config = new int[1];
        egl.eglChooseConfig(display, configAttribs, configs, 10, num_config);
        egl.eglTerminate(display);
        return num_config[0] > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GL20 getGL20() {
        return gl20;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGL20(@NonNull GL20 gl20) {
        this.gl20 = gl20;
        if (gl30 == null) {
            Gdx.gl = gl20;
            Gdx.gl20 = gl20;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGL30Available() {
        return gl30 != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GL30 getGL30() {
        return gl30;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGL30(@NonNull GL30 gl30) {
        this.gl30 = gl30;
        this.gl20 = gl30;

        Gdx.gl = gl20;
        Gdx.gl20 = gl20;
        Gdx.gl30 = gl30;
    }

    @Override
    public boolean isGL31Available() {
        return false;
    }

    @Override
    public GL31 getGL31() {
        return null;
    }

    @Override
    public void setGL31(@NonNull GL31 gl31) {

    }

    @Override
    public boolean isGL32Available() {
        return false;
    }

    @Override
    public GL32 getGL32() {
        return null;
    }

    @Override
    public void setGL32(@NonNull GL32 gl32) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getBackBufferWidth() {
        return width;
    }

    @Override
    public int getBackBufferHeight() {
        return height;
    }

    /**
     * This instantiates the GL10, GL11 and GL20 instances. Includes the check for certain devices that pretend to support GL11
     * but fuck up vertex buffer objects. This includes the pixelflinger which segfaults when buffers are deleted as well as the
     * Motorola CLIQ and the Samsung Behold II.
     */
    protected void setupGL(javax.microedition.khronos.opengles.GL10 gl) {
        String versionString = gl.glGetString(GL10.GL_VERSION);
        String vendorString = gl.glGetString(GL10.GL_VENDOR);
        String rendererString = gl.glGetString(GL10.GL_RENDERER);
        glVersion = new GLVersion(Application.ApplicationType.Android, versionString, vendorString, rendererString);
        if (config.useGL30 && glVersion.getMajorVersion() > 2) {
            if (gl30 != null) return;
            gl20 = gl30 = new AndroidGL30();

            Gdx.gl = gl30;
            Gdx.gl20 = gl30;
            Gdx.gl30 = gl30;
        } else {
            if (gl20 != null) return;
            gl20 = new AndroidGL20();

            Gdx.gl = gl20;
            Gdx.gl20 = gl20;
        }

        Gdx.app.log(LOG_TAG, "OGL renderer: " + gl.glGetString(GL10.GL_RENDERER));
        Gdx.app.log(LOG_TAG, "OGL vendor: " + gl.glGetString(GL10.GL_VENDOR));
        Gdx.app.log(LOG_TAG, "OGL version: " + gl.glGetString(GL10.GL_VERSION));
        Gdx.app.log(LOG_TAG, "OGL extensions: " + gl.glGetString(GL10.GL_EXTENSIONS));
    }

    @Override
    public void onSurfaceChanged(javax.microedition.khronos.opengles.GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        updatePpi();
        updateSafeAreaInsets();
        gl.glViewport(0, 0, this.width, this.height);
        if (!created) {
            app.getApplicationListener().create();
            created = true;
            synchronized (this) {
                running = true;
            }
        }
        app.getApplicationListener().resize(width, height);
    }

    @Override
    public void onSurfaceCreated(javax.microedition.khronos.opengles.GL10 gl, EGLConfig config) {
        eglContext = ((EGL10) EGLContext.getEGL()).eglGetCurrentContext();
        setupGL(gl);
        logConfig(config);
        updatePpi();
        updateSafeAreaInsets();

        Mesh.invalidateAllMeshes(app);
        Texture.invalidateAllTextures(app);
        Cubemap.invalidateAllCubemaps(app);
        TextureArray.invalidateAllTextureArrays(app);
        ShaderProgram.invalidateAllShaderPrograms(app);
        FrameBuffer.invalidateAllFrameBuffers(app);

        logManagedCachesStatus();

        Display display = app.getWindowManager().getDefaultDisplay();
        this.width = display.getWidth();
        this.height = display.getHeight();
        this.lastFrameTime = System.nanoTime();

        gl.glViewport(0, 0, this.width, this.height);
    }

    protected void logConfig(EGLConfig config) {
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        int r = getAttrib(egl, display, config, EGL10.EGL_RED_SIZE);
        int g = getAttrib(egl, display, config, EGL10.EGL_GREEN_SIZE);
        int b = getAttrib(egl, display, config, EGL10.EGL_BLUE_SIZE);
        int a = getAttrib(egl, display, config, EGL10.EGL_ALPHA_SIZE);
        int d = getAttrib(egl, display, config, EGL10.EGL_DEPTH_SIZE);
        int s = getAttrib(egl, display, config, EGL10.EGL_STENCIL_SIZE);
        int samples = Math.max(getAttrib(egl, display, config, EGL10.EGL_SAMPLES),
                getAttrib(egl, display, config, GdxEglConfigChooser.EGL_COVERAGE_SAMPLES_NV));
        boolean coverageSample = getAttrib(egl, display, config, GdxEglConfigChooser.EGL_COVERAGE_SAMPLES_NV) != 0;

        Gdx.app.log(LOG_TAG, "framebuffer: (" + r + ", " + g + ", " + b + ", " + a + ")");
        Gdx.app.log(LOG_TAG, "depthbuffer: (" + d + ")");
        Gdx.app.log(LOG_TAG, "stencilbuffer: (" + s + ")");
        Gdx.app.log(LOG_TAG, "samples: (" + samples + ")");
        Gdx.app.log(LOG_TAG, "coverage sampling: (" + coverageSample + ")");

        bufferFormat = new BufferFormat(r, g, b, a, d, s, samples, coverageSample);
    }

    private int getAttrib(EGL10 egl, EGLDisplay display, EGLConfig config, int attrib) {
        if (egl.eglGetConfigAttrib(display, config, attrib, value)) {
            return value[0];
        }
        return 0;
    }

    void resume() {
        synchronized (synch) {
            running = true;
            resume = true;
        }
    }

    void pause() {
        synchronized (synch) {
            if (!running) return;
            running = false;
            pause = true;

            view.queueEvent(() -> {
                if (!pause) {
                    // pause event already picked up by onDrawFrame
                    return;
                }

                // it's ok to call ApplicationListener's events
                // from onDrawFrame because it's executing in GL thread
                onDrawFrame(null);
            });

            while (pause) {
                try {
                    // Android ANR time is 5 seconds, so wait up to 4 seconds before assuming
                    // deadlock and killing process.
                    synch.wait(4000);
                    if (pause) {
                        // pause will never go false if onDrawFrame is never called by the GLThread
                        // when entering this method, we MUST enforce continuous rendering
                        Gdx.app.error(LOG_TAG, "waiting for pause synchronization took too long; assuming deadlock and killing");
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                } catch (InterruptedException ignored) {
                    Gdx.app.log(LOG_TAG, "waiting for pause synchronization failed!");
                }
            }
        }
    }

    void destroy() {
        synchronized (synch) {
            running = false;
            destroy = true;

            while (destroy) {
                try {
                    synch.wait();
                } catch (InterruptedException ex) {
                    Gdx.app.log(LOG_TAG, "waiting for destroy synchronization failed!");
                }
            }
        }
    }

    @Override
    public void onDrawFrame(javax.microedition.khronos.opengles.GL10 gl) {
        long time = System.nanoTime();
        // After pause deltaTime can have somewhat huge value that destabilizes the mean, so let's cut it off
        if (!resume) {
            deltaTime = (time - lastFrameTime) / 1000000000.0f;
        } else {
            deltaTime = 0;
        }
        lastFrameTime = time;

        boolean lrunning;
        boolean lpause;
        boolean ldestroy;
        boolean lresume;

        synchronized (synch) {
            lrunning = running;
            lpause = pause;
            ldestroy = destroy;
            lresume = resume;

            if (resume) {
                resume = false;
            }

            if (pause) {
                pause = false;
                synch.notifyAll();
            }

            if (destroy) {
                destroy = false;
                synch.notifyAll();
            }
        }

        if (lresume) {
            SnapshotArray<LifecycleListener> lifecycleListeners = app.getLifecycleListeners();
            synchronized (lifecycleListeners) {
                LifecycleListener[] listeners = lifecycleListeners.begin();
                for (int i = 0, n = lifecycleListeners.size; i < n; ++i) {
                    listeners[i].resume();
                }
                lifecycleListeners.end();
            }
            app.getApplicationListener().resume();
            Gdx.app.log(LOG_TAG, "resumed");
        }

        if (lrunning) {
            synchronized (app.getRunnables()) {
                app.getExecutedRunnables().clear();
                app.getExecutedRunnables().addAll(app.getRunnables());
                app.getRunnables().clear();
            }

            for (int i = 0; i < app.getExecutedRunnables().size; i++) {
                app.getExecutedRunnables().get(i).run();
            }
            app.getInput().processEvents();
            frameId++;
            app.getApplicationListener().render();
        }

        if (lpause) {
            SnapshotArray<LifecycleListener> lifecycleListeners = app.getLifecycleListeners();
            synchronized (lifecycleListeners) {
                LifecycleListener[] listeners = lifecycleListeners.begin();
                for (int i = 0, n = lifecycleListeners.size; i < n; ++i) {
                    listeners[i].pause();
                }
            }
            app.getApplicationListener().pause();
            Gdx.app.log(LOG_TAG, "paused");
        }

        if (ldestroy) {
            SnapshotArray<LifecycleListener> lifecycleListeners = app.getLifecycleListeners();
            synchronized (lifecycleListeners) {
                LifecycleListener[] listeners = lifecycleListeners.begin();
                for (int i = 0, n = lifecycleListeners.size; i < n; ++i) {
                    listeners[i].dispose();
                }
            }
            app.getApplicationListener().dispose();
            Gdx.app.log(LOG_TAG, "destroyed");
        }

        if (time - frameStart > 1000000000) {
            fps = frames;
            frames = 0;
            frameStart = time;
        }
        frames++;
    }

    @Override
    public long getFrameId() {
        return frameId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getDeltaTime() {
        return deltaTime;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public GraphicsType getType() {
        return GraphicsType.AndroidGL;
    }

    @NonNull
    @Override
    public GLVersion getGLVersion() {
        return glVersion;
    }

    @Override
    public int getFramesPerSecond() {
        return fps;
    }

    public void clearManagedCaches() {
        Mesh.clearAllMeshes(app);
        Texture.clearAllTextures(app);
        Cubemap.clearAllCubemaps(app);
        TextureArray.clearAllTextureArrays(app);
        ShaderProgram.clearAllShaderPrograms(app);
        FrameBuffer.clearAllFrameBuffers(app);

        logManagedCachesStatus();
    }

    protected void logManagedCachesStatus() {
        Gdx.app.log(LOG_TAG, Mesh.getManagedStatus());
        Gdx.app.log(LOG_TAG, Texture.getManagedStatus());
        Gdx.app.log(LOG_TAG, Cubemap.getManagedStatus());
        Gdx.app.log(LOG_TAG, ShaderProgram.getManagedStatus());
        Gdx.app.log(LOG_TAG, FrameBuffer.getManagedStatus());
    }

    public View getView() {
        return view;
    }

    @Override
    public float getPpiX() {
        return ppiX;
    }

    @Override
    public float getPpiY() {
        return ppiY;
    }

    @Override
    public float getPpcX() {
        return ppcX;
    }

    @Override
    public float getPpcY() {
        return ppcY;
    }

    @Override
    public float getDensity() {
        return density;
    }

    @Override
    public boolean supportsDisplayModeChange() {
        return false;
    }

    @Override
    public boolean setFullscreenMode(@NonNull DisplayMode displayMode) {
        return false;
    }

    @NonNull
    @Override
    public Monitor getPrimaryMonitor() {
        return new AndroidMonitor(0, 0, "Primary Monitor");
    }

    @NonNull
    @Override
    public Monitor getMonitor() {
        return getPrimaryMonitor();
    }

    @NonNull
    @Override
    public Monitor[] getMonitors() {
        return new Monitor[]{getPrimaryMonitor()};
    }

    @NonNull
    @Override
    public DisplayMode[] getDisplayModes(@NonNull Monitor monitor) {
        return getDisplayModes();
    }

    @NonNull
    @Override
    public DisplayMode getDisplayMode(@NonNull Monitor monitor) {
        return getDisplayMode();
    }

    @NonNull
    @Override
    public DisplayMode[] getDisplayModes() {
        return new DisplayMode[]{getDisplayMode()};
    }

    @TargetApi(Build.VERSION_CODES.P)
    protected void updateSafeAreaInsets() {
        safeInsetLeft = 0;
        safeInsetTop = 0;
        safeInsetRight = 0;
        safeInsetBottom = 0;

        try {
            DisplayCutout displayCutout = app.getApplicationWindow().getDecorView().getRootWindowInsets().getDisplayCutout();
            if (displayCutout != null) {
                safeInsetRight = displayCutout.getSafeInsetRight();
                safeInsetBottom = displayCutout.getSafeInsetBottom();
                safeInsetTop = displayCutout.getSafeInsetTop();
                safeInsetLeft = displayCutout.getSafeInsetLeft();
            }
        } // Some Application implementations (such as Live Wallpapers) do not implement Application#getApplicationWindow()
        catch (UnsupportedOperationException e) {
            Gdx.app.log("AndroidGraphics", "Unable to get safe area insets");
        }
    }

    @Override
    public int getSafeInsetLeft() {
        return safeInsetLeft;
    }

    @Override
    public int getSafeInsetTop() {
        return safeInsetTop;
    }

    @Override
    public int getSafeInsetBottom() {
        return safeInsetBottom;
    }

    @Override
    public int getSafeInsetRight() {
        return safeInsetRight;
    }

    @Override
    public boolean setWindowedMode(int width, int height) {
        return false;
    }

    @Override
    public void setTitle(@NonNull String title) {

    }

    @Override
    public void setUndecorated(boolean undecorated) {
        final int mask = (undecorated) ? 1 : 0;
        app.getApplicationWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, mask);
    }

    @Override
    public void setResizable(boolean resizable) {

    }

    @NonNull
    @Override
    public DisplayMode getDisplayMode() {
        Display display;
        DisplayMetrics metrics = new DisplayMetrics();

        DisplayManager displayManager = (DisplayManager) app.getContext().getSystemService(Context.DISPLAY_SERVICE);
        display = displayManager.getDisplay(Display.DEFAULT_DISPLAY);
        display.getRealMetrics(metrics); // Deprecated but no direct equivalent

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        int refreshRate = MathUtils.roundPositive(display.getRefreshRate());
        int bitsPerPixel = config.r + config.g + config.b + config.a;

        return new AndroidDisplayMode(width, height, refreshRate, bitsPerPixel);
    }

    @NonNull
    @Override
    public BufferFormat getBufferFormat() {
        return bufferFormat;
    }

    @Override
    public void setVSync(boolean vsync) {
    }

    @Override
    public void setForegroundFPS(int fps) {
    }

    @Override
    public boolean supportsExtension(@NonNull String extension) {
        if (extensions == null) extensions = Gdx.gl.glGetString(GL10.GL_EXTENSIONS);
        return extensions.contains(extension);
    }

    @Override
    public boolean isContinuousRendering() {
        return isContinuous;
    }

    @Override
    public void setContinuousRendering(boolean isContinuous) {
        if (view != null) {
            // ignore setContinuousRendering(false) while pausing
            this.isContinuous = enforceContinuousRendering || isContinuous;
            int renderMode = this.isContinuous ? GLSurfaceView.RENDERMODE_CONTINUOUSLY : GLSurfaceView.RENDERMODE_WHEN_DIRTY;
            view.setRenderMode(renderMode);
        }
    }

    @Override
    public void requestRendering() {
        if (view != null) {
            view.requestRender();
        }
    }

    @Override
    public boolean isFullscreen() {
        return true;
    }

    @Override
    public Cursor newCursor(@NonNull Pixmap pixmap, int xHotspot, int yHotspot) {
        return null;
    }

    @Override
    public void setCursor(@NonNull Cursor cursor) {
    }

    @Override
    public void setSystemCursor(@NonNull SystemCursor systemCursor) {
        View view = ((AndroidGraphics) app.getGraphics()).getView();
        AndroidCursor.setSystemCursor(view, systemCursor);
    }

    private static class AndroidDisplayMode extends DisplayMode {
        protected AndroidDisplayMode(int width, int height, int refreshRate, int bitsPerPixel) {
            super(width, height, refreshRate, bitsPerPixel);
        }
    }

    private static class AndroidMonitor extends Monitor {
        public AndroidMonitor(int virtualX, int virtualY, String name) {
            super(virtualX, virtualY, name);
        }
    }
}
