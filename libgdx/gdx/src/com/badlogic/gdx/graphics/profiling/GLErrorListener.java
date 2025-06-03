package com.badlogic.gdx.graphics.profiling;

import static com.badlogic.gdx.graphics.profiling.GLInterceptor.resolveErrorNumber;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Listener for GL errors detected by {@link GLProfiler}.
 *
 * @see GLProfiler
 */
public interface GLErrorListener {

    /**
     * Listener that will log using Gdx.app.error GL error name and GL function.
     */
    GLErrorListener LOGGING_LISTENER = new GLErrorListener() {
        @Override
        public void onError(int error) {
            String place = null;
            try {
                final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
                for (int i = 0; i < stack.length; i++) {
                    if ("check".equals(stack[i].getMethodName())) {
                        if (i + 1 < stack.length) {
                            final StackTraceElement glMethod = stack[i + 1];
                            place = glMethod.getMethodName();
                        }
                        break;
                    }
                }
            } catch (Exception ignored) {
            }

            if (place != null) {
                Gdx.app.error("GLProfiler", "Error " + resolveErrorNumber(error) + " from " + place);
            } else {
                Gdx.app.error("GLProfiler", "Error " + resolveErrorNumber(error) + " at: ", new Exception());
                // This will capture current stack trace for logging, if possible
            }
        }
    };

    // Basic implementations
    /**
     * Listener that will throw a GdxRuntimeException with error name.
     */
    GLErrorListener THROWING_LISTENER = new GLErrorListener() {
        @Override
        public void onError(int error) {
            throw new GdxRuntimeException("GLProfiler: Got GL error " + resolveErrorNumber(error));
        }
    };

    /**
     * Put your error logging code here.
     *
     * @see GLInterceptor#resolveErrorNumber(int)
     */
    void onError(int error);
}
