package com.badlogic.gdx.utils

/**
 * Interface for disposable resources. Anything that  needs to be cleaned at some point  during its lifetime.
 */
interface Disposable {
    /**
     * Releases all resources of this object.
     */
    fun dispose()
}