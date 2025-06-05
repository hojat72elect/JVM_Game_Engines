package com.badlogic.gdx

/**
 * Convenience implementation of [Screen]. Derive from this and only override what you need.
 */
class ScreenAdapter : Screen {
    override fun render(delta: Float) {
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun show() {
    }

    override fun hide() {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
    }
}
