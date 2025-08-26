package com.badlogic.gdx.backends.android.surfaceview;

import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.EGLConfigChooser;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * {@link EGLConfigChooser} implementation for GLES 1.x and 2.0. Let's hope this really works for all devices. Includes MSAA/CSAA
 * config selection if requested. Taken from GLSurfaceView20, heavily modified to accommodate MSAA/CSAA.
 */
public class GdxEglConfigChooser implements GLSurfaceView.EGLConfigChooser {
    public static final int EGL_COVERAGE_BUFFERS_NV = 0x30E0;
    public static final int EGL_COVERAGE_SAMPLES_NV = 0x30E1;
    private static final int EGL_OPENGL_ES2_BIT = 4;
    protected final int[] mConfigAttribs;
    protected int mRedSize;
    protected int mGreenSize;
    protected int mBlueSize;
    protected int mAlphaSize;
    protected int mDepthSize;
    protected int mStencilSize;
    protected int mNumSamples;
    private final int[] mValue = new int[1];

    public GdxEglConfigChooser(int r, int g, int b, int a, int depth, int stencil, int numSamples) {
        mRedSize = r;
        mGreenSize = g;
        mBlueSize = b;
        mAlphaSize = a;
        mDepthSize = depth;
        mStencilSize = stencil;
        mNumSamples = numSamples;

        mConfigAttribs = new int[]{EGL10.EGL_RED_SIZE, 4, EGL10.EGL_GREEN_SIZE, 4, EGL10.EGL_BLUE_SIZE, 4,
                EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT, EGL10.EGL_NONE};
    }

    public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
        // get (almost) all configs available by using r=g=b=4 so we
        // can chose with big confidence :)
        int[] num_config = new int[1];
        egl.eglChooseConfig(display, mConfigAttribs, null, 0, num_config);
        int numConfigs = num_config[0];

        if (numConfigs <= 0) {
            throw new IllegalArgumentException("No configs match configSpec");
        }

        // now actually read the configurations.
        EGLConfig[] configs = new EGLConfig[numConfigs];
        egl.eglChooseConfig(display, mConfigAttribs, configs, numConfigs, num_config);

        return chooseConfig(egl, display, configs);
    }

    public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs) {
        EGLConfig best = null;
        EGLConfig bestAA = null;
        EGLConfig safe = null; // default back to 565 when no exact match found

        for (EGLConfig config : configs) {
            int d = findConfigAttrib(egl, display, config, EGL10.EGL_DEPTH_SIZE);
            int s = findConfigAttrib(egl, display, config, EGL10.EGL_STENCIL_SIZE);

            // We need at least mDepthSize and mStencilSize bits
            if (d < mDepthSize || s < mStencilSize) continue;

            // We want an *exact* match for red/green/blue/alpha
            int r = findConfigAttrib(egl, display, config, EGL10.EGL_RED_SIZE);
            int g = findConfigAttrib(egl, display, config, EGL10.EGL_GREEN_SIZE);
            int b = findConfigAttrib(egl, display, config, EGL10.EGL_BLUE_SIZE);
            int a = findConfigAttrib(egl, display, config, EGL10.EGL_ALPHA_SIZE);

            // Match RGB565 as a fallback
            if (safe == null && r == 5 && g == 6 && b == 5 && a == 0) {
                safe = config;
            }
            // if we have a match, we chose this as our non AA fallback if that one
            // isn't set already.
            if (best == null && r == mRedSize && g == mGreenSize && b == mBlueSize && a == mAlphaSize) {
                best = config;

                // if no AA is requested we can bail out here.
                if (mNumSamples == 0) {
                    break;
                }
            }

            // now check for MSAA support
            int hasSampleBuffers = findConfigAttrib(egl, display, config, EGL10.EGL_SAMPLE_BUFFERS);
            int numSamples = findConfigAttrib(egl, display, config, EGL10.EGL_SAMPLES);

            // We take the first sort of matching config, thank you.
            if (bestAA == null && hasSampleBuffers == 1 && numSamples >= mNumSamples && r == mRedSize && g == mGreenSize
                    && b == mBlueSize && a == mAlphaSize) {
                bestAA = config;
                continue;
            }

            // for this to work we need to call the extension glCoverageMaskNV which is not
            // exposed in the Android bindings. We'd have to link agains the NVidia SDK and
            // that is simply not going to happen.
// // still no luck, let's try CSAA support
            hasSampleBuffers = findConfigAttrib(egl, display, config, EGL_COVERAGE_BUFFERS_NV);
            numSamples = findConfigAttrib(egl, display, config, EGL_COVERAGE_SAMPLES_NV);

            // We take the first sort of matching config, thank you.
            if (bestAA == null && hasSampleBuffers == 1 && numSamples >= mNumSamples && r == mRedSize && g == mGreenSize
                    && b == mBlueSize && a == mAlphaSize) {
                bestAA = config;
            }
        }

        if (bestAA != null)
            return bestAA;
        else if (best != null)
            return best;
        else
            return safe;
    }

    private int findConfigAttrib(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute) {
        if (egl.eglGetConfigAttrib(display, config, attribute, mValue)) {
            return mValue[0];
        }
        return 0;
    }

}
