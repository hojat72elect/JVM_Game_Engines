package com.badlogic.gdx;

/**
 * A LifecycleListener can be added to an {@link Application} via {@link Application#addLifecycleListener(LifecycleListener)}. It
 * will receive notification of pause, resume and dispose events. This is mainly meant to be used by extensions that need to
 * manage resources based on the life-cycle. Normal, application level development should rely on the {@link ApplicationListener}
 * interface.
 * </p>
 * <p>
 * The methods will be invoked on the rendering thread. The methods will be executed before the {@link ApplicationListener}
 * methods are executed.
 */
public interface LifecycleListener {
    /**
     * Called when the {@link Application} is about to pause
     */
    void pause();

    /**
     * Called when the Application is about to be resumed
     */
    void resume();

    /**
     * Called when the {@link Application} is about to be disposed
     */
    void dispose();
}
