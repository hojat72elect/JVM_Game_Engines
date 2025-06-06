package com.badlogic.gdx.setup;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class SetupPreferences {

    private static final File[] files = new File[]{
            // $CONFIG_HOME/gdxsetup/config
            new File(new File(new File(findConfigHomePath()), "gdxsetup"), "config"),
            // $HOME/.gdxsetup
            new File(new File(System.getProperty("user.home")), ".gdxsetup")};
    private final Properties properties = new Properties();
    private final File file = findFile();

    public SetupPreferences() {
        if (!file.exists()) return;
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            properties.loadFromXML(in);
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (in != null) try {
                in.close();
            } catch (IOException e) {
            }
        }
    }

    private static final String findConfigHomePath() {
        Map<String, String> env = System.getenv();
        if (env.containsKey("XDG_CONFIG_HOME")) return env.get("XDG_CONFIG_HOME");
        // TODO add windows and mac config home paths
        return System.getProperty("user.home") + "/.config";
    }

    private static final File findFile() {
        for (File i : files) {
            if (i.exists()) return i;
        }
        return files[files.length - 1]; // default to $HOME/.gdxsetup
    }

    public SetupPreferences putBoolean(String key, boolean val) {
        properties.put(key, Boolean.toString(val));
        return this;
    }

    public SetupPreferences putInteger(String key, int val) {
        properties.put(key, Integer.toString(val));
        return this;
    }

    public SetupPreferences putLong(String key, long val) {
        properties.put(key, Long.toString(val));
        return this;
    }

    public SetupPreferences putFloat(String key, float val) {
        properties.put(key, Float.toString(val));
        return this;
    }

    public SetupPreferences putString(String key, String val) {
        properties.put(key, val);
        return this;
    }

    public SetupPreferences put(Map<String, ?> vals) {
        for (Entry<String, ?> val : vals.entrySet()) {
            if (val.getValue() instanceof Boolean) putBoolean(val.getKey(), (Boolean) val.getValue());
            if (val.getValue() instanceof Integer) putInteger(val.getKey(), (Integer) val.getValue());
            if (val.getValue() instanceof Long) putLong(val.getKey(), (Long) val.getValue());
            if (val.getValue() instanceof String) putString(val.getKey(), (String) val.getValue());
            if (val.getValue() instanceof Float) putFloat(val.getKey(), (Float) val.getValue());
        }
        return this;
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public int getInteger(String key) {
        return getInteger(key, 0);
    }

    public long getLong(String key) {
        return getLong(key, 0);
    }

    public float getFloat(String key) {
        return getFloat(key, 0);
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public boolean getBoolean(String key, boolean defValue) {
        return Boolean.parseBoolean(properties.getProperty(key, Boolean.toString(defValue)));
    }

    public int getInteger(String key, int defValue) {
        return Integer.parseInt(properties.getProperty(key, Integer.toString(defValue)));
    }

    public long getLong(String key, long defValue) {
        return Long.parseLong(properties.getProperty(key, Long.toString(defValue)));
    }

    public float getFloat(String key, float defValue) {
        return Float.parseFloat(properties.getProperty(key, Float.toString(defValue)));
    }

    public String getString(String key, String defValue) {
        return properties.getProperty(key, defValue);
    }

    public Map<String, ?> get() {
        Map<String, Object> map = new HashMap<String, Object>();
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

    public boolean contains(String key) {
        return properties.containsKey(key);
    }

    public void clear() {
        properties.clear();
    }

    public void flush() {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            properties.storeToXML(out, null);
        } catch (Exception ex) {
            throw new RuntimeException("Error writing preferences: " + file, ex);
        } finally {
            if (out != null) try {
                out.close();
            } catch (IOException e) {
            }
        }
    }

    public void remove(String key) {
        properties.remove(key);
    }
}
