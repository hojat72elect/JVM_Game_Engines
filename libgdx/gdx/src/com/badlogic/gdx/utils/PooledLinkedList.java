package com.badlogic.gdx.utils;

import org.jetbrains.annotations.Nullable;

/**
 * A simple linked list that pools its nodes.
 */
public class PooledLinkedList<T> {
    private final Pool<Item<T>> pool;
    private Item<T> head;
    private Item<T> tail;
    private Item<T> iter;
    private Item<T> curr;
    private int size = 0;

    public PooledLinkedList(int maxPoolSize) {
        this.pool = new Pool<Item<T>>(16, maxPoolSize) {
            @Override
            protected Item<T> newObject() {
                return new Item<>();
            }
        };
    }

    /**
     * Adds the specified object to the end of the list regardless of iteration status
     */
    public void add(T object) {
        Item<T> item = pool.obtain();
        item.payload = object;
        item.next = null;
        item.prev = null;

        if (head == null) {
            head = item;
            tail = item;
            size++;
            return;
        }

        item.prev = tail;
        tail.next = item;
        tail = item;
        size++;
    }

    /**
     * Adds the specified object to the head of the list regardless of iteration status
     */
    public void addFirst(T object) {
        Item<T> item = pool.obtain();
        item.payload = object;
        item.next = head;
        item.prev = null;

        if (head != null) {
            head.prev = item;
        } else {
            tail = item;
        }

        head = item;

        size++;
    }

    /**
     * Returns the number of items in the list
     */
    public int size() {
        return size;
    }

    /**
     * Starts iterating over the list's items from the head of the list
     */
    public void iter() {
        iter = head;
    }

    /**
     * Starts iterating over the list's items from the tail of the list
     */
    public void iterReverse() {
        iter = tail;
    }

    /**
     * Gets the next item in the list
     *
     * @return the next item in the list or null if there are no more items
     */
    public @Nullable T next() {
        if (iter == null) return null;

        T payload = iter.payload;
        curr = iter;
        iter = iter.next;
        return payload;
    }

    /**
     * Gets the previous item in the list
     *
     * @return the previous item in the list or null if there are no more items
     */
    public @Nullable T previous() {
        if (iter == null) return null;

        T payload = iter.payload;
        curr = iter;
        iter = iter.prev;
        return payload;
    }

    /**
     * Removes the current list item based on the iterator position.
     */
    public void remove() {
        if (curr == null) return;

        size--;

        Item<T> c = curr;
        Item<T> n = curr.next;
        Item<T> p = curr.prev;
        pool.free(curr);
        curr = null;

        if (size == 0) {
            head = null;
            tail = null;
            return;
        }

        if (c == head) {
            n.prev = null;
            head = n;
            return;
        }

        if (c == tail) {
            p.next = null;
            tail = p;
            return;
        }

        p.next = n;
        n.prev = p;
    }

    /**
     * Removes the tail of the list regardless of iteration status
     */
    public @Nullable T removeLast() {
        if (tail == null) {
            return null;
        }

        T payload = tail.payload;

        size--;

        Item<T> p = tail.prev;
        pool.free(tail);

        if (size == 0) {
            head = null;
            tail = null;
        } else {
            tail = p;
            tail.next = null;
        }

        return payload;
    }

    public void clear() {
        iter();
        T v = null;
        while ((v = next()) != null)
            remove();
    }

    static final class Item<T> {
        public T payload;
        public Item<T> next;
        public Item<T> prev;
    }
}
