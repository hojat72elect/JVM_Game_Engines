package com.badlogic.gdx.assets;

public class AssetLoaderParameters<T> {

    public LoadedCallback loadedCallback;

    /**
     * Callback interface that will be invoked when the {@link AssetManager} loaded an asset.
     */
    public interface LoadedCallback {
        public void finishedLoading(AssetManager assetManager, String fileName, Class type);
    }
}
