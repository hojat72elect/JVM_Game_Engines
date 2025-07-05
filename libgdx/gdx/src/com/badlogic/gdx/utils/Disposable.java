package com.badlogic.gdx.utils;

/**
 * Interface for disposable resources. Anything that  needs to be cleaned at some point  during its lifetime.
 */
public interface Disposable {
    /**
     * Releases all resources of this object.
     */
    void dispose();
}
