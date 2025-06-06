package com.badlogic.gdx.backends.android;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.File;
import java.io.IOException;

public class DefaultAndroidFiles implements AndroidFiles {
    protected final String externalFilesPath;
    protected final String localpath;

    protected final AssetManager assets;
    private ZipResourceFile expansionFile = null;

    public DefaultAndroidFiles(AssetManager assets, ContextWrapper contextWrapper, boolean useExternalFiles) {
        this.assets = assets;

        String localPath = contextWrapper.getFilesDir().getAbsolutePath();
        this.localpath = localPath.endsWith("/") ? localPath : localPath + "/";
        if (useExternalFiles) {
            this.externalFilesPath = initExternalFilesPath(contextWrapper);
        } else {
            this.externalFilesPath = null;
        }
    }

    protected String initExternalFilesPath(ContextWrapper contextWrapper) {
        File externalFilesDir = contextWrapper.getExternalFilesDir(null);
        if (externalFilesDir != null) {
            String externalFilesPath = externalFilesDir.getAbsolutePath();
            return externalFilesPath.endsWith("/") ? externalFilesPath : externalFilesPath + "/";
        } else {
            return null;
        }
    }

    @Override
    public FileHandle getFileHandle(String path, FileType type) {
        FileHandle handle = new AndroidFileHandle(type == FileType.Internal ? assets : null, path, type);
        if (expansionFile != null && type == FileType.Internal) handle = getZipFileHandleIfExists(handle, path);
        return handle;
    }

    private FileHandle getZipFileHandleIfExists(FileHandle handle, String path) {
        try {
            assets.open(path).close(); // Check if file exists.
            return handle;
        } catch (Exception ex) {
            // try APK expansion instead
            FileHandle zipHandle = new AndroidZipFileHandle(path);
            if (!zipHandle.isDirectory())
                return zipHandle;
            else if (zipHandle.exists()) return zipHandle;
        }
        return handle;
    }

    @Override
    public FileHandle classpath(String path) {
        return new AndroidFileHandle(null, path, FileType.Classpath);
    }

    @Override
    public FileHandle internal(String path) {
        FileHandle handle = new AndroidFileHandle(assets, path, FileType.Internal);
        if (expansionFile != null) handle = getZipFileHandleIfExists(handle, path);
        return handle;
    }

    @Override
    public FileHandle external(String path) {
        return new AndroidFileHandle(null, path, FileType.External);
    }

    @Override
    public FileHandle absolute(String path) {
        return new AndroidFileHandle(null, path, FileType.Absolute);
    }

    @Override
    public FileHandle local(String path) {
        return new AndroidFileHandle(null, path, FileType.Local);
    }

    @Override
    public String getExternalStoragePath() {
        return externalFilesPath;
    }

    @Override
    public boolean isExternalStorageAvailable() {
        return externalFilesPath != null;
    }

    @Override
    public String getLocalStoragePath() {
        return localpath;
    }

    @Override
    public boolean isLocalStorageAvailable() {
        return true;
    }

    @Override
    public boolean setAPKExpansion(int mainVersion, int patchVersion) {
        try {
            Context context;
            if (Gdx.app instanceof Activity) {
                context = ((Activity) Gdx.app).getBaseContext();
            } else if (Gdx.app instanceof Fragment) {
                context = ((Fragment) Gdx.app).getActivity().getBaseContext();
            } else {
                throw new GdxRuntimeException("APK expansion not supported for application type");
            }
            expansionFile = APKExpansionSupport.getAPKExpansionZipFile(context, mainVersion, patchVersion);
        } catch (IOException ex) {
            throw new GdxRuntimeException(
                    "APK expansion main version " + mainVersion + " or patch version " + patchVersion + " couldn't be opened!");
        }
        return expansionFile != null;
    }

    @Override
    public ZipResourceFile getExpansionFile() {
        return expansionFile;
    }
}
