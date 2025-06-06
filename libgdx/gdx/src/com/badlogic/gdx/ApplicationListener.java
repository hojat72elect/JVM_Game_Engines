package com.badlogic.gdx;

/**
 * <p>
 * An <code>ApplicationListener</code> is called when the {@link Application} is created, resumed, rendering, paused or destroyed.
 * All methods are called in a thread that has the OpenGL context current. You can thus safely create and manipulate graphics
 * resources.
 * </p>
 *
 * <p>
 * The <code>ApplicationListener</code> interface follows the standard Android activity life-cycle and is emulated on the desktop
 * accordingly.
 * </p>
 */
public interface ApplicationListener {
    /**
     * Called when the {@link Application} is first created.
     */
    void create();

    /**
     * Called when the {@link Application} is resized. This can happen at any point during a non-paused state but will never
     * happen before a call to {@link #create()}.
     *
     * @param width  the new width in pixels
     * @param height the new height in pixels
     */
    void resize(int width, int height);

    /**
     * Called when the {@link Application} should render itself.
     */
    void render();

    /**
     * Called when the {@link Application} is paused, usually when it's not active or visible on-screen. An Application is also
     * paused before it is destroyed.
     */
    void pause();

    /**
     * Called when the {@link Application} is resumed from a paused state, usually when it regains focus.
     */
    void resume();

    /**
     * Called when the {@link Application} is destroyed. Preceded by a call to {@link #pause()}.
     */
    void dispose();
}
