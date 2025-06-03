package com.badlogic.gdx;

import java.util.Map;

/**
 * <p>
 * A Preference instance is a hash map holding different values. It is stored alongside your application (SharedPreferences on
 * Android, LocalStorage on GWT, on the desktop a Java Preferences file in a ".prefs" directory will be created, and on iOS an
 * NSMutableDictionary will be written to the given file). CAUTION: On the desktop platform, all libGDX applications share the
 * same ".prefs" directory. To avoid collisions use specific names like "com.myname.game1.settings" instead of "settings".
 * </p>
 *
 * <p>
 * To persist changes made to a preferences instance {@link #flush()} has to be invoked. With the exception of Android, changes
 * are cached in memory prior to flushing. On iOS changes are not synchronized between different preferences instances.
 * </p>
 *
 * <p>
 * Use {@link Application#getPreferences(String)} to look up a specific preferences instance. Note that on several backends the
 * preferences name will be used as the filename, so make sure the name is valid for a filename.
 * </p>
 */
public interface Preferences {
    Preferences putBoolean(String key, boolean val);

    Preferences putInteger(String key, int val);

    Preferences putLong(String key, long val);

    Preferences putFloat(String key, float val);

    Preferences putString(String key, String val);

    Preferences put(Map<String, ?> vals);

    boolean getBoolean(String key);

    int getInteger(String key);

    long getLong(String key);

    float getFloat(String key);

    String getString(String key);

    boolean getBoolean(String key, boolean defValue);

    int getInteger(String key, int defValue);

    long getLong(String key, long defValue);

    float getFloat(String key, float defValue);

    String getString(String key, String defValue);

    /**
     * Returns a read only Map<String, Object> with all the key, objects of the preferences.
     */
    Map<String, ?> get();

    boolean contains(String key);

    void clear();

    void remove(String key);

    /**
     * Makes sure the preferences are persisted.
     */
    void flush();
}
