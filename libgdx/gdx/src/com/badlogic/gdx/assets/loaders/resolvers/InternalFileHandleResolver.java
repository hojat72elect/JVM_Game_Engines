package com.badlogic.gdx.assets.loaders.resolvers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

import org.jetbrains.annotations.NotNull;

public class InternalFileHandleResolver implements FileHandleResolver {
    @Override
    public FileHandle resolve(@NotNull String fileName) {
        return Gdx.files.internal(fileName);
    }
}
