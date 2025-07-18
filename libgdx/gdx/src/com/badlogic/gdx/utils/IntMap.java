package com.badlogic.gdx.utils;

import static com.badlogic.gdx.utils.ObjectSet.tableSize;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An unordered map where the keys are unboxed ints and values are objects. No allocation is done except when growing the table
 * size.
 * <p>
 * This class performs fast contains and remove (typically O(1), worst case O(n) but that is rare in practice). Add may be
 * slightly slower, depending on hash collisions. Hashcodes are rehashed to reduce collisions and the need to resize. Load factors
 * greater than 0.91 greatly increase the chances to resize to the next higher POT size.
 * <p>
 * Unordered sets and maps are not designed to provide especially fast iteration. Iteration is faster with OrderedSet and
 * OrderedMap.
 * <p>
 * This implementation uses linear probing with the backward shift algorithm for removal. Hashcodes are rehashed using Fibonacci
 * hashing, instead of the more common power-of-two mask, to better distribute poor hashCodes (see <a href=
 * "https://probablydance.com/2018/06/16/fibonacci-hashing-the-optimization-that-the-world-forgot-or-a-better-alternative-to-integer-modulo/">Malte
 * Skarupke's blog post</a>). Linear probing continues to work even when all hashCodes collide, just more slowly.
 */
public class IntMap<V> implements Iterable<IntMap.Entry<V>> {
    private final float loadFactor;
    public int size;
    /**
     * Used by {@link #place(int)} to bit shift the upper bits of a {@code long} into a usable range (&gt;= 0 and &lt;=
     * {@link #mask}). The shift can be negative, which is convenient to match the number of bits in mask: if mask is a 7-bit
     * number, a shift of -7 shifts the upper 7 bits into the lowest 7 positions. This class sets the shift &gt; 32 and &lt; 64,
     * which if used with an int will still move the upper bits of an int to the lower bits due to Java's implicit modulus on
     * shifts.
     * <p>
     * {@link #mask} can also be used to mask the low bits of a number, which may be faster for some hashcodes, if
     * {@link #place(int)} is overridden.
     */
    protected int shift;
    /**
     * A bitmask used to confine hashcodes to the size of the table. Must be all 1 bits in its low positions, ie a power of two
     * minus 1. If {@link #place(int)} is overriden, this can be used instead of {@link #shift} to isolate usable bits of a
     * hash.
     */
    protected int mask;
    int[] keyTable;
    V[] valueTable;
    V zeroValue;
    boolean hasZeroValue;
    private int threshold;
    private transient Entries entries1, entries2;
    private transient Values values1, values2;
    private transient Keys keys1, keys2;

    /**
     * Creates a new map with an initial capacity of 51 and a load factor of 0.8.
     */
    public IntMap() {
        this(51, 0.8f);
    }

    /**
     * Creates a new map with a load factor of 0.8.
     *
     * @param initialCapacity The backing array size is initialCapacity / loadFactor, increased to the next power of two.
     */
    public IntMap(int initialCapacity) {
        this(initialCapacity, 0.8f);
    }

    /**
     * Creates a new map with the specified initial capacity and load factor. This map will hold initialCapacity items before
     * growing the backing table.
     *
     * @param initialCapacity The backing array size is initialCapacity / loadFactor, increased to the next power of two.
     */
    public IntMap(int initialCapacity, float loadFactor) {
        if (loadFactor <= 0f || loadFactor >= 1f)
            throw new IllegalArgumentException("loadFactor must be > 0 and < 1: " + loadFactor);
        this.loadFactor = loadFactor;

        int tableSize = tableSize(initialCapacity, loadFactor);
        threshold = (int) (tableSize * loadFactor);
        mask = tableSize - 1;
        shift = Long.numberOfLeadingZeros(mask);

        keyTable = new int[tableSize];
        valueTable = (V[]) new Object[tableSize];
    }

    /**
     * Creates a new map identical to the specified map.
     */
    public IntMap(IntMap<? extends V> map) {
        this((int) (map.keyTable.length * map.loadFactor), map.loadFactor);
        System.arraycopy(map.keyTable, 0, keyTable, 0, map.keyTable.length);
        System.arraycopy(map.valueTable, 0, valueTable, 0, map.valueTable.length);
        size = map.size;
        zeroValue = map.zeroValue;
        hasZeroValue = map.hasZeroValue;
    }

    /**
     * Returns an index >= 0 and <= {@link #mask} for the specified {@code item}.
     * <p>
     * The default implementation uses Fibonacci hashing on the item's {@link Object#hashCode()}: the hashcode is multiplied by a
     * long constant (2 to the 64th, divided by the golden ratio) then the uppermost bits are shifted into the lowest positions to
     * obtain an index in the desired range. Multiplication by a long may be slower than int (eg on GWT) but greatly improves
     * rehashing, allowing even very poor hashcodes, such as those that only differ in their upper bits, to be used without high
     * collision rates. Fibonacci hashing has increased collision rates when all or most hashcodes are multiples of larger
     * Fibonacci numbers (see <a href=
     * "https://probablydance.com/2018/06/16/fibonacci-hashing-the-optimization-that-the-world-forgot-or-a-better-alternative-to-integer-modulo/">Malte
     * Skarupke's blog post</a>).
     * <p>
     * This method can be overriden to customizing hashing. This may be useful eg in the unlikely event that most hashcodes are
     * Fibonacci numbers, if keys provide poor or incorrect hashcodes, or to simplify hashing if keys provide high quality
     * hashcodes and don't need Fibonacci hashing: {@code return item.hashCode() & mask;}
     */
    protected int place(int item) {
        return (int) (item * 0x9E3779B97F4A7C15L >>> shift);
    }

    /**
     * Returns the index of the key if already present, else -(index + 1) for the next empty index. This can be overridden in this
     * pacakge to compare for equality differently than {@link Object#equals(Object)}.
     */
    private int locateKey(int key) {
        int[] keyTable = this.keyTable;
        for (int i = place(key); ; i = i + 1 & mask) {
            int other = keyTable[i];
            if (other == 0) return -(i + 1); // Empty space is available.
            if (other == key) return i; // Same key was found.
        }
    }

    public @Nullable V put(int key, @Nullable V value) {
        if (key == 0) {
            V oldValue = zeroValue;
            zeroValue = value;
            if (!hasZeroValue) {
                hasZeroValue = true;
                size++;
            }
            return oldValue;
        }
        int i = locateKey(key);
        if (i >= 0) { // Existing key was found.
            V oldValue = valueTable[i];
            valueTable[i] = value;
            return oldValue;
        }
        i = -(i + 1); // Empty space was found.
        keyTable[i] = key;
        valueTable[i] = value;
        if (++size >= threshold) resize(keyTable.length << 1);
        return null;
    }

    public void putAll(IntMap<? extends V> map) {
        ensureCapacity(map.size);
        if (map.hasZeroValue) put(0, map.zeroValue);
        int[] keyTable = map.keyTable;
        V[] valueTable = map.valueTable;
        for (int i = 0, n = keyTable.length; i < n; i++) {
            int key = keyTable[i];
            if (key != 0) put(key, valueTable[i]);
        }
    }

    /**
     * Skips checks for existing keys, doesn't increment size, doesn't need to handle key 0.
     */
    private void putResize(int key, @Nullable V value) {
        int[] keyTable = this.keyTable;
        for (int i = place(key); ; i = (i + 1) & mask) {
            if (keyTable[i] == 0) {
                keyTable[i] = key;
                valueTable[i] = value;
                return;
            }
        }
    }

    public V get(int key) {
        if (key == 0) return hasZeroValue ? zeroValue : null;
        int i = locateKey(key);
        return i >= 0 ? valueTable[i] : null;
    }

    public V get(int key, @Nullable V defaultValue) {
        if (key == 0) return hasZeroValue ? zeroValue : defaultValue;
        int i = locateKey(key);
        return i >= 0 ? valueTable[i] : defaultValue;
    }

    /**
     * Returns the value for the removed key, or null if the key is not in the map.
     */
    public @Nullable V remove(int key) {
        if (key == 0) {
            if (!hasZeroValue) return null;
            hasZeroValue = false;
            V oldValue = zeroValue;
            zeroValue = null;
            size--;
            return oldValue;
        }

        int i = locateKey(key);
        if (i < 0) return null;
        int[] keyTable = this.keyTable;
        V[] valueTable = this.valueTable;
        V oldValue = valueTable[i];
        int mask = this.mask, next = i + 1 & mask;
        while ((key = keyTable[next]) != 0) {
            int placement = place(key);
            if ((next - placement & mask) > (i - placement & mask)) {
                keyTable[i] = key;
                valueTable[i] = valueTable[next];
                i = next;
            }
            next = next + 1 & mask;
        }
        keyTable[i] = 0;
        valueTable[i] = null;
        size--;
        return oldValue;
    }

    /**
     * Returns true if the map has one or more items.
     */
    public boolean notEmpty() {
        return size > 0;
    }

    /**
     * Returns true if the map is empty.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Reduces the size of the backing arrays to be the specified capacity / loadFactor, or less. If the capacity is already less,
     * nothing is done. If the map contains more items than the specified capacity, the next highest power of two capacity is used
     * instead.
     */
    public void shrink(int maximumCapacity) {
        if (maximumCapacity < 0) throw new IllegalArgumentException("maximumCapacity must be >= 0: " + maximumCapacity);
        int tableSize = tableSize(maximumCapacity, loadFactor);
        if (keyTable.length > tableSize) resize(tableSize);
    }

    /**
     * Clears the map and reduces the size of the backing arrays to be the specified capacity / loadFactor, if they are larger.
     */
    public void clear(int maximumCapacity) {
        int tableSize = tableSize(maximumCapacity, loadFactor);
        if (keyTable.length <= tableSize) {
            clear();
            return;
        }
        size = 0;
        hasZeroValue = false;
        zeroValue = null;
        resize(tableSize);
    }

    public void clear() {
        if (size == 0) return;
        size = 0;
        Arrays.fill(keyTable, 0);
        Arrays.fill(valueTable, null);
        zeroValue = null;
        hasZeroValue = false;
    }

    /**
     * Returns true if the specified value is in the map. Note this traverses the entire map and compares every value, which may
     * be an expensive operation.
     *
     * @param identity If true, uses == to compare the specified value with values in the map. If false, uses
     *                 {@link #equals(Object)}.
     */
    public boolean containsValue(@Nullable Object value, boolean identity) {
        V[] valueTable = this.valueTable;
        if (value == null) {
            if (hasZeroValue && zeroValue == null) return true;
            int[] keyTable = this.keyTable;
            for (int i = valueTable.length - 1; i >= 0; i--)
                if (keyTable[i] != 0 && valueTable[i] == null) return true;
        } else if (identity) {
            if (value == zeroValue) return true;
            for (int i = valueTable.length - 1; i >= 0; i--)
                if (valueTable[i] == value) return true;
        } else {
            if (hasZeroValue && value.equals(zeroValue)) return true;
            for (int i = valueTable.length - 1; i >= 0; i--)
                if (value.equals(valueTable[i])) return true;
        }
        return false;
    }

    public boolean containsKey(int key) {
        if (key == 0) return hasZeroValue;
        return locateKey(key) >= 0;
    }

    /**
     * Returns the key for the specified value, or <tt>notFound</tt> if it is not in the map. Note this traverses the entire map
     * and compares every value, which may be an expensive operation.
     *
     * @param identity If true, uses == to compare the specified value with values in the map. If false, uses
     *                 {@link #equals(Object)}.
     */
    public int findKey(@Nullable Object value, boolean identity, int notFound) {
        V[] valueTable = this.valueTable;
        if (value == null) {
            if (hasZeroValue && zeroValue == null) return 0;
            int[] keyTable = this.keyTable;
            for (int i = valueTable.length - 1; i >= 0; i--)
                if (keyTable[i] != 0 && valueTable[i] == null) return keyTable[i];
        } else if (identity) {
            if (value == zeroValue) return 0;
            for (int i = valueTable.length - 1; i >= 0; i--)
                if (valueTable[i] == value) return keyTable[i];
        } else {
            if (hasZeroValue && value.equals(zeroValue)) return 0;
            for (int i = valueTable.length - 1; i >= 0; i--)
                if (value.equals(valueTable[i])) return keyTable[i];
        }
        return notFound;
    }

    /**
     * Increases the size of the backing array to accommodate the specified number of additional items / loadFactor. Useful before
     * adding many items to avoid multiple backing array resizes.
     */
    public void ensureCapacity(int additionalCapacity) {
        int tableSize = tableSize(size + additionalCapacity, loadFactor);
        if (keyTable.length < tableSize) resize(tableSize);
    }

    private void resize(int newSize) {
        int oldCapacity = keyTable.length;
        threshold = (int) (newSize * loadFactor);
        mask = newSize - 1;
        shift = Long.numberOfLeadingZeros(mask);

        int[] oldKeyTable = keyTable;
        V[] oldValueTable = valueTable;

        keyTable = new int[newSize];
        valueTable = (V[]) new Object[newSize];

        if (size > 0) {
            for (int i = 0; i < oldCapacity; i++) {
                int key = oldKeyTable[i];
                if (key != 0) putResize(key, oldValueTable[i]);
            }
        }
    }

    public int hashCode() {
        int h = size;
        if (hasZeroValue && zeroValue != null) h += zeroValue.hashCode();
        int[] keyTable = this.keyTable;
        V[] valueTable = this.valueTable;
        for (int i = 0, n = keyTable.length; i < n; i++) {
            int key = keyTable[i];
            if (key != 0) {
                h += key * 31;
                V value = valueTable[i];
                if (value != null) h += value.hashCode();
            }
        }
        return h;
    }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof IntMap)) return false;
        IntMap other = (IntMap) obj;
        if (other.size != size) return false;
        if (other.hasZeroValue != hasZeroValue) return false;
        if (hasZeroValue) {
            if (other.zeroValue == null) {
                if (zeroValue != null) return false;
            } else {
                if (!other.zeroValue.equals(zeroValue)) return false;
            }
        }
        int[] keyTable = this.keyTable;
        V[] valueTable = this.valueTable;
        for (int i = 0, n = keyTable.length; i < n; i++) {
            int key = keyTable[i];
            if (key != 0) {
                V value = valueTable[i];
                if (value == null) {
                    if (other.get(key, ObjectMap.dummy) != null) return false;
                } else {
                    if (!value.equals(other.get(key))) return false;
                }
            }
        }
        return true;
    }

    /**
     * Uses == for comparison of each value.
     */
    public boolean equalsIdentity(@Nullable Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof IntMap)) return false;
        IntMap other = (IntMap) obj;
        if (other.size != size) return false;
        if (other.hasZeroValue != hasZeroValue) return false;
        if (hasZeroValue && zeroValue != other.zeroValue) return false;
        int[] keyTable = this.keyTable;
        V[] valueTable = this.valueTable;
        for (int i = 0, n = keyTable.length; i < n; i++) {
            int key = keyTable[i];
            if (key != 0 && valueTable[i] != other.get(key, ObjectMap.dummy)) return false;
        }
        return true;
    }

    public String toString() {
        if (size == 0) return "[]";
        java.lang.StringBuilder buffer = new java.lang.StringBuilder(32);
        buffer.append('[');
        int[] keyTable = this.keyTable;
        V[] valueTable = this.valueTable;
        int i = keyTable.length;
        if (hasZeroValue) {
            buffer.append("0=");
            buffer.append(zeroValue);
        } else {
            while (i-- > 0) {
                int key = keyTable[i];
                if (key == 0) continue;
                buffer.append(key);
                buffer.append('=');
                buffer.append(valueTable[i]);
                break;
            }
        }
        while (i-- > 0) {
            int key = keyTable[i];
            if (key == 0) continue;
            buffer.append(", ");
            buffer.append(key);
            buffer.append('=');
            buffer.append(valueTable[i]);
        }
        buffer.append(']');
        return buffer.toString();
    }

    public Iterator<Entry<V>> iterator() {
        return entries();
    }

    /**
     * Returns an iterator for the entries in the map. Remove is supported.
     * <p>
     * If {@link Collections#allocateIterators} is false, the same iterator instance is returned each time this method is called.
     * Use the {@link Entries} constructor for nested or multithreaded iteration.
     */
    public Entries<V> entries() {
        if (Collections.allocateIterators) return new Entries(this);
        if (entries1 == null) {
            entries1 = new Entries(this);
            entries2 = new Entries(this);
        }
        if (!entries1.valid) {
            entries1.reset();
            entries1.valid = true;
            entries2.valid = false;
            return entries1;
        }
        entries2.reset();
        entries2.valid = true;
        entries1.valid = false;
        return entries2;
    }

    /**
     * Returns an iterator for the values in the map. Remove is supported.
     * <p>
     * If {@link Collections#allocateIterators} is false, the same iterator instance is returned each time this method is called.
     * Use the {@link Entries} constructor for nested or multithreaded iteration.
     */
    public Values<V> values() {
        if (Collections.allocateIterators) return new Values(this);
        if (values1 == null) {
            values1 = new Values(this);
            values2 = new Values(this);
        }
        if (!values1.valid) {
            values1.reset();
            values1.valid = true;
            values2.valid = false;
            return values1;
        }
        values2.reset();
        values2.valid = true;
        values1.valid = false;
        return values2;
    }

    /**
     * Returns an iterator for the keys in the map. Remove is supported.
     * <p>
     * If {@link Collections#allocateIterators} is false, the same iterator instance is returned each time this method is called.
     * Use the {@link Entries} constructor for nested or multithreaded iteration.
     */
    public Keys keys() {
        if (Collections.allocateIterators) return new Keys(this);
        if (keys1 == null) {
            keys1 = new Keys(this);
            keys2 = new Keys(this);
        }
        if (!keys1.valid) {
            keys1.reset();
            keys1.valid = true;
            keys2.valid = false;
            return keys1;
        }
        keys2.reset();
        keys2.valid = true;
        keys1.valid = false;
        return keys2;
    }

    static public class Entry<V> {
        public int key;
        public @Nullable V value;

        public String toString() {
            return key + "=" + value;
        }
    }

    static private class MapIterator<V> {
        static final int INDEX_ZERO = -1;
        static private final int INDEX_ILLEGAL = -2;
        final IntMap<V> map;
        public boolean hasNext;
        int nextIndex, currentIndex;
        boolean valid = true;

        public MapIterator(IntMap<V> map) {
            this.map = map;
            reset();
        }

        public void reset() {
            currentIndex = INDEX_ILLEGAL;
            nextIndex = INDEX_ZERO;
            if (map.hasZeroValue)
                hasNext = true;
            else
                findNextIndex();
        }

        void findNextIndex() {
            int[] keyTable = map.keyTable;
            for (int n = keyTable.length; ++nextIndex < n; ) {
                if (keyTable[nextIndex] != 0) {
                    hasNext = true;
                    return;
                }
            }
            hasNext = false;
        }

        public void remove() {
            int i = currentIndex;
            if (i == INDEX_ZERO && map.hasZeroValue) {
                map.hasZeroValue = false;
                map.zeroValue = null;
            } else if (i < 0) {
                throw new IllegalStateException("next must be called before remove.");
            } else {
                int[] keyTable = map.keyTable;
                V[] valueTable = map.valueTable;
                int mask = map.mask, next = i + 1 & mask, key;
                while ((key = keyTable[next]) != 0) {
                    int placement = map.place(key);
                    if ((next - placement & mask) > (i - placement & mask)) {
                        keyTable[i] = key;
                        valueTable[i] = valueTable[next];
                        i = next;
                    }
                    next = next + 1 & mask;
                }
                keyTable[i] = 0;
                valueTable[i] = null;
                if (i != currentIndex) --nextIndex;
            }
            currentIndex = INDEX_ILLEGAL;
            map.size--;
        }
    }

    static public class Entries<V> extends MapIterator<V> implements Iterable<Entry<V>>, Iterator<Entry<V>> {
        private final Entry<V> entry = new Entry();

        public Entries(IntMap map) {
            super(map);
        }

        /**
         * Note the same entry instance is returned each time this method is called.
         */
        public Entry<V> next() {
            if (!hasNext) throw new NoSuchElementException();
            if (!valid) throw new GdxRuntimeException("#iterator() cannot be used nested.");
            int[] keyTable = map.keyTable;
            if (nextIndex == INDEX_ZERO) {
                entry.key = 0;
                entry.value = map.zeroValue;
            } else {
                entry.key = keyTable[nextIndex];
                entry.value = map.valueTable[nextIndex];
            }
            currentIndex = nextIndex;
            findNextIndex();
            return entry;
        }

        public boolean hasNext() {
            if (!valid) throw new GdxRuntimeException("#iterator() cannot be used nested.");
            return hasNext;
        }

        public Iterator<Entry<V>> iterator() {
            return this;
        }
    }

    static public class Values<V> extends MapIterator<V> implements Iterable<V>, Iterator<V> {
        public Values(IntMap<V> map) {
            super(map);
        }

        public boolean hasNext() {
            if (!valid) throw new GdxRuntimeException("#iterator() cannot be used nested.");
            return hasNext;
        }

        public @Nullable V next() {
            if (!hasNext) throw new NoSuchElementException();
            if (!valid) throw new GdxRuntimeException("#iterator() cannot be used nested.");
            V value;
            if (nextIndex == INDEX_ZERO)
                value = map.zeroValue;
            else
                value = map.valueTable[nextIndex];
            currentIndex = nextIndex;
            findNextIndex();
            return value;
        }

        public Iterator<V> iterator() {
            return this;
        }

        /**
         * Returns a new array containing the remaining values.
         */
        public Array<V> toArray() {
            Array array = new Array(true, map.size);
            while (hasNext)
                array.add(next());
            return array;
        }
    }

    static public class Keys extends MapIterator {
        public Keys(IntMap map) {
            super(map);
        }

        public int next() {
            if (!hasNext) throw new NoSuchElementException();
            if (!valid) throw new GdxRuntimeException("#iterator() cannot be used nested.");
            int key = nextIndex == INDEX_ZERO ? 0 : map.keyTable[nextIndex];
            currentIndex = nextIndex;
            findNextIndex();
            return key;
        }

        /**
         * Returns a new array containing the remaining keys.
         */
        public IntArray toArray() {
            IntArray array = new IntArray(true, map.size);
            while (hasNext)
                array.add(next());
            return array;
        }

        /**
         * Adds the remaining values to the specified array.
         */
        public IntArray toArray(IntArray array) {
            while (hasNext)
                array.add(next());
            return array;
        }
    }
}
