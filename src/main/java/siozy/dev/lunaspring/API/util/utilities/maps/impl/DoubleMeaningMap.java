package siozy.dev.lunaspring.API.util.utilities.maps.impl;

import org.jetbrains.annotations.NotNull;
import siozy.dev.lunaspring.API.util.utilities.maps.IPairMap;
import siozy.dev.lunaspring.API.util.utilities.maps.Pair;

import java.util.*;
import java.util.Objects;

/**
 * Карта, где один ключ хранит два значения.
 *
 * @param <K> тип ключа
 * @param <V1> тип первого значения
 * @param <V2> тип второго значения
 */
public class DoubleMeaningMap<K, V1, V2> implements IPairMap<K, Pair<V1, V2>> {
    private static final Object DELETED = new Object();

    private Object[] table;
    private int size;
    private int threshold;
    private final float loadFactor;
    private int modCount;

    public DoubleMeaningMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public DoubleMeaningMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public DoubleMeaningMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY) initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) throw new IllegalArgumentException("Illegal load factor: " + loadFactor);

        int capacity = 1;
        while (capacity < initialCapacity) capacity <<= 1;

        this.table = new Object[capacity];
        this.loadFactor = loadFactor;
        this.threshold = (int) (capacity * loadFactor);
        this.size = 0;
        this.modCount = 0;
    }

    private int hash(K key) {
        return key == null ? 0 : key.hashCode();
    }

    @SuppressWarnings("unchecked")
    private Entry<V1, V2> entryAt(int index) {
        Object obj = table[index];
        if (obj == null || obj == DELETED) return null;
        return (Entry<V1, V2>) obj;
    }

    private int findSlot(K key) {
        int hash = hash(key);
        int idx = indexFor(hash, table.length);
        int startIdx = idx;

        do {
            Object obj = table[idx];
            if (obj == null) return -idx - 1;
            if (obj != DELETED) {
                @SuppressWarnings("unchecked")
                Entry<V1, V2> entry = (Entry<V1, V2>) obj;
                if (Objects.equals(entry.key, key)) return idx;
            }
            idx = (idx + 1) & (table.length - 1);
        } while (idx != startIdx);

        return -table.length - 1;
    }

    @SuppressWarnings("unchecked")
    public void resize() {
        int oldCapacity = table.length;
        int newCapacity = oldCapacity << 1;
        if (newCapacity > MAXIMUM_CAPACITY) throw new RuntimeException("Map too large");

        Object[] oldTable = table;
        table = new Object[newCapacity];
        threshold = (int) (newCapacity * loadFactor);
        size = 0;
        modCount++;

        for (Object obj : oldTable) {
            if (obj != null && obj != DELETED) {
                Entry<V1, V2> entry = (Entry<V1, V2>) obj;
                put((K) entry.key, entry.value1, entry.value2);
            }
        }
    }

    public V1 put(K key, V1 value1, V2 value2) {
        int idx = findSlot(key);

        if (idx >= 0) {
            @SuppressWarnings("unchecked")
            Entry<V1, V2> entry = (Entry<V1, V2>) table[idx];
            V1 oldValue1 = entry.value1;
            entry.value1 = value1;
            entry.value2 = value2;
            return oldValue1;
        }

        idx = -idx - 1;
        if (table[idx] == DELETED || table[idx] == null) {
            table[idx] = new Entry<>(key, value1, value2);
            size++;
            modCount++;
            if (size >= threshold) resize();
            return null;
        }

        resize();
        return put(key, value1, value2);
    }

    public V1 getFirst(K key) {
        int idx = findSlot(key);
        if (idx < 0) return null;
        Entry<V1, V2> entry = entryAt(idx);
        return entry == null ? null : entry.value1;
    }

    public V2 getSecond(K key) {
        int idx = findSlot(key);
        if (idx < 0) return null;
        Entry<V1, V2> entry = entryAt(idx);
        return entry == null ? null : entry.value2;
    }

    public Pair<V1, V2> getBoth(K key) {
        int idx = findSlot(key);
        if (idx < 0) return null;
        Entry<V1, V2> entry = entryAt(idx);
        return entry == null ? null : Pair.of(entry.value1, entry.value2);
    }

    public V1 setFirst(K key, V1 value1) {
        int idx = findSlot(key);
        if (idx >= 0) {
            @SuppressWarnings("unchecked")
            Entry<V1, V2> entry = (Entry<V1, V2>) table[idx];
            V1 old = entry.value1;
            entry.value1 = value1;
            return old;
        }
        return null;
    }

    public V2 setSecond(K key, V2 value2) {
        int idx = findSlot(key);
        if (idx >= 0) {
            @SuppressWarnings("unchecked")
            Entry<V1, V2> entry = (Entry<V1, V2>) table[idx];
            V2 old = entry.value2;
            entry.value2 = value2;
            return old;
        }
        return null;
    }

    public Pair<V1, V2> removeByKey(K key) {
        int idx = findSlot(key);
        if (idx < 0) return null;
        Entry<V1, V2> entry = entryAt(idx);
        if (entry == null) return null;
        Pair<V1, V2> removed = Pair.of(entry.value1, entry.value2);
        table[idx] = DELETED;
        size--;
        modCount++;
        return removed;
    }

    @Override
    public int size() { return size; }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsKey(Object key) {
        if (key == null) return false;
        return findSlot((K) key) >= 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsValue(Object value) {
        if (!(value instanceof Pair)) return false;
        Pair<V1, V2> pair = (Pair<V1, V2>) value;
        for (Object obj : table) {
            if (obj != null && obj != DELETED) {
                Entry<V1, V2> entry = (Entry<V1, V2>) obj;
                if (Objects.equals(entry.value1, pair.getFirst()) &&
                        Objects.equals(entry.value2, pair.getSecond())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Pair<V1, V2> get(Object key) {
        if (key == null) return null;
        int idx = findSlot((K) key);
        if (idx < 0) return null;
        Entry<V1, V2> entry = entryAt(idx);
        return entry == null ? null : Pair.of(entry.value1, entry.value2);
    }

    @Override
    public Pair<V1, V2> put(K key, Pair<V1, V2> value) {
        V1 old = put(key, value.getFirst(), value.getSecond());
        return old == null ? null : Pair.of(old, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Pair<V1, V2> remove(Object key) {
        if (key == null) return null;
        return removeByKey((K) key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends Pair<V1, V2>> m) {
        for (Map.Entry<? extends K, ? extends Pair<V1, V2>> entry : m.entrySet()) {
            Pair<V1, V2> pair = entry.getValue();
            put(entry.getKey(), pair.getFirst(), pair.getSecond());
        }
    }

    @Override
    public void clear() {
        Arrays.fill(table, null);
        size = 0;
        modCount++;
    }

    @Override
    public @NotNull Set<K> keySet() { return new KeySet(); }

    @Override
    public @NotNull Collection<Pair<V1, V2>> values() { return new ValuesCollection(); }

    @Override
    public @NotNull Set<Map.Entry<K, Pair<V1, V2>>> entrySet() { return new EntrySet(); }

    private class KeySet extends AbstractSet<K> {
        @Override public int size() { return size; }
        @Override public @NotNull Iterator<K> iterator() { return new KeyIterator(); }
        @Override public boolean contains(Object o) { return containsKey(o); }
        @Override public boolean remove(Object o) { return DoubleMeaningMap.this.remove(o) != null; }
        @Override public void clear() { DoubleMeaningMap.this.clear(); }
    }

    private class ValuesCollection extends AbstractCollection<Pair<V1, V2>> {
        @Override public int size() { return size; }
        @Override public @NotNull Iterator<Pair<V1, V2>> iterator() { return new ValueIterator(); }
        @Override public boolean contains(Object o) { return containsValue(o); }
        @Override public void clear() { DoubleMeaningMap.this.clear(); }
    }

    private class EntrySet extends AbstractSet<Map.Entry<K, Pair<V1, V2>>> {
        @Override public int size() { return size; }
        @Override public @NotNull Iterator<Map.Entry<K, Pair<V1, V2>>> iterator() { return new EntryIterator(); }
        @Override public void clear() { DoubleMeaningMap.this.clear(); }
    }

    private abstract class BaseIterator {
        private int nextIndex = 0;
        protected int lastReturned = -1;
        int expectedModCount = modCount;

        private int findNextValidIndex(int start) {
            for (int i = start; i < table.length; i++) {
                Object obj = table[i];
                if (obj != null && obj != DELETED) return i;
            }
            return -1;
        }

        public boolean hasNext() { return findNextValidIndex(nextIndex) != -1; }

        public void remove() {
            if (lastReturned == -1) throw new IllegalStateException();
            if (expectedModCount != modCount) throw new ConcurrentModificationException();
            if (table[lastReturned] != null && table[lastReturned] != DELETED) {
                table[lastReturned] = DELETED;
                size--;
                modCount++;
                expectedModCount = modCount;
            }
            lastReturned = -1;
        }

        protected void prepareNext() {
            int idx = findNextValidIndex(nextIndex);
            if (idx == -1) throw new NoSuchElementException();
            nextIndex = idx + 1;
            lastReturned = idx;
        }
    }

    private class KeyIterator extends BaseIterator implements Iterator<K> {
        @Override
        public K next() {
            if (expectedModCount != modCount) throw new ConcurrentModificationException();
            prepareNext();
            @SuppressWarnings("unchecked")
            Entry<V1, V2> entry = (Entry<V1, V2>) table[lastReturned];
            return (K) entry.key;
        }
    }

    private class ValueIterator extends BaseIterator implements Iterator<Pair<V1, V2>> {
        @Override
        public Pair<V1, V2> next() {
            if (expectedModCount != modCount) throw new ConcurrentModificationException();
            prepareNext();
            @SuppressWarnings("unchecked")
            Entry<V1, V2> entry = (Entry<V1, V2>) table[lastReturned];
            return Pair.of(entry.value1, entry.value2);
        }
    }

    private class EntryIterator extends BaseIterator implements Iterator<Map.Entry<K, Pair<V1, V2>>> {
        @Override
        public Map.Entry<K, Pair<V1, V2>> next() {
            if (expectedModCount != modCount) throw new ConcurrentModificationException();
            prepareNext();
            @SuppressWarnings("unchecked")
            Entry<V1, V2> entry = (Entry<V1, V2>) table[lastReturned];
            return new MapEntry(entry);
        }
    }

    private class MapEntry implements Map.Entry<K, Pair<V1, V2>> {
        private final Entry<V1, V2> entry;
        MapEntry(Entry<V1, V2> entry) { this.entry = entry; }
        @Override public K getKey() { return (K) entry.key; }
        @Override public Pair<V1, V2> getValue() { return Pair.of(entry.value1, entry.value2); }
        @Override public Pair<V1, V2> setValue(Pair<V1, V2> value) {
            Pair<V1, V2> old = Pair.of(entry.value1, entry.value2);
            entry.value1 = value.getFirst();
            entry.value2 = value.getSecond();
            return old;
        }
        @Override public boolean equals(Object o) {
            if (!(o instanceof Map.Entry<?, ?> e)) return false;
            return Objects.equals(getKey(), e.getKey()) && Objects.equals(getValue(), e.getValue());
        }
        @Override public int hashCode() { return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue()); }
    }

    private static class Entry<V1, V2> {
        final Object key;
        V1 value1;
        V2 value2;
        Entry(Object key, V1 value1, V2 value2) {
            this.key = key;
            this.value1 = value1;
            this.value2 = value2;
        }
    }

    @Override
    public String toString() {
        if (size == 0) return "{}";
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<K, Pair<V1, V2>> entry : entrySet()) {
            if (!first) sb.append(", ");
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            first = false;
        }
        return sb.append("}").toString();
    }
}