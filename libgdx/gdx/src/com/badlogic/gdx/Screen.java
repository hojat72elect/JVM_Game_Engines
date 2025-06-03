package com.badlogic.gdx;

import com.badlogic.gdx.utils.Disposable;

/**
 * <p>
 * Represents one of many application screens, such as a main menu, a settings menu, the game screen and so on.
 * </p>
 * <p>
 * Note that {@link #dispose()} is not called automatically.
 * </p>
 *
 * @see Game
 */
public interface Screen extends Disposable {

    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    void show();

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    void render(float delta);

    /**
     * @see ApplicationListener#resize(int, int)
     */
    void resize(int width, int height);

    /**
     * @see ApplicationListener#pause()
     */
    void pause();

    /**
     * @see ApplicationListener#resume()
     */
    void resume();

    /**
     * Called when this screen is no longer the current screen for a {@link Game}.
     */
    void hide();

    /**
     * Called when this screen should release all resources.
     */
    void dispose();
}
