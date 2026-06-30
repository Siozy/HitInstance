package siozy.dev.lunaspring.API.util.utilities.lists;

import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

@UtilityClass
public class LunaLists {
    @SafeVarargs
    public <E> LunaList<E> newList(E... elements) {
        return new LunaList<>(elements);
    }

    public <E> LunaList<E> newList(Collection<E> collection) {
        return new LunaList<>(collection);
    }

    public ItemStackList newStacks(ItemStack... itemStacks) {
        return new ItemStackList(itemStacks);
    }

    public ItemStackList newStacks(Collection<ItemStack> itemStacks) {
        return new ItemStackList(itemStacks);
    }

    public StringList newStrings(String... strings) {
        return new StringList(strings);
    }

    public StringList newStrings(Collection<String> strings) {
        return new StringList(strings);
    }
}
