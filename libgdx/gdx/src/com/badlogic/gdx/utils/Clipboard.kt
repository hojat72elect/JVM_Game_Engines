package com.badlogic.gdx.utils

/**
 * A very simple clipboard interface for text content.
 * This is currently not supported on the web.
 */
interface Clipboard {

    /**
     * Check if the clipboard has contents. It's recommended to use this one over [getContents] if you only want to check
     * if there's something on the clipboard. For instance, calling [getContents] on iOS shows a privacy notification since iOS 14,
     * while hasContents() does not.
     *
     * @return true, if the clipboard has contents.
     */
    fun hasContents(): Boolean

    /**
     * Gets the current content of the clipboard if it contains text; otherwise, returns null.
     *
     * @return the clipboard content or null.
     */
    fun getContents(): String?

    /**
     * Sets the content of the system clipboard.
     * Setting the clipboard is not synchronous so you can't rely on getting same content just after setting it.
     *
     * @param content the content
     */
    fun setContents(content: String)
}