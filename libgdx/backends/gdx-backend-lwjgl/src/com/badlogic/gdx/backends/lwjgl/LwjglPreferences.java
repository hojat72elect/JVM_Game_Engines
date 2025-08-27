package com.badlogic.gdx.backends.lwjgl;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class LwjglPreferences implements Preferences {
    private final Properties properties = new Properties();
    private final FileHandle file;

    public LwjglPreferences(String name, String directory) {
        this(new LwjglFileHandle(new File(directory, name), FileType.External));
    }

    public LwjglPreferences(FileHandle file) {

        this.file = file;
        if (!file.exists()) return;
        InputStream in = null;
        try {
            in = new BufferedInputStream(file.read());
            properties.loadFromXML(in);
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            StreamUtils.closeQuietly(in);
        }
    }

    @NotNull
    @Override
    public Preferences putBoolean(@NotNull String key, boolean val) {
        properties.put(key, Boolean.toString(val));
        return this;
    }

    @NotNull
    @Override
    public Preferences putInteger(@NotNull String key, int val) {
        properties.put(key, Integer.toString(val));
        return this;
    }

    @NotNull
    @Override
    public Preferences putLong(@NotNull String key, long val) {
        properties.put(key, Long.toString(val));
        return this;
    }

    @NotNull
    @Override
    public Preferences putFloat(@NotNull String key, float val) {
        properties.put(key, Float.toString(val));
        return this;
    }

    @NotNull
    @Override
    public Preferences putString(@NotNull String key, @NotNull String val) {
        properties.put(key, val);
        return this;
    }

    @NotNull
    @Override
    public Preferences put(Map<String, ?> vals) {
        for (Entry<String, ?> val : vals.entrySet()) {
            if (val.getValue() instanceof Boolean) putBoolean(val.getKey(), (Boolean) val.getValue());
            if (val.getValue() instanceof Integer) putInteger(val.getKey(), (Integer) val.getValue());
            if (val.getValue() instanceof Long) putLong(val.getKey(), (Long) val.getValue());
            if (val.getValue() instanceof String) putString(val.getKey(), (String) val.getValue());
            if (val.getValue() instanceof Float) putFloat(val.getKey(), (Float) val.getValue());
        }
        return this;
    }

    @Override
    public boolean getBoolean(@NotNull String key) {
        return getBoolean(key, false);
    }

    @Override
    public int getInteger(@NotNull String key) {
        return getInteger(key, 0);
    }

    @Override
    public long getLong(@NotNull String key) {
        return getLong(key, 0);
    }

    @Override
    public float getFloat(@NotNull String key) {
        return getFloat(key, 0);
    }

    @NotNull
    @Override
    public String getString(@NotNull String key) {
        return getString(key, "");
    }

    @Override
    public boolean getBoolean(@NotNull String key, boolean defValue) {
        return Boolean.parseBoolean(properties.getProperty(key, Boolean.toString(defValue)));
    }

    @Override
    public int getInteger(@NotNull String key, int defValue) {
        return Integer.parseInt(properties.getProperty(key, Integer.toString(defValue)));
    }

    @Override
    public long getLong(@NotNull String key, long defValue) {
        return Long.parseLong(properties.getProperty(key, Long.toString(defValue)));
    }

    @Override
    public float getFloat(@NotNull String key, float defValue) {
        return Float.parseFloat(properties.getProperty(key, Float.toString(defValue)));
    }

    @NotNull
    @Override
    public String getString(@NotNull String key, @NotNull String defValue) {
        return properties.getProperty(key, defValue);
    }

    @NotNull
    @Override
    public Map<String, ?> get() {
        Map<String, Object> map = new HashMap<>();
        for (Entry<Object, Object> val : properties.entrySet()) {
            if (val.getValue() instanceof Boolean)
                map.put((String) val.getKey(), Boolean.parseBoolean((String) val.getValue()));
            if (val.getValue() instanceof Integer)
                map.put((String) val.getKey(), Integer.parseInt((String) val.getValue()));
            if (val.getValue() instanceof Long)
                map.put((String) val.getKey(), Long.parseLong((String) val.getValue()));
            if (val.getValue() instanceof String) map.put((String) val.getKey(), val.getValue());
            if (val.getValue() instanceof Float)
                map.put((String) val.getKey(), Float.parseFloat((String) val.getValue()));
        }

        return map;
    }

    @Override
    public boolean contains(@NotNull String key) {
        return properties.containsKey(key);
    }

    @Override
    public void clear() {
        properties.clear();
    }

    @Override
    public void flush() {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(file.write(false));
            properties.storeToXML(out, null);
        } catch (Exception ex) {
            throw new GdxRuntimeException("Error writing preferences: " + file, ex);
        } finally {
            StreamUtils.closeQuietly(out);
        }
    }

    @Override
    public void remove(@NotNull String key) {
        properties.remove(key);
    }
}
