package siozy.dev.lunaspring.API.util.utilities.lists;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import siozy.dev.lunaspring.API.util.utilities.LunaMath;
import siozy.dev.lunaspring.API.util.utilities.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class LunaList<E> extends ArrayList<E> {
    public LunaList(int initialCapacity) {
        super(initialCapacity);
    }

    public LunaList(@NotNull Collection<? extends E> c) {
        super(c);
    }

    public LunaList() {
        super();
    }

    public LunaList(Class<E> itemClass, String serializableData) {
        super(Utils.Base64.deserializeList(itemClass, serializableData));
    }

    @SafeVarargs
    public LunaList(E... elements) {
        this(List.of(elements));
    }

    @Deprecated(forRemoval = true)
    public LunaList<E> filter(Predicate<E> predicate) {
        this.removeIf(predicate);
        return this;
    }

    public Stream<E> s() {
        return this.stream();
    }

    public E first(Predicate<E> predicate, Supplier<E> orElse) {
        return Utils.find(this.s(), predicate).orElse(orElse.get());
    }

    public @Nullable E first(Predicate<E> predicate) {
        return this.first(predicate, () -> null);
    }

    public @Nullable E first() {
        return this.isEmpty() ? null : this.get(0);
    }

    public @Nullable E last() {
        return this.isEmpty() ? null : this.get(this.size() - 1);
    }

    public String serialize() {
        return Utils.Base64.serializeList(this);
    }

    public boolean contains(Predicate<E> predicate) {
        return this.first(predicate) != null;
    }

    public @Nullable E randomElement() {
        return LunaMath.getRandom(this);
    }

    public @NotNull E randomElement(Supplier<E> returner) {
        return LunaMath.getRandomIfPresent(this, returner);
    }
}
