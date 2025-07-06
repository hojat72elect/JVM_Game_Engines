package com.badlogic.gdx.assets

/**
 * A contract for any class that wants to listen for and handle (any kind of) errors related to assets.
 */
interface AssetErrorListener {
    fun error(asset: AssetDescriptor<*>, throwable: Throwable)
}