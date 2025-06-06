package com.badlogic.gdx.utils;

/**
 * A very simple clipboard interface for text content.
 */
public interface Clipboard {
    /**
     * Check if the clipboard has contents. Recommended to use over getContents() for privacy reasons, if you only want to check
     * if there's something on the clipboard. For instance, calling getContents() on iOS shows a privacy notification since iOS 14,
     * while hasContents() does not.
     *
     * @return true, if the clipboard has contents
     */
    boolean hasContents();

    /**
     * gets the current content of the clipboard if it contains text for WebGL app, getting the system clipboard is currently not
     * supported. It works only inside the app
     *
     * @return the clipboard content or null
     */
    String getContents();

    /**
     * Sets the content of the system clipboard. for WebGL app, clipboard content might not be set if user denied permission,
     * setting clipboard is not synchronous so you can't rely on getting same content just after setting it
     *
     * @param content the content
     */
    void setContents(String content);
}
