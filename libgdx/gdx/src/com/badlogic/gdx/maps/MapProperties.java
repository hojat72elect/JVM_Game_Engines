package com.badlogic.gdx.maps;

import com.badlogic.gdx.utils.ObjectMap;

import java.util.Iterator;

/**
 * @brief Set of string indexed values representing map elements' properties, allowing to retrieve, modify and add properties to
 * the set.
 */
public class MapProperties {

    private final ObjectMap<String, Object> properties;

    /**
     * Creates an empty properties set
     */
    public MapProperties() {
        properties = new ObjectMap<String, Object>();
    }

    /**
     * @param key property name
     * @return true if and only if the property exists
     */
    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }

    /**
     * @param key property name
     * @return the value for that property if it exists, otherwise, null
     */
    public Object get(String key) {
        return properties.get(key);
    }

    /**
     * Returns the object for the given key, casting it to clazz.
     *
     * @param key   the key of the object
     * @param clazz the class of the object
     * @return the object or null if the object is not in the map
     * @throws ClassCastException if the object with the given key is not of type clazz
     */
    public <T> T get(String key, Class<T> clazz) {
        return (T) get(key);
    }

    /**
     * Returns the object for the given key, casting it to clazz.
     *
     * @param key          the key of the object
     * @param defaultValue the default value
     * @param clazz        the class of the object
     * @return the object or the defaultValue if the object is not in the map
     * @throws ClassCastException if the object with the given key is not of type clazz
     */
    public <T> T get(String key, T defaultValue, Class<T> clazz) {
        Object object = get(key);
        return object == null ? defaultValue : (T) object;
    }

    /**
     * @param key   property name
     * @param value value to be inserted or modified (if it already existed)
     */
    public void put(String key, Object value) {
        properties.put(key, value);
    }

    /**
     * @param properties set of properties to be added
     */
    public void putAll(MapProperties properties) {
        this.properties.putAll(properties.properties);
    }

    /**
     * @param key property name to be removed
     */
    public void remove(String key) {
        properties.remove(key);
    }

    /**
     * Removes all properties
     */
    public void clear() {
        properties.clear();
    }

    /**
     * @return iterator for the property names
     */
    public Iterator<String> getKeys() {
        return properties.keys();
    }

    /**
     * @return iterator to properties' values
     */
    public Iterator<Object> getValues() {
        return properties.values();
    }
}
