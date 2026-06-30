package siozy.dev.lunaspring.API.util.utilities.maps;

import java.util.Map;

public interface LunaMap<K, V> extends Map<K, V> {
    @Override
    default boolean isEmpty() {
        return size() == 0;
    }
}
