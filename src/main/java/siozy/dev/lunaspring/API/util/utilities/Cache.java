package siozy.dev.lunaspring.API.util.utilities;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.PolyNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Getter
public class Cache<K, V> {
    private final com.github.benmanes.caffeine.cache.Cache<K, V> cache;
    private final long ttl;
    private final TimeUnit unit;
    private final long maximumSize;
    public Cache(long ttl, TimeUnit ttlUnit, long maximumSize) {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder().expireAfterWrite(ttl, ttlUnit);
        if (maximumSize >= 0) {
            this.cache = caffeine.maximumSize(maximumSize).build();
        }
        else {
            this.cache = caffeine.build();
        }

        this.ttl = ttl;
        this.unit = ttlUnit;
        this.maximumSize = maximumSize < 0 ? -1 : maximumSize;
    }

    public Cache(long ttl, TimeUnit ttlUnit) {
        this(ttl, ttlUnit, -1);
    }

    public Cache(Cache<K, V> forCloningCache) {
        this(forCloningCache.ttl, forCloningCache.unit, forCloningCache.maximumSize);
    }

    public ConcurrentMap<K, V> toMap() {
        return this.cache.asMap();
    }

    public @PolyNull V get(K key, Function<? super K, ? extends V> function) {
        return this.cache.get(key, function);
    }

    public @Nullable V getIfPresent(K key) {
        return this.cache.getIfPresent(key);
    }

    public void put(K key, V value) {
        this.cache.put(key, value);
    }

    public void remove(K key) {
        this.cache.invalidate(key);
    }

    public void clear() {
        this.cache.invalidateAll();
    }

    public void cleanUp() {
        this.cache.cleanUp();
    }

    public Cache<K, V> duplicate(long cacheTTL, TimeUnit timeUnit, long maximumSize) {
        Cache<K, V> cloned = new Cache<>(cacheTTL, timeUnit, maximumSize);
        for (Map.Entry<K, V> entry : this.toMap().entrySet()) {
            cloned.put(entry.getKey(), entry.getValue());
        }
        return cloned;
    }

    public Cache<K, V> duplicate(long cacheTTL, TimeUnit timeUnit) {
        return this.duplicate(cacheTTL, timeUnit, this.maximumSize);
    }

    public Cache<K, V> duplicate() {
        return this.duplicate(this.ttl, this.unit, this.maximumSize);
    }
}
