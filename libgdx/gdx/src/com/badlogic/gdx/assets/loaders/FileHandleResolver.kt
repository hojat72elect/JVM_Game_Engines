package com.badlogic.gdx.assets.loaders

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.files.FileHandle

/**
 * This contract is for any classes that can map a file name (as a String) to a [FileHandle].
 * Used to allow the [AssetManager]s to load resources from anywhere or implement caching strategies.
 *
 * If it doesn't manage to get a [FileHandle] out of the input String, it will return Null.
 */
interface FileHandleResolver {
    fun resolve(fileName: String): FileHandle?
}