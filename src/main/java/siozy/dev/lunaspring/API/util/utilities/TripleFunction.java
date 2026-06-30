package siozy.dev.lunaspring.API.util.utilities;

@FunctionalInterface
public interface TripleFunction<B, D, S, M> {
    M apply(B b, D d, S s);
}
