package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

/**
 * To deal with HDPI monitors properly, use the glViewport and glScissor functions of this class instead of directly calling
 * OpenGL yourself. The logical coordinate system provided by the operating system may not have the same resolution as the actual
 * drawing surface to which OpenGL draws, also known as the backbuffer. This class will ensure, that you pass the correct values
 * to OpenGL for any function that expects backbuffer coordinates instead of logical coordinates.
 */
public class HdpiUtils {
    private static HdpiMode mode = HdpiMode.Logical;

    /**
     * Allows applications to override HDPI coordinate conversion for glViewport and glScissor calls.
     * <p>
     * This function can be used to ignore the default behavior, for example when rendering a UI stage to an off-screen
     * framebuffer:
     *
     * <pre>
     * HdpiUtils.setMode(HdpiMode.Pixels);
     * fb.begin();
     * stage.draw();
     * fb.end();
     * HdpiUtils.setMode(HdpiMode.Logical);
     * </pre>
     *
     * @param mode set to HdpiMode.Pixels to ignore HDPI conversion for glViewport and glScissor functions
     */
    public static void setMode(HdpiMode mode) {
        HdpiUtils.mode = mode;
    }

    /**
     * Calls {@link GL20#glScissor(int, int, int, int)}, expecting the coordinates and sizes given in logical coordinates and
     * automatically converts them to backbuffer coordinates, which may be bigger on HDPI screens.
     */
    public static void glScissor(int x, int y, int width, int height) {
        if (mode == HdpiMode.Logical && (Gdx.graphics.getWidth() != Gdx.graphics.getBackBufferWidth()
                || Gdx.graphics.getHeight() != Gdx.graphics.getBackBufferHeight())) {
            Gdx.gl.glScissor(toBackBufferX(x), toBackBufferY(y), toBackBufferX(width), toBackBufferY(height));
        } else {
            Gdx.gl.glScissor(x, y, width, height);
        }
    }

    /**
     * Calls {@link GL20#glViewport(int, int, int, int)}, expecting the coordinates and sizes given in logical coordinates and
     * automatically converts them to backbuffer coordinates, which may be bigger on HDPI screens.
     */
    public static void glViewport(int x, int y, int width, int height) {
        if (mode == HdpiMode.Logical && (Gdx.graphics.getWidth() != Gdx.graphics.getBackBufferWidth()
                || Gdx.graphics.getHeight() != Gdx.graphics.getBackBufferHeight())) {
            Gdx.gl.glViewport(toBackBufferX(x), toBackBufferY(y), toBackBufferX(width), toBackBufferY(height));
        } else {
            Gdx.gl.glViewport(x, y, width, height);
        }
    }

    /**
     * Converts an x-coordinate given in backbuffer coordinates to logical screen coordinates.
     */
    public static int toLogicalX(int backBufferX) {
        return (int) (backBufferX * Gdx.graphics.getWidth() / (float) Gdx.graphics.getBackBufferWidth());
    }

    /**
     * Converts an y-coordinate given in backbuffer coordinates to logical screen coordinates.
     */
    public static int toLogicalY(int backBufferY) {
        return (int) (backBufferY * Gdx.graphics.getHeight() / (float) Gdx.graphics.getBackBufferHeight());
    }

    /**
     * Converts an x-coordinate given in logical screen coordinates to backbuffer coordinates.
     */
    public static int toBackBufferX(int logicalX) {
        return (int) (logicalX * Gdx.graphics.getBackBufferWidth() / (float) Gdx.graphics.getWidth());
    }

    /**
     * Converts an y-coordinate given in logical screen coordinates to backbuffer coordinates.
     */
    public static int toBackBufferY(int logicalY) {
        return (int) (logicalY * Gdx.graphics.getBackBufferHeight() / (float) Gdx.graphics.getHeight());
    }
}
