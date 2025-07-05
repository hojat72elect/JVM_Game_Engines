package com.badlogic.gdx

/**
 * An [ApplicationListener]is called when the [Application] is created, resumed, rendering, paused or destroyed.
 * All methods are called in a thread that has the OpenGL context current. You can thus safely create and manipulate graphics resources.
 *
 * The [ApplicationListener] interface follows the standard Android activity life-cycle and is emulated on the desktop accordingly (just consider this as a lifecycle listener).
 */
interface ApplicationListener {

    /**
     * Called when the [Application] is first created.
     */
    fun create()

    /**
     * Called when the [Application] is resized. This can happen at any point during a non-paused state but will never happen before a call to [create].
     *
     * @param width  the new width in pixels.
     * @param height the new height in pixels.
     */
    fun resize(width: Int, height: Int)

    /**
     * Called when the [Application] should render (or re-render) itself.
     */
    fun render()

    /**
     * Called when the [Application] is paused, usually when it's not active or visible on-screen. An Application is also
     * paused before it is destroyed.
     */
    fun pause()

    /**
     * Called when the [Application] is resumed from a paused state, usually when it regains focus on screen.
     */
    fun resume()

    /**
     * Called when the [Application] is destroyed. Preceded by a call to [pause].
     */
    fun dispose()
}