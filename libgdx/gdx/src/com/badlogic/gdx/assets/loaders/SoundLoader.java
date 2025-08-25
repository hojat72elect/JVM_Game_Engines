package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

/**
 * {@link AssetLoader} to load {@link Sound} instances.
 */
public class SoundLoader extends AsynchronousAssetLoader<Sound, SoundLoader.SoundParameter> {

    private Sound sound;

    public SoundLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, SoundParameter parameter) {
        sound = Gdx.audio.newSound(file);
    }

    @Override
    public Sound loadSync(AssetManager manager, String fileName, FileHandle file, SoundParameter parameter) {
        Sound sound = this.sound;
        this.sound = null;
        return sound;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, SoundParameter parameter) {
        return null;
    }

    static public class SoundParameter extends AssetLoaderParameters {
    }
}
