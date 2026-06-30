package siozy.dev.lunaspring.API.util.utilities.lists;

import org.jetbrains.annotations.NotNull;
import siozy.dev.lunaspring.API.util.utilities.Utils;

import java.util.Collection;
import java.util.List;

public class StringList extends LunaList<String> {
    public StringList(int initialCapacity) {
        super(initialCapacity);
    }

    public StringList(@NotNull Collection<? extends String> c) {
        super(c);
    }

    public StringList() {}

    public StringList(String serializableData) {
        super(String.class, serializableData);
    }

    public StringList(String... elements) {
        super(elements);
    }

    public List<String> tabComplete(String argument) {
        return Utils.tabCompleterFiltering(this, argument);
    }

    public boolean containsCased(String element) {
        return this.first(s -> s.equalsIgnoreCase(element)) != null;
    }

    public boolean containsStarted(String element) {
        return this.first(s -> s.startsWith(element)) != null;
    }

    public boolean containsEnded(String element) {
        return this.first(s -> s.endsWith(element)) != null;
    }
}
