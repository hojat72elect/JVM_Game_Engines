package com.badlogic.gdx


/**
 * A Preference instance is a hash map holding different values. It is stored alongside your application (SharedPreferences on
 * Android, LocalStorage on GWT, on the desktop a Java Preferences file in a ".prefs" directory will be created, and on iOS an
 * NSMutableDictionary will be written to the given file). CAUTION: On the desktop platform, all libGDX applications share the
 * same ".prefs" directory. To avoid collisions use specific names like "com.myname.game1.settings" instead of "settings".
 *
 * To persist changes made to a preferences instance [.flush] has to be invoked. With the exception of Android, changes
 * are cached in memory prior to flushing. On iOS changes are not synchronized between different preferences instances.
 *
 * Use [Application.getPreferences] to look up a specific preferences instance. Note that on several backends the
 * preferences name will be used as the filename, so make sure the name is valid for a filename.
 */
interface Preferences {
    fun putBoolean(key: String, value: Boolean): Preferences

    fun putInteger(key: String, value: Int): Preferences

    fun putLong(key: String, value: Long): Preferences

    fun putFloat(key: String, value: Float): Preferences

    fun putString(key: String, value: String): Preferences

    fun put(values: MutableMap<String, *>): Preferences

    fun getBoolean(key: String): Boolean

    fun getInteger(key: String): Int

    fun getLong(key: String): Long

    fun getFloat(key: String): Float

    fun getString(key: String): String

    fun getBoolean(key: String, defValue: Boolean): Boolean

    fun getInteger(key: String, defValue: Int): Int

    fun getLong(key: String, defValue: Long): Long

    fun getFloat(key: String, defValue: Float): Float

    fun getString(key: String, defValue: String): String

    /**
     * Returns a read only Map<String, Object> with all the key, objects of the preferences.
     */
    fun get(): MutableMap<String, *>

    fun contains(key: String): Boolean

    fun clear()

    fun remove(key: String)

    /**
     * Makes sure the preferences are persisted.
     */
    fun flush()
}
