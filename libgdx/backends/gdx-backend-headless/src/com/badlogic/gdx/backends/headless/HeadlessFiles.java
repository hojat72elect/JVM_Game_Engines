package com.badlogic.gdx.backends.headless;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class HeadlessFiles implements Files {
    static public final String externalPath = System.getProperty("user.home") + File.separator;
    static public final String localPath = new File("").getAbsolutePath() + File.separator;

    @Override
    public FileHandle getFileHandle(@NotNull String fileName, @NotNull FileType type) {
        return new HeadlessFileHandle(fileName, type);
    }

    @Override
    public FileHandle classpath(@NotNull String path) {
        return new HeadlessFileHandle(path, FileType.Classpath);
    }

    @Override
    public FileHandle internal(@NotNull String path) {
        return new HeadlessFileHandle(path, FileType.Internal);
    }

    @Override
    public FileHandle external(@NotNull String path) {
        return new HeadlessFileHandle(path, FileType.External);
    }

    @Override
    public FileHandle absolute(@NotNull String path) {
        return new HeadlessFileHandle(path, FileType.Absolute);
    }

    @Override
    public FileHandle local(@NotNull String path) {
        return new HeadlessFileHandle(path, FileType.Local);
    }

    @Override
    public String getExternalStoragePath() {
        return externalPath;
    }

    @Override
    public boolean isExternalStorageAvailable() {
        return true;
    }

    @Override
    public String getLocalStoragePath() {
        return localPath;
    }

    @Override
    public boolean isLocalStorageAvailable() {
        return true;
    }
}
