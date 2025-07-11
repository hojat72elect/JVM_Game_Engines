package com.badlogic.gdx.backends.android;

import android.content.res.AssetFileDescriptor;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.ZipResourceFile.ZipEntryRO;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

public class AndroidZipFileHandle extends AndroidFileHandle {
    private boolean hasAssetFd;
    private long fdLength;
    private ZipResourceFile expansionFile;
    private String path;

    public AndroidZipFileHandle(String fileName) {
        super(null, fileName, FileType.Internal);
        initialize();
    }

    public AndroidZipFileHandle(File file, FileType type) {
        super(null, file, type);
        initialize();
    }

    private void initialize() {
        path = file.getPath().replace('\\', '/');
        expansionFile = ((AndroidFiles) Gdx.files).getExpansionFile();
        AssetFileDescriptor assetFd = expansionFile.getAssetFileDescriptor(getPath());
        if (assetFd != null) {
            hasAssetFd = true;
            fdLength = assetFd.getLength();
            try {
                assetFd.close();
            } catch (IOException ignored) {
            }
        } else {
            hasAssetFd = false;
        }

        // needed for listing entries and exists() of directories
        if (isDirectory()) path += "/";
    }

    @Override
    public AssetFileDescriptor getAssetFileDescriptor() throws IOException {
        return expansionFile.getAssetFileDescriptor(getPath());
    }

    private String getPath() {
        return path;
    }

    @Override
    public InputStream read() {
        InputStream input;

        try {
            input = expansionFile.getInputStream(getPath());
        } catch (IOException ex) {
            throw new GdxRuntimeException("Error reading file: " + file + " (ZipResourceFile)", ex);
        }
        return input;
    }

    @Override
    public FileHandle child(String name) {
        if (file.getPath().isEmpty()) return new AndroidZipFileHandle(new File(name), type);
        return new AndroidZipFileHandle(new File(file, name), type);
    }

    @Override
    public FileHandle sibling(String name) {
        if (file.getPath().isEmpty()) throw new GdxRuntimeException("Cannot get the sibling of the root.");
        return Gdx.files.getFileHandle(new File(file.getParent(), name).getPath(), type); // this way we can find the sibling even
        // if it's not inside the obb
    }

    @Override
    public FileHandle parent() {
        File parent = file.getParentFile();
        if (parent == null) parent = new File("");
        return new AndroidZipFileHandle(parent.getPath());
    }

    @Override
    public FileHandle[] list() {
        ZipEntryRO[] zipEntries = expansionFile.getEntriesAt(getPath());
        FileHandle[] handles = new FileHandle[zipEntries.length - 1];
        int count = 0;
        for (ZipEntryRO zipEntry : zipEntries) {
            if (zipEntry.mFileName.length() == getPath().length()) // Don't include the directory itself
                continue;
            handles[count++] = new AndroidZipFileHandle(zipEntry.mFileName);
        }
        return handles;
    }

    @Override
    public FileHandle[] list(FileFilter filter) {
        ZipEntryRO[] zipEntries = expansionFile.getEntriesAt(getPath());
        FileHandle[] handles = new FileHandle[zipEntries.length - 1];
        int count = 0;
        for (ZipEntryRO zipEntry : zipEntries) {
            if (zipEntry.mFileName.length() == getPath().length()) // Don't include the directory itself
                continue;
            FileHandle child = new AndroidZipFileHandle(zipEntry.mFileName);
            if (!filter.accept(child.file())) continue;
            handles[count] = child;
            count++;
        }
        if (count < handles.length) {
            FileHandle[] newHandles = new FileHandle[count];
            System.arraycopy(handles, 0, newHandles, 0, count);
            handles = newHandles;
        }
        return handles;
    }

    @Override
    public FileHandle[] list(FilenameFilter filter) {
        ZipEntryRO[] zipEntries = expansionFile.getEntriesAt(getPath());
        FileHandle[] handles = new FileHandle[zipEntries.length - 1];
        int count = 0;
        for (ZipEntryRO zipEntry : zipEntries) {
            if (zipEntry.mFileName.length() == getPath().length()) // Don't include the directory itself
                continue;
            String path = zipEntry.mFileName;
            if (!filter.accept(file, path)) continue;
            handles[count] = new AndroidZipFileHandle(path);
            count++;
        }
        if (count < handles.length) {
            FileHandle[] newHandles = new FileHandle[count];
            System.arraycopy(handles, 0, newHandles, 0, count);
            handles = newHandles;
        }
        return handles;
    }

    @Override
    public FileHandle[] list(String suffix) {
        ZipEntryRO[] zipEntries = expansionFile.getEntriesAt(getPath());
        FileHandle[] handles = new FileHandle[zipEntries.length - 1];
        int count = 0;
        for (ZipEntryRO zipEntry : zipEntries) {
            if (zipEntry.mFileName.length() == getPath().length()) // Don't include the directory itself
                continue;
            String path = zipEntry.mFileName;
            if (!path.endsWith(suffix)) continue;
            handles[count] = new AndroidZipFileHandle(path);
            count++;
        }
        if (count < handles.length) {
            FileHandle[] newHandles = new FileHandle[count];
            System.arraycopy(handles, 0, newHandles, 0, count);
            handles = newHandles;
        }
        return handles;
    }

    @Override
    public boolean isDirectory() {
        return !hasAssetFd;
    }

    @Override
    public long length() {
        return hasAssetFd ? fdLength : 0;
    }

    @Override
    public boolean exists() {
        return hasAssetFd || expansionFile.getEntriesAt(getPath()).length != 0;
    }
}
