package com.badlogic.gdx.utils;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * A queue that allows one thread to call {@link #put(Object)} and another thread to call {@link #poll()}. Multiple threads must
 * not call these methods.
 * <p>
 * Generally speaking, an Atomic Queue is a data structure that provides thread-safe operations
 * for adding and removing elements. It ensures that these operations are performed atomically,
 * which means they are indivisible and cannot be interrupted by other threads. This prevents race
 * conditions and data corruption when multiple threads access the queue concurrently.
 */
public class AtomicQueue<T> {
    private final AtomicInteger writeIndex = new AtomicInteger();
    private final AtomicInteger readIndex = new AtomicInteger();
    private final AtomicReferenceArray<T> queue;

    public AtomicQueue(int capacity) {
        queue = new AtomicReferenceArray<>(capacity);
    }

    private int next(int idx) {
        return (idx + 1) % queue.length();
    }

    public boolean put(@Nullable T value) {
        int write = writeIndex.get();
        int read = readIndex.get();
        int next = next(write);
        if (next == read) return false;
        queue.set(write, value);
        writeIndex.set(next);
        return true;
    }

    public @Nullable T poll() {
        int read = readIndex.get();
        int write = writeIndex.get();
        if (read == write) return null;
        T value = queue.get(read);
        readIndex.set(next(read));
        return value;
    }
}
