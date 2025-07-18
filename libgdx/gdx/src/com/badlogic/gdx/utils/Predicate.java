package com.badlogic.gdx.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * Interface used to select items within an iterator against a predicate.
 */
public interface Predicate<T> {

    /**
     * @return true if the item matches the criteria and should be included in the iterator's items
     */
    boolean evaluate(T arg0);

    class PredicateIterator<T> implements Iterator<T> {
        public Iterator<T> iterator;
        public Predicate<T> predicate;
        public boolean end = false;
        public boolean peeked = false;
        public T next = null;

        public PredicateIterator(final Iterable<T> iterable, final Predicate<T> predicate) {
            this(iterable.iterator(), predicate);
        }

        public PredicateIterator(final Iterator<T> iterator, final Predicate<T> predicate) {
            set(iterator, predicate);
        }

        public void set(final Iterable<T> iterable, final Predicate<T> predicate) {
            set(iterable.iterator(), predicate);
        }

        public void set(final Iterator<T> iterator, final Predicate<T> predicate) {
            this.iterator = iterator;
            this.predicate = predicate;
            end = peeked = false;
            next = null;
        }

        @Override
        public boolean hasNext() {
            if (end) return false;
            if (next != null) return true;
            peeked = true;
            while (iterator.hasNext()) {
                final T n = iterator.next();
                if (predicate.evaluate(n)) {
                    next = n;
                    return true;
                }
            }
            end = true;
            return false;
        }

        @Override
        public T next() {
            if (next == null && !hasNext()) return null;
            final T result = next;
            next = null;
            peeked = false;
            return result;
        }

        @Override
        public void remove() {
            if (peeked) throw new GdxRuntimeException("Cannot remove between a call to hasNext() and next().");
            iterator.remove();
        }
    }

    class PredicateIterable<T> implements Iterable<T> {
        public Iterable<T> iterable;
        public Predicate<T> predicate;
        public PredicateIterator<T> iterator = null;

        public PredicateIterable(Iterable<T> iterable, Predicate<T> predicate) {
            set(iterable, predicate);
        }

        public void set(Iterable<T> iterable, Predicate<T> predicate) {
            this.iterable = iterable;
            this.predicate = predicate;
        }

        /**
         * Returns an iterator. Remove is supported.
         * <p>
         * If {@link Collections#allocateIterators} is false, the same iterator instance is returned each time this method is
         * called. Use the {@link Predicate.PredicateIterator} constructor for nested or multithreaded iteration.
         */
        @NotNull
        @Override
        public Iterator<T> iterator() {
            if (Collections.allocateIterators) return new PredicateIterator<>(iterable.iterator(), predicate);
            if (iterator == null)
                iterator = new PredicateIterator<>(iterable.iterator(), predicate);
            else
                iterator.set(iterable.iterator(), predicate);
            return iterator;
        }
    }
}
