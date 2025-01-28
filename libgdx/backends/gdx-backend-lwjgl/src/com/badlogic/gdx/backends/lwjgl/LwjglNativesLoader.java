package com.badlogic.gdx.backends.lwjgl;

import com.badlogic.gdx.utils.GdxNativesLoader;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.SharedLibraryLoader;

import java.io.File;
import java.lang.reflect.Method;

import static com.badlogic.gdx.utils.SharedLibraryLoader.bitness;
import static com.badlogic.gdx.utils.SharedLibraryLoader.os;

public final class LwjglNativesLoader {
    static public boolean load = true;

    static {
        System.setProperty("org.lwjgl.input.Mouse.allowNegativeMouseCoords", "true");

        // Don't extract natives if using JWS.
        try {
            Method method = Class.forName("javax.jnlp.ServiceManager").getDeclaredMethod("lookup", new Class[]{String.class});
            method.invoke(null, "javax.jnlp.PersistenceService");
            load = false;
        } catch (Throwable ex) {
            load = true;
        }
    }

    /**
     * Extracts the LWJGL native libraries from the classpath and sets the "org.lwjgl.librarypath" system property.
     */
    static public void load() {
        GdxNativesLoader.load();
        if (GdxNativesLoader.disableNativesLoading) return;
        if (!load) return;

        SharedLibraryLoader loader = new SharedLibraryLoader();
        File nativesDir = null;
        try {
            String lwjglLib = null;
            String openalLib = null;
            switch (os) {
                case Windows:
                    switch (bitness) {
                        case _32:
                            lwjglLib = "lwjgl.dll";
                            openalLib = "OpenAL32.dll";
                            break;
                        case _64:
                            lwjglLib = "lwjgl64.dll";
                            openalLib = "OpenAL64.dll";
                            break;
                    }
                    break;
                case MacOsX:
                    lwjglLib = "liblwjgl.dylib";
                    openalLib = "openal.dylib";
                    break;
                case Linux:
                    switch (bitness) {
                        case _32:
                            lwjglLib = "liblwjgl.so";
                            openalLib = "libopenal.so";
                            break;
                        case _64:
                            lwjglLib = "liblwjgl64.so";
                            openalLib = "libopenal64.so";
                            break;
                    }
                    break;
            }
            if (lwjglLib == null) throw new GdxRuntimeException("Unknown LWJGL platform: " + os + ", " + bitness);
            nativesDir = loader.extractFile(lwjglLib, null).getParentFile();
            if (!LwjglApplicationConfiguration.disableAudio) loader.extractFileTo(openalLib, nativesDir);
        } catch (Throwable ex) {
            throw new GdxRuntimeException("Unable to extract LWJGL natives.", ex);
        }
        System.setProperty("org.lwjgl.librarypath", nativesDir.getAbsolutePath());
        load = false;
    }
}
