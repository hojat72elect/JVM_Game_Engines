package com.badlogic.gdx.utils;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.ReflectionException;

/**
 * Pool that creates new instances of a type using reflection. The type must have a zero argument constructor.
 * {@link Constructor#setAccessible(boolean)} will be used if the class and/or constructor is not visible.
 */
public class ReflectionPool<T> extends Pool<T> {
    private final Constructor constructor;

    public ReflectionPool(Class<T> type) {
        this(type, 16, Integer.MAX_VALUE);
    }

    public ReflectionPool(Class<T> type, int initialCapacity) {
        this(type, initialCapacity, Integer.MAX_VALUE);
    }

    public ReflectionPool(Class<T> type, int initialCapacity, int max) {
        super(initialCapacity, max);
        constructor = findConstructor(type);
        if (constructor == null)
            throw new RuntimeException("Class cannot be created (missing no-arg constructor): " + type.getName());
    }

    private @Null Constructor findConstructor(Class<T> type) {
        try {
            return ClassReflection.getConstructor(type, (Class[]) null);
        } catch (Exception ex1) {
            try {
                Constructor constructor = ClassReflection.getDeclaredConstructor(type, (Class[]) null);
                constructor.setAccessible(true);
                return constructor;
            } catch (ReflectionException ex2) {
                return null;
            }
        }
    }

    protected T newObject() {
        try {
            return (T) constructor.newInstance((Object[]) null);
        } catch (Exception ex) {
            throw new GdxRuntimeException("Unable to create new instance: " + constructor.getDeclaringClass().getName(), ex);
        }
    }
}
