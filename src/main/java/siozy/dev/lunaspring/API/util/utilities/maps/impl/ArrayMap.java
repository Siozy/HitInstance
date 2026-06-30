package siozy.dev.lunaspring.API.util.utilities.maps.impl;

import org.jetbrains.annotations.NotNull;
import siozy.dev.lunaspring.API.util.utilities.maps.LunaMap;

import java.util.*;

/**
 * Карта с доступом по индексам. Хранит ключи и значения в параллельных массивах.
 *
 * @param <K> тип ключей
 * @param <V> тип значений
 */
public class ArrayMap<K, V> implements LunaMap<K, V> {
    private static final int DEFAULT_CAPACITY = 8;
    private static final float GROWTH_FACTOR = 1.5f;

    private K[] keys;
    private V[] values;
    private int size;

    public ArrayMap() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public ArrayMap(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Initial capacity cannot be negative");
        }
        this.keys = (K[]) new Object[initialCapacity];
        this.values = (V[]) new Object[initialCapacity];
        this.size = 0;
    }

    public ArrayMap(Map<? extends K, ? extends V> map) {
        this(map.size());
        putAll(map);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean containsKey(Object key) {
        for (K k : keys) {
            if (k != null && k.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        for (V v : values) {
            if (v != null && v.equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != null && keys[i].equals(key)) {
                return values[i];
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        if (key == null) {
            throw new NullPointerException("ArrayMap does not support null keys");
        }

        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != null && keys[i].equals(key)) {
                V oldValue = values[i];
                values[i] = value;
                return oldValue;
            }
        }

        ensureCapacity(size + 1);
        keys[size] = key;
        values[size] = value;
        size++;
        return null;
    }

    @Override
    public V remove(Object key) {
        for (int i = 0; i < size; i++) {
            if (keys[i] != null && keys[i].equals(key)) {
                V oldValue = values[i];

                // Сдвигаем элементы влево
                for (int j = i; j < size - 1; j++) {
                    keys[j] = keys[j + 1];
                    values[j] = values[j + 1];
                }

                keys[size - 1] = null;
                values[size - 1] = null;
                size--;
                return oldValue;
            }
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        ensureCapacity(size + m.size());
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            keys[i] = null;
            values[i] = null;
        }
        size = 0;
    }

    /**
     * Возвращает ключ по индексу
     * @param index позиция (0-based)
     * @return ключ
     * @throws IndexOutOfBoundsException если индекс вне диапазона
     */
    public K getKey(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return keys[index];
    }

    /**
     * Возвращает значение по индексу
     * @param index позиция (0-based)
     * @return значение
     * @throws IndexOutOfBoundsException если индекс вне диапазона
     */
    public V getValue(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return values[index];
    }

    /**
     * Возвращает Entry (пару ключ-значение) по индексу
     * @param index позиция (0-based)
     * @return Entry
     * @throws IndexOutOfBoundsException если индекс вне диапазона
     */
    public Entry<K, V> getEntry(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return new ArrayMapEntry(keys[index], values[index], index);
    }

    /**
     * Устанавливает значение по индексу
     * @param index позиция (0-based)
     * @param value новое значение
     * @return старое значение
     * @throws IndexOutOfBoundsException если индекс вне диапазона
     */
    public V setValue(int index, V value) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        V oldValue = values[index];
        values[index] = value;
        return oldValue;
    }

    /**
     * Возвращает первый ключ в карте
     */
    public K firstKey() {
        return size == 0 ? null : keys[0];
    }

    /**
     * Возвращает последний ключ в карте
     */
    public K lastKey() {
        return size == 0 ? null : keys[size - 1];
    }

    /**
     * Возвращает первое значение в карте
     */
    public V firstValue() {
        return size == 0 ? null : values[0];
    }

    /**
     * Возвращает последнее значение в карте
     */
    public V lastValue() {
        return size == 0 ? null : values[size - 1];
    }

    /**
     * Удаляет элемент по индексу
     * @param index позиция (0-based)
     * @return удалённый Entry
     * @throws IndexOutOfBoundsException если индекс вне диапазона
     */
    public Entry<K, V> removeAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        Entry<K, V> removed = new ArrayMapEntry(keys[index], values[index], index);
        for (int i = index; i < size - 1; i++) {
            keys[i] = keys[i + 1];
            values[i] = values[i + 1];
        }

        keys[size - 1] = null;
        values[size - 1] = null;
        size--;

        return removed;
    }

    /**
     * Вставляет элемент на определённую позицию
     * @param index позиция (0-based)
     * @param key ключ
     * @param value значение
     */
    public void insertAt(int index, K key, V value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        // Если ключ уже есть — удаляем старый
        int existingIndex = indexOfKey(key);
        if (existingIndex != -1) {
            removeAt(existingIndex);
            // Корректируем индекс, если удалили элемент перед позицией вставки
            if (existingIndex < index) {
                index--;
            }
        }

        ensureCapacity(size + 1);

        for (int i = size; i > index; i--) {
            keys[i] = keys[i - 1];
            values[i] = values[i - 1];
        }

        keys[index] = key;
        values[index] = value;
        size++;
    }

    /**
     * Возвращает индекс ключа
     * @param key ключ
     * @return индекс или -1 если не найден
     */
    public int indexOfKey(K key) {
        for (int i = 0; i < size; i++) {
            if (keys[i].equals(key)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Возвращает индекс значения
     * @param value значение
     * @return индекс или -1 если не найден
     */
    public int indexOfValue(V value) {
        for (int i = 0; i < size; i++) {
            if (values[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    private void ensureCapacity(int minCapacity) {
        if (minCapacity <= keys.length) {
            return;
        }

        int newCapacity = (int) (keys.length * GROWTH_FACTOR);
        if (newCapacity < minCapacity) {
            newCapacity = minCapacity;
        }

        K[] newKeys = (K[]) new Object[newCapacity];
        V[] newValues = (V[]) new Object[newCapacity];

        System.arraycopy(keys, 0, newKeys, 0, size);
        System.arraycopy(values, 0, newValues, 0, size);

        this.keys = newKeys;
        this.values = newValues;
    }

    @Override
    public String toString() {
        if (size == 0) return "{}";

        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(keys[i]).append("=").append(values[i]);
        }
        return sb.append("}").toString();
    }

    @Override
    public @NotNull Set<K> keySet() {
        return new KeySet();
    }

    @Override
    public @NotNull Collection<V> values() {
        return new ValuesCollection();
    }

    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    private class KeySet extends AbstractSet<K> {
        @Override
        public int size() {
            return size;
        }

        @Override
        public @NotNull Iterator<K> iterator() {
            return new Iterator<K>() {
                private int current = 0;

                @Override
                public boolean hasNext() {
                    return current < size;
                }

                @Override
                public K next() {
                    if (!hasNext()) throw new NoSuchElementException();
                    return keys[current++];
                }

                @Override
                public void remove() {
                    if (current == 0) throw new IllegalStateException();
                    ArrayMap.this.removeAt(--current);
                }
            };
        }

        @Override
        public boolean contains(Object o) {
            return containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            int idx = indexOfKey((K) o);
            if (idx == -1) return false;
            ArrayMap.this.removeAt(idx);
            return true;
        }

        @Override
        public void clear() {
            ArrayMap.this.clear();
        }
    }

    private class ValuesCollection extends AbstractCollection<V> {
        @Override
        public int size() {
            return size;
        }

        @Override
        public @NotNull Iterator<V> iterator() {
            return new Iterator<>() {
                private int current = 0;

                @Override
                public boolean hasNext() {
                    return current < size;
                }

                @Override
                public V next() {
                    if (!hasNext()) throw new NoSuchElementException();
                    return values[current++];
                }

                @Override
                public void remove() {
                    if (current == 0) throw new IllegalStateException();
                    ArrayMap.this.removeAt(--current);
                }
            };
        }

        @Override
        public boolean contains(Object o) {
            return containsValue(o);
        }

        @Override
        public void clear() {
            ArrayMap.this.clear();
        }
    }

    private class EntrySet extends AbstractSet<Entry<K, V>> {
        @Override
        public int size() {
            return size;
        }

        @Override
        public @NotNull Iterator<Entry<K, V>> iterator() {
            return new Iterator<>() {
                private int current = 0;

                @Override
                public boolean hasNext() {
                    return current < size;
                }

                @Override
                public Entry<K, V> next() {
                    if (!hasNext()) throw new NoSuchElementException();
                    return new ArrayMapEntry(keys[current], values[current], current++);
                }

                @Override
                public void remove() {
                    if (current == 0) throw new IllegalStateException();
                    ArrayMap.this.removeAt(--current);
                }
            };
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Entry<?, ?> e)) return false;
            int idx = indexOfKey((K) e.getKey());
            return idx != -1 && Objects.equals(values[idx], e.getValue());
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Entry<?, ?> e)) return false;
            int idx = indexOfKey((K) e.getKey());
            if (idx == -1 || !Objects.equals(values[idx], e.getValue())) return false;
            ArrayMap.this.removeAt(idx);
            return true;
        }

        @Override
        public void clear() {
            ArrayMap.this.clear();
        }
    }

    private class ArrayMapEntry implements Entry<K, V> {
        private final K key;
        private V value;
        private final int index;  // для быстрого обновления

        ArrayMapEntry(K key, V value, int index) {
            this.key = key;
            this.value = value;
            this.index = index;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V newValue) {
            V oldValue = value;
            this.value = newValue;
            values[index] = newValue;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Entry<?, ?> e)) return false;
            return Objects.equals(key, e.getKey()) && Objects.equals(value, e.getValue());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }
}
