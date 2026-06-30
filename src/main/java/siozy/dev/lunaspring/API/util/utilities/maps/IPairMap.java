package siozy.dev.lunaspring.API.util.utilities.maps;

public interface IPairMap<K, V> extends LunaMap<K, V> {
    int DEFAULT_CAPACITY = 16;
    float DEFAULT_LOAD_FACTOR = 0.75f;
    int MAXIMUM_CAPACITY = 1 << 30;

    void resize();

    default int indexFor(int hash, int length) {
        return hash & (length - 1);
    }
}
