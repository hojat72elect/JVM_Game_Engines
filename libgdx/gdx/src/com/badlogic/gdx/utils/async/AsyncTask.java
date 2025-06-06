package com.badlogic.gdx.utils.async;

/**
 * Task to be submitted to an {@link AsyncExecutor}, returning a result of type T.
 */
public interface AsyncTask<T> {
    T call() throws Exception;
}
