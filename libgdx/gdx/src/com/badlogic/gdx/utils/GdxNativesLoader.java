package com.badlogic.gdx.utils;

public class GdxNativesLoader {
    static public boolean disableNativesLoading = false;

    static private boolean nativesLoaded;

    /**
     * Loads the libgdx native libraries if they have not already been loaded.
     */
    static public synchronized void load() {
        if (nativesLoaded) return;

        if (disableNativesLoading) return;

        new SharedLibraryLoader().load("gdx");
        nativesLoaded = true;
    }
}
