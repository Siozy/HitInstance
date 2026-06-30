package siozy.dev.lunaspring.API.util.utilities.maps;

import lombok.Getter;

import java.util.Objects;

/**
 * Неизменяемая пара из двух значений.
 * Используется как составной ключ в PairMap.
 *
 * @param <K1> тип первого элемента
 * @param <K2> тип второго элемента
 */
@Getter
public class Pair<K1, K2> {
    private final K1 first;
    private final K2 second;

    private Pair(K1 first, K2 second) {
        this.first = first;
        this.second = second;
    }

    public static <K1, K2> Pair<K1, K2> of(K1 first, K2 second) {
        return new Pair<>(first, second);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair<?, ?> pair)) return false;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "Pair{" + first + ", " + second + "}";
    }
}