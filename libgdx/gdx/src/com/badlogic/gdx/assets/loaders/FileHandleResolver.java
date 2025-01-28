package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;

/**
 * Interface for classes the can map a file name to a {@link FileHandle}. Used to allow the {@link AssetManager} to load
 * resources from anywhere or implement caching strategies.
 */
public interface FileHandleResolver {
    public FileHandle resolve(String fileName);
}
