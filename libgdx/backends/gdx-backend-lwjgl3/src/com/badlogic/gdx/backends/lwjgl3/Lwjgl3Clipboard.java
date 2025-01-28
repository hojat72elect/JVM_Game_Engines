package com.badlogic.gdx.backends.lwjgl3;

import org.lwjgl.glfw.GLFW;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Clipboard;

/**
 * Clipboard implementation for desktop that uses the system clipboard via GLFW.
 */
public class Lwjgl3Clipboard implements Clipboard {
    @Override
    public boolean hasContents() {
        String contents = getContents();
        return contents != null && !contents.isEmpty();
    }

    @Override
    public String getContents() {
        return GLFW.glfwGetClipboardString(((Lwjgl3Graphics) Gdx.graphics).getWindow().getWindowHandle());
    }

    @Override
    public void setContents(String content) {
        GLFW.glfwSetClipboardString(((Lwjgl3Graphics) Gdx.graphics).getWindow().getWindowHandle(), content);
    }
}
