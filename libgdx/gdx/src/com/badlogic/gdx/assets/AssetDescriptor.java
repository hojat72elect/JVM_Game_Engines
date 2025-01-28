package com.badlogic.gdx.assets;

import com.badlogic.gdx.files.FileHandle;

/**
 * Describes an asset to be loaded by its filename, type and {@link AssetLoaderParameters}. Instances of this are used in
 * {@link AssetLoadingTask} to load the actual asset.
 */
public class AssetDescriptor<T> {
    public final String fileName;
    public final Class<T> type;
    public final AssetLoaderParameters params;
    /**
     * The resolved file. May be null if the fileName has not been resolved yet.
     */
    public FileHandle file;

    public AssetDescriptor(String fileName, Class<T> assetType) {
        this(fileName, assetType, null);
    }

    /**
     * Creates an AssetDescriptor with an already resolved name.
     */
    public AssetDescriptor(FileHandle file, Class<T> assetType) {
        this(file, assetType, null);
    }

    public AssetDescriptor(String fileName, Class<T> assetType, AssetLoaderParameters<T> params) {
        this.fileName = fileName;
        this.type = assetType;
        this.params = params;
    }

    /**
     * Creates an AssetDescriptor with an already resolved name.
     */
    public AssetDescriptor(FileHandle file, Class<T> assetType, AssetLoaderParameters<T> params) {
        this.fileName = file.path();
        this.file = file;
        this.type = assetType;
        this.params = params;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(fileName);
        sb.append(", ");
        sb.append(type.getName());
        return sb.toString();
    }
}
