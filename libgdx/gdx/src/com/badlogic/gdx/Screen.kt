package com.badlogic.gdx

import com.badlogic.gdx.utils.Disposable


/**
 * Represents one of many application screens, such as a main menu, a settings menu, the game screen and so on.
 * Note that [dispose] is not called automatically.
 * @see Game
 */
interface Screen : Disposable {
    /**
     * Called when this screen becomes the current screen for a [Game].
     */
    fun show()

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    fun render(delta: Float)

    /**
     * @see ApplicationListener.resize
     */
    fun resize(width: Int, height: Int)

    /**
     * @see ApplicationListener.pause
     */
    fun pause()

    /**
     * @see ApplicationListener.resume
     */
    fun resume()

    /**
     * Called when this screen is no longer the current screen for a [Game].
     */
    fun hide()

    /**
     * Called when this screen should release all resources.
     */
    override fun dispose()
}
