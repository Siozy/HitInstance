package siozy.dev.lunaspring.API.util.utilities;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface TripleConsumer<S, E, X> {
    void accept(S s, E e, X x);
    default TripleConsumer<S, E, X> andThen(@NotNull TripleConsumer<? super S, ? super E, ? super X> after) {
        return (S s, E e, X x) -> {
            accept(s, e, x);
            after.accept(s, e, x);
        };
    }
}
