package com.badlogic.gdx.backends.android;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.badlogic.gdx.Preferences;

import java.util.Map;
import java.util.Map.Entry;

public class AndroidPreferences implements Preferences {
    SharedPreferences sharedPrefs;
    Editor editor;

    public AndroidPreferences(SharedPreferences preferences) {
        this.sharedPrefs = preferences;
    }

    @Override
    public Preferences putBoolean(String key, boolean val) {
        edit();
        editor.putBoolean(key, val);
        return this;
    }

    @Override
    public Preferences putInteger(String key, int val) {
        edit();
        editor.putInt(key, val);
        return this;
    }

    @Override
    public Preferences putLong(String key, long val) {
        edit();
        editor.putLong(key, val);
        return this;
    }

    @Override
    public Preferences putFloat(String key, float val) {
        edit();
        editor.putFloat(key, val);
        return this;
    }

    @Override
    public Preferences putString(String key, String val) {
        edit();
        editor.putString(key, val);
        return this;
    }

    @Override
    public Preferences put(Map<String, ?> vals) {
        edit();
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
    public boolean getBoolean(String key) {
        return sharedPrefs.getBoolean(key, false);
    }

    @Override
    public int getInteger(String key) {
        return sharedPrefs.getInt(key, 0);
    }

    @Override
    public long getLong(String key) {
        return sharedPrefs.getLong(key, 0);
    }

    @Override
    public float getFloat(String key) {
        return sharedPrefs.getFloat(key, 0);
    }

    @Override
    public String getString(String key) {
        return sharedPrefs.getString(key, "");
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return sharedPrefs.getBoolean(key, defValue);
    }

    @Override
    public int getInteger(String key, int defValue) {
        return sharedPrefs.getInt(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return sharedPrefs.getLong(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return sharedPrefs.getFloat(key, defValue);
    }

    @Override
    public String getString(String key, String defValue) {
        return sharedPrefs.getString(key, defValue);
    }

    @Override
    public Map<String, ?> get() {
        return sharedPrefs.getAll();
    }

    @Override
    public boolean contains(String key) {
        return sharedPrefs.contains(key);
    }

    @Override
    public void clear() {
        edit();
        editor.clear();
    }

    @Override
    public void flush() {
        if (editor != null) {
            editor.apply();
            editor = null;
        }
    }

    @Override
    public void remove(String key) {
        edit();
        editor.remove(key);
    }

    private void edit() {
        if (editor == null) {
            editor = sharedPrefs.edit();
        }
    }
}
