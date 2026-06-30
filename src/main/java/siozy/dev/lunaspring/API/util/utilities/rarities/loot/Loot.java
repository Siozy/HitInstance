package siozy.dev.lunaspring.API.util.utilities.rarities.loot;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import siozy.dev.lunaspring.API.menus.items.NonMenuItem;

import java.util.Collection;
import java.util.List;

@Getter
public abstract class Loot<T, E> {
    private final List<T> list = Lists.newArrayList();
    private final E object;
    private final int maximumItems;
    public Loot(E object, @Nullable Collection<T> collection, int maximumItems) {
        if (collection != null) this.list.addAll(collection);
        this.object = object;
        this.maximumItems = maximumItems;
        this.generate(object);
    }

    public Loot(E object, int maximumItems) {
        this.object = object;
        this.maximumItems = maximumItems;
        this.generate(object);
    }

    public void add(@NotNull T t) {
        this.list.add(t);
    }

    public void remove(@NotNull T t) {
        this.list.remove(t);
    }

    protected abstract void generate(E object);

    public static ItemStack getStackFromSection(@NotNull ConfigurationSection itemSection) {
        ItemStack itemStack = itemSection.getItemStack("item");
        if (itemStack == null) {
            return new NonMenuItem(itemSection).getItemStack();
        }
        else
            itemStack = itemStack.clone();

        return itemStack;
    }
}
