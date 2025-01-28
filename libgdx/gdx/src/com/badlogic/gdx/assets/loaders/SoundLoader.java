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

    /**
     * Returns the {@link Sound} instance currently loaded by this {@link SoundLoader}.
     *
     * @return the currently loaded {@link Sound}, otherwise {@code null} if no {@link Sound} has been loaded yet.
     */
    protected Sound getLoadedSound() {
        return sound;
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

    static public class SoundParameter extends AssetLoaderParameters<Sound> {
    }

}
