package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

/**
 * {@link AssetLoader} for {@link Music} instances. The Music instance is loaded synchronously.
 */
public class MusicLoader extends AsynchronousAssetLoader<Music, MusicLoader.MusicParameter> {

    private Music music;

    public MusicLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    /**
     * Returns the {@link Music} instance currently loaded by this {@link MusicLoader}.
     *
     * @return the currently loaded {@link Music}, otherwise {@code null} if no {@link Music} has been loaded yet.
     */
    protected Music getLoadedMusic() {
        return music;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, MusicParameter parameter) {
        music = Gdx.audio.newMusic(file);
    }

    @Override
    public Music loadSync(AssetManager manager, String fileName, FileHandle file, MusicParameter parameter) {
        Music music = this.music;
        this.music = null;
        return music;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, MusicParameter parameter) {
        return null;
    }

    static public class MusicParameter extends AssetLoaderParameters<Music> {
    }

}
