package siozy.dev.lunaspring.API.util.utilities.maps.impl;

import org.jetbrains.annotations.NotNull;
import siozy.dev.lunaspring.API.util.utilities.maps.IPairMap;
import siozy.dev.lunaspring.API.util.utilities.maps.Pair;

import java.util.*;

/**
 * Карта с составным ключом из пары (K1, K2)
 *
 * @param <K1> тип первого ключа
 * @param <K2> тип второго ключа
 * @param <V>  тип значения
 */
public class PairMap<K1, K2, V> implements IPairMap<Pair<K1, K2>, V> {
    private static final Object DELETED = new Entry<>(null, null, null);

    private Entry<K1, K2, V>[] table;
    private int size;
    private int threshold;
    private final float loadFactor;
    private int modCount;

    public PairMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public PairMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    @SuppressWarnings("unchecked")
    public PairMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }
        if (initialCapacity > MAXIMUM_CAPACITY) {
            initialCapacity = MAXIMUM_CAPACITY;
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }

        int capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
        }

        this.table = new Entry[capacity];
        this.loadFactor = loadFactor;
        this.threshold = (int) (capacity * loadFactor);
        this.size = 0;
        this.modCount = 0;
    }

    public PairMap(Map<? extends Pair<K1, K2>, ? extends V> map) {
        this(map.size());
        putAll(map);
    }

    private int hash(K1 key1, K2 key2) {
        int h1 = key1 == null ? 0 : key1.hashCode();
        int h2 = key2 == null ? 0 : key2.hashCode();
        int combined = h1 * 31 + h2;
        combined ^= (combined >>> 20) ^ (combined >>> 12);
        return combined ^ (combined >>> 7) ^ (combined >>> 4);
    }

    private int findSlot(K1 key1, K2 key2) {
        int hash = hash(key1, key2);
        int idx = indexFor(hash, table.length);
        int startIdx = idx;

        do {
            Entry<K1, K2, V> entry = table[idx];
            if (entry == null) {
                return -idx - 1;
            }
            if (entry != DELETED &&
                    Objects.equals(entry.key1, key1) &&
                    Objects.equals(entry.key2, key2)) {
                return idx;
            }
            idx = (idx + 1) & (table.length - 1);
        } while (idx != startIdx);

        return -table.length - 1;
    }

    @SuppressWarnings("unchecked")
    public void resize() {
        int oldCapacity = table.length;
        int newCapacity = oldCapacity << 1;
        if (newCapacity > MAXIMUM_CAPACITY) {
            throw new RuntimeException("Map too large");
        }

        Entry<K1, K2, V>[] oldTable = table;
        table = new Entry[newCapacity];
        threshold = (int) (newCapacity * loadFactor);
        size = 0;
        modCount++;

        for (Entry<K1, K2, V> entry : oldTable) {
            if (entry != null && entry != DELETED) {
                put(entry.key1, entry.key2, entry.value);
            }
        }
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > threshold) {
            int newCapacity = table.length;
            while (newCapacity < minCapacity) {
                newCapacity <<= 1;
            }
            if (newCapacity > MAXIMUM_CAPACITY) {
                newCapacity = MAXIMUM_CAPACITY;
            }
            if (newCapacity > table.length) {
                resize();
            }
        }
    }

    public V get(K1 key1, K2 key2) {
        int idx = findSlot(key1, key2);
        if (idx < 0) return null;
        Entry<K1, K2, V> entry = table[idx];
        return entry == null || entry == DELETED ? null : entry.value;
    }

    public V put(K1 key1, K2 key2, V value) {
        int idx = findSlot(key1, key2);

        if (idx >= 0) {
            Entry<K1, K2, V> entry = table[idx];
            V oldValue = entry.value;
            entry.value = value;
            return oldValue;
        }

        idx = -idx - 1;
        if (table[idx] == DELETED) {
            table[idx] = new Entry<>(key1, key2, value);
            size++;
            modCount++;
            return null;
        }

        if (table[idx] == null) {
            table[idx] = new Entry<>(key1, key2, value);
            size++;
            modCount++;

            if (size >= threshold) {
                resize();
            }
            return null;
        }

        resize();
        return put(key1, key2, value);
    }

    @SuppressWarnings("unchecked")
    public V removeByKeys(K1 key1, K2 key2) {
        int idx = findSlot(key1, key2);
        if (idx < 0) return null;

        Entry<K1, K2, V> entry = table[idx];
        if (entry == null || entry == DELETED) return null;

        V oldValue = entry.value;
        table[idx] = (Entry<K1, K2, V>) DELETED;
        size--;
        modCount++;

        return oldValue;
    }

    public boolean containsKey(K1 key1, K2 key2) {
        return findSlot(key1, key2) >= 0;
    }

    public boolean putIfAbsent(K1 k1, K2 k2, V value) {
        V val = get(k1, k2);
        if (val != null) return false;
        put(k1, k2, value);
        return true;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsKey(Object key) {
        if (!(key instanceof Pair)) return false;
        Pair<K1, K2> pair = (Pair<K1, K2>) key;
        return containsKey(pair.getFirst(), pair.getSecond());
    }

    @Override
    public boolean containsValue(Object value) {
        for (Entry<K1, K2, V> entry : table) {
            if (entry != null && entry != DELETED && Objects.equals(entry.value, value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        if (!(key instanceof Pair)) return null;
        Pair<K1, K2> pair = (Pair<K1, K2>) key;
        return get(pair.getFirst(), pair.getSecond());
    }

    @Override
    public V put(Pair<K1, K2> key, V value) {
        return put(key.getFirst(), key.getSecond(), value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        if (!(key instanceof Pair)) return null;
        Pair<K1, K2> pair = (Pair<K1, K2>) key;
        return removeByKeys(pair.getFirst(), pair.getSecond());
    }

    @Override
    public void putAll(Map<? extends Pair<K1, K2>, ? extends V> m) {
        ensureCapacity(size + m.size());
        for (Map.Entry<? extends Pair<K1, K2>, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        Arrays.fill(table, null);
        size = 0;
        modCount++;
    }

    @Override
    public @NotNull Set<Pair<K1, K2>> keySet() {
        return new KeySet();
    }

    @Override
    public @NotNull Collection<V> values() {
        return new ValuesCollection();
    }

    @Override
    public @NotNull Set<Map.Entry<Pair<K1, K2>, V>> entrySet() {
        return new EntrySet();
    }

    private class KeySet extends AbstractSet<Pair<K1, K2>> {
        @Override
        public int size() {
            return size;
        }

        @Override
        public @NotNull Iterator<Pair<K1, K2>> iterator() {
            return new KeyIterator();
        }

        @Override
        public boolean contains(Object o) {
            return containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            return PairMap.this.remove(o) != null;
        }

        @Override
        public void clear() {
            PairMap.this.clear();
        }
    }

    private class ValuesCollection extends AbstractCollection<V> {
        @Override
        public int size() {
            return size;
        }

        @Override
        public @NotNull Iterator<V> iterator() {
            return new ValueIterator();
        }

        @Override
        public boolean contains(Object o) {
            return containsValue(o);
        }

        @Override
        public void clear() {
            PairMap.this.clear();
        }
    }

    private class EntrySet extends AbstractSet<Map.Entry<Pair<K1, K2>, V>> {
        @Override
        public int size() {
            return size;
        }

        @Override
        public @NotNull Iterator<Map.Entry<Pair<K1, K2>, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry<?, ?> e)) return false;
            Object key = e.getKey();
            if (!(key instanceof Pair)) return false;
            Pair<K1, K2> pair = (Pair<K1, K2>) key;
            V value = get(pair);
            return value != null && Objects.equals(value, e.getValue());
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry<?, ?> e)) return false;
            Object key = e.getKey();
            if (!(key instanceof Pair)) return false;
            Pair<K1, K2> pair = (Pair<K1, K2>) key;
            V value = get(pair);
            if (value != null && Objects.equals(value, e.getValue())) {
                PairMap.this.remove(pair);
                return true;
            }
            return false;
        }

        @Override
        public void clear() {
            PairMap.this.clear();
        }
    }

    private abstract class BaseIterator {
        private int nextIndex = 0;
        protected int lastReturned = -1;
        int expectedModCount = modCount;

        private int findNextValidIndex(int start) {
            for (int i = start; i < table.length; i++) {
                Entry<K1, K2, V> entry = table[i];
                if (entry != null && entry != DELETED) {
                    return i;
                }
            }
            return -1;
        }

        public boolean hasNext() {
            return findNextValidIndex(nextIndex) != -1;
        }

        @SuppressWarnings("unchecked")
        public void remove() {
            if (lastReturned == -1) {
                throw new IllegalStateException();
            }
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }

            if (table[lastReturned] != null && table[lastReturned] != DELETED) {
                table[lastReturned] = (Entry<K1, K2, V>) DELETED;
                size--;
                modCount++;
                expectedModCount = modCount;
            }
            lastReturned = -1;
        }

        protected void prepareNext() {
            int idx = findNextValidIndex(nextIndex);
            if (idx == -1) {
                throw new NoSuchElementException();
            }
            nextIndex = idx + 1;
            lastReturned = idx;
        }
    }

    private class KeyIterator extends BaseIterator implements Iterator<Pair<K1, K2>> {
        @Override
        public Pair<K1, K2> next() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            prepareNext();
            Entry<K1, K2, V> entry = table[lastReturned];
            return Pair.of(entry.key1, entry.key2);
        }
    }

    private class ValueIterator extends BaseIterator implements Iterator<V> {
        @Override
        public V next() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            prepareNext();
            return table[lastReturned].value;
        }
    }

    private class EntryIterator extends BaseIterator implements Iterator<Map.Entry<Pair<K1, K2>, V>> {
        @Override
        public Map.Entry<Pair<K1, K2>, V> next() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            prepareNext();
            return new MapEntry(table[lastReturned]);
        }
    }

    private class MapEntry implements Map.Entry<Pair<K1, K2>, V> {
        private final Entry<K1, K2, V> entry;

        MapEntry(Entry<K1, K2, V> entry) {
            this.entry = entry;
        }

        @Override
        public Pair<K1, K2> getKey() {
            return Pair.of(entry.key1, entry.key2);
        }

        @Override
        public V getValue() {
            return entry.value;
        }

        @Override
        public V setValue(V value) {
            V oldValue = entry.value;
            entry.value = value;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry<?, ?> e)) return false;
            return Objects.equals(getKey(), e.getKey()) &&
                    Objects.equals(getValue(), e.getValue());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue());
        }

        @Override
        public String toString() {
            return getKey() + "=" + getValue();
        }
    }

    private static class Entry<K1, K2, V> {
        final K1 key1;
        final K2 key2;
        V value;

        Entry(K1 key1, K2 key2, V value) {
            this.key1 = key1;
            this.key2 = key2;
            this.value = value;
        }

        Pair<K1, K2> toPair() {
            return Pair.of(key1, key2);
        }

        @Override
        public String toString() {
            return key1 + "," + key2 + "=" + value;
        }
    }

    @Override
    public String toString() {
        if (size == 0) return "{}";

        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<Pair<K1, K2>, V> entry : entrySet()) {
            if (!first) sb.append(", ");
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            first = false;
        }
        return sb.append("}").toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Map<?, ?> map)) return false;
        if (size() != map.size()) return false;

        for (Map.Entry<Pair<K1, K2>, V> entry : entrySet()) {
            var value = map.get(entry.getKey());
            if (!Objects.equals(entry.getValue(), value)) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (Map.Entry<Pair<K1, K2>, V> entry : entrySet()) {
            h += entry.hashCode();
        }
        return h;
    }
}