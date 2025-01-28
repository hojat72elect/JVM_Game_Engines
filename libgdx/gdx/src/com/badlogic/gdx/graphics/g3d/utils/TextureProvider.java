package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;

/**
 * Used by {@link Model} to load textures from {@link ModelData}.
 *
 * 
 */
public interface TextureProvider {
    public Texture load(String fileName);

    public static class FileTextureProvider implements TextureProvider {
        private Texture.TextureFilter minFilter, magFilter;
        private Texture.TextureWrap uWrap, vWrap;
        private boolean useMipMaps;

        public FileTextureProvider() {
            minFilter = magFilter = Texture.TextureFilter.Linear;
            uWrap = vWrap = Texture.TextureWrap.Repeat;
            useMipMaps = false;
        }

        public FileTextureProvider(Texture.TextureFilter minFilter, Texture.TextureFilter magFilter, Texture.TextureWrap uWrap,
                                   Texture.TextureWrap vWrap, boolean useMipMaps) {
            this.minFilter = minFilter;
            this.magFilter = magFilter;
            this.uWrap = uWrap;
            this.vWrap = vWrap;
            this.useMipMaps = useMipMaps;
        }

        @Override
        public Texture load(String fileName) {
            Texture result = new Texture(Gdx.files.internal(fileName), useMipMaps);
            result.setFilter(minFilter, magFilter);
            result.setWrap(uWrap, vWrap);
            return result;
        }
    }

    public static class AssetTextureProvider implements TextureProvider {
        public final AssetManager assetManager;

        public AssetTextureProvider(final AssetManager assetManager) {
            this.assetManager = assetManager;
        }

        @Override
        public Texture load(String fileName) {
            return assetManager.get(fileName, Texture.class);
        }
    }
}
