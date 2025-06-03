package com.badlogic.gdx;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Provides standard access to the filesystem, classpath, Android app storage (internal and external), and Android assets
 * directory.
 */
public interface Files {
    /**
     * Returns a handle representing a file or directory.
     *
     * @param type Determines how the path is resolved.
     * @throws GdxRuntimeException if the type is classpath or internal and the file does not exist.
     * @see FileType
     */
    FileHandle getFileHandle(String path, FileType type);

    /**
     * Convenience method that returns a {@link FileType#Classpath} file handle.
     */
    FileHandle classpath(String path);

    /**
     * Convenience method that returns a {@link FileType#Internal} file handle.
     */
    FileHandle internal(String path);

    /**
     * Convenience method that returns a {@link FileType#External} file handle.
     */
    FileHandle external(String path);

    /**
     * Convenience method that returns a {@link FileType#Absolute} file handle.
     */
    FileHandle absolute(String path);

    /**
     * Convenience method that returns a {@link FileType#Local} file handle.
     */
    FileHandle local(String path);

    /**
     * Returns the external storage path directory. This is the app external storage on Android and the home directory of the
     * current user on the desktop.
     */
    String getExternalStoragePath();

    /**
     * Returns true if the external storage is ready for file IO.
     */
    boolean isExternalStorageAvailable();

    /**
     * Returns the local storage path directory. This is the private files directory on Android and the directory of the jar on
     * the desktop.
     */
    String getLocalStoragePath();

    /**
     * Returns true if the local storage is ready for file IO.
     */
    boolean isLocalStorageAvailable();

    /**
     * Indicates how to resolve a path to a file.
     */
    enum FileType {
        /**
         * Path relative to the root of the classpath. Classpath files are always readonly. Note that classpath files are not
         * compatible with some functionality on Android, such as {@link Audio#newSound(FileHandle)} and
         * {@link Audio#newMusic(FileHandle)}.
         */
        Classpath,

        /**
         * Path relative to the asset directory on Android and to the application's root directory on the desktop. On the desktop,
         * if the file is not found, then the classpath is checked. This enables files to be found when using JWS or applets.
         * Internal files are always readonly.
         */
        Internal,

        /**
         * Path relative to the root of the app external storage on Android and to the home directory of the current user on the
         * desktop.
         */
        External,

        /**
         * Path that is a fully qualified, absolute filesystem path. To ensure portability across platforms use absolute files only
         * when absolutely (heh) necessary.
         */
        Absolute,

        /**
         * Path relative to the private files directory on Android and to the application's root directory on the desktop.
         */
        Local
    }
}
