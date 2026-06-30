package siozy.dev.lunaspring.API.events;

import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.event.Cancellable;
import siozy.dev.lunaspring.API.util.utilities.Cache;

import java.util.concurrent.TimeUnit;


/**
 * CooldownPrevent нужен для создания задержек между разными событиями (например, клик по слоту инвентаря и т.д.).
 * @param <T> - класс по которому определяется задержка.
 */
@Getter
public class CooldownPrevent<T> implements Cloneable {
    private Cache<T, Long> cache;
    private long cooldown;

    public CooldownPrevent() {
        this(0);
    }

    public CooldownPrevent(int cooldownMS) {
        this(cooldownMS, TimeUnit.MILLISECONDS);
    }

    public CooldownPrevent(long cacheTTL, TimeUnit cacheUnit) {
        if (cacheUnit != TimeUnit.MILLISECONDS) cacheTTL = TimeUnit.MILLISECONDS.convert(cacheTTL, cacheUnit);
        this.cache = new Cache<>(cacheTTL, TimeUnit.MILLISECONDS);
        this.cooldown = cacheTTL;
    }

    @Builder
    public CooldownPrevent(long cacheTTL, TimeUnit cacheUnit, long maximumSize) {
        if (cacheUnit != TimeUnit.MILLISECONDS) cacheTTL = TimeUnit.MILLISECONDS.convert(cacheTTL, cacheUnit);
        this.cache = new Cache<>(cacheTTL, cacheUnit, maximumSize);
        this.cooldown = cacheTTL;
    }

    public long getRemaining(T object) {
        if (this.contains(object)) {
            return this.cache.get(object, v -> System.currentTimeMillis()) - System.currentTimeMillis();
        }

        return 0L;
    }

    public void setCooldown(long value) {
        this.cooldown = value;
        this.cache = this.cache.duplicate(value, TimeUnit.MILLISECONDS);
    }

    public void remove(T key) {
        this.cache.remove(key);
    }

    /**
     * Возвращает true если определилось то, что object всё еще недоступен, и false если время задержки истекло или для object нет задержек
     * (если время задержки не 0, то при получении false, для object создаётся задержка).
     * Если вернуло true и event не равен null - будет автоматическая отмена самого ивента, если в этом нет необходимости - передавайте в первый аргумент null.
     * @param event ивент, требующий отмены.
     * @param object объект для проверки на задержку.
     */
    public boolean isCancelled(Cancellable event, T object) {
        if (this.cache.getTtl() <= 0) return false;

        if (this.contains(object)) {
            if (event != null) event.setCancelled(true);
            return true;
        } else {
            this.put(object);
            return false;
        }
    }

    public boolean contains(T object) {
        return this.cache.getIfPresent(object) != null;
    }

    public void put(T object) {
        this.cache.put(object, System.currentTimeMillis() + this.cooldown);
    }

    @Override
    @SuppressWarnings("unchecked")
    @SneakyThrows
    public CooldownPrevent<T> clone() {
        CooldownPrevent<T> cooldownPrevent = (CooldownPrevent<T>) super.clone();
        cooldownPrevent.cache = this.cache.duplicate();
        return cooldownPrevent;
    }
}
