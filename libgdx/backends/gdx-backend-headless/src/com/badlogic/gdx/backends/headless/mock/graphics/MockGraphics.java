package com.badlogic.gdx.backends.headless.mock.graphics;

import com.badlogic.gdx.AbstractGraphics;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.GL31;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.GLVersion;

/**
 * The headless backend does its best to mock elements. This is intended to make code-sharing between server and client as simple
 * as possible.
 */
public class MockGraphics extends AbstractGraphics {
    long frameId = -1;
    float deltaTime = 0;
    long frameStart = 0;
    int frames = 0;
    int fps;
    long lastTime = System.nanoTime();
    long targetRenderInterval;
    GLVersion glVersion = new GLVersion(Application.ApplicationType.HeadlessDesktop, "", "", "");

    @Override
    public boolean isGL30Available() {
        return false;
    }

    @Override
    public boolean isGL31Available() {
        return false;
    }

    @Override
    public boolean isGL32Available() {
        return false;
    }

    @Override
    public GL20 getGL20() {
        return null;
    }

    @Override
    public void setGL20(GL20 gl20) {

    }

    @Override
    public GL30 getGL30() {
        return null;
    }

    @Override
    public void setGL30(GL30 gl30) {

    }

    @Override
    public GL31 getGL31() {
        return null;
    }

    @Override
    public void setGL31(GL31 gl31) {

    }

    @Override
    public GL32 getGL32() {
        return null;
    }

    @Override
    public void setGL32(GL32 gl32) {

    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getBackBufferWidth() {
        return 0;
    }

    @Override
    public int getBackBufferHeight() {
        return 0;
    }

    @Override
    public long getFrameId() {
        return frameId;
    }

    @Override
    public float getDeltaTime() {
        return deltaTime;
    }

    @Override
    public int getFramesPerSecond() {
        return fps;
    }

    @Override
    public GraphicsType getType() {
        return GraphicsType.Mock;
    }

    @Override
    public GLVersion getGLVersion() {
        return glVersion;
    }

    @Override
    public float getPpiX() {
        return 0;
    }

    @Override
    public float getPpiY() {
        return 0;
    }

    @Override
    public float getPpcX() {
        return 0;
    }

    @Override
    public float getPpcY() {
        return 0;
    }

    @Override
    public boolean supportsDisplayModeChange() {
        return false;
    }

    @Override
    public DisplayMode[] getDisplayModes() {
        return new DisplayMode[0];
    }

    @Override
    public DisplayMode getDisplayMode() {
        return null;
    }

    @Override
    public int getSafeInsetLeft() {
        return 0;
    }

    @Override
    public int getSafeInsetTop() {
        return 0;
    }

    @Override
    public int getSafeInsetBottom() {
        return 0;
    }

    @Override
    public int getSafeInsetRight() {
        return 0;
    }

    @Override
    public boolean setFullscreenMode(DisplayMode displayMode) {
        return false;
    }

    @Override
    public boolean setWindowedMode(int width, int height) {
        return false;
    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setVSync(boolean vsync) {

    }

    /**
     * Sets the target framerate for the application. Use 0 to never sleep; negative to not call the render method at all. Default
     * is 60.
     *
     * @param fps fps
     */
    @Override
    public void setForegroundFPS(int fps) {
        this.targetRenderInterval = (long) (fps <= 0 ? (fps == 0 ? 0 : -1) : ((1F / fps) * 1000000000F));
    }

    public long getTargetRenderInterval() {
        return targetRenderInterval;
    }

    @Override
    public BufferFormat getBufferFormat() {
        return null;
    }

    @Override
    public boolean supportsExtension(String extension) {
        return false;
    }

    @Override
    public boolean isContinuousRendering() {
        return false;
    }

    @Override
    public void setContinuousRendering(boolean isContinuous) {

    }

    @Override
    public void requestRendering() {

    }

    @Override
    public boolean isFullscreen() {
        return false;
    }

    public void updateTime() {
        long time = System.nanoTime();
        deltaTime = (time - lastTime) / 1000000000.0f;
        lastTime = time;

        if (time - frameStart >= 1000000000) {
            fps = frames;
            frames = 0;
            frameStart = time;
        }
        frames++;
    }

    public void incrementFrameId() {
        frameId++;
    }

    @Override
    public Cursor newCursor(Pixmap pixmap, int xHotspot, int yHotspot) {
        return null;
    }

    @Override
    public void setCursor(Cursor cursor) {
    }

    @Override
    public void setSystemCursor(SystemCursor systemCursor) {
    }

    @Override
    public Monitor getPrimaryMonitor() {
        return null;
    }

    @Override
    public Monitor getMonitor() {
        return null;
    }

    @Override
    public Monitor[] getMonitors() {
        return null;
    }

    @Override
    public DisplayMode[] getDisplayModes(Monitor monitor) {
        return null;
    }

    @Override
    public DisplayMode getDisplayMode(Monitor monitor) {
        return null;
    }

    @Override
    public void setUndecorated(boolean undecorated) {

    }

    @Override
    public void setResizable(boolean resizable) {

    }
}
