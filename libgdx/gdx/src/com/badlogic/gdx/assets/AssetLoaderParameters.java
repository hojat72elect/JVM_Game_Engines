package com.badlogic.gdx.assets;

public class AssetLoaderParameters {

    public LoadedCallback loadedCallback;

    /**
     * Callback interface that will be invoked when the {@link AssetManager} loaded an asset.
     */
    public interface LoadedCallback {
        void finishedLoading(AssetManager assetManager, String fileName, Class type);
    }
}
