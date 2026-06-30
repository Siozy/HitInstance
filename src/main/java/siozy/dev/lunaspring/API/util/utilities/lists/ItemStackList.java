package siozy.dev.lunaspring.API.util.utilities.lists;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import siozy.dev.lunaspring.API.util.utilities.Utils;

import java.util.Collection;

public class ItemStackList extends LunaList<ItemStack> {
    public ItemStackList(int initialCapacity) {
        super(initialCapacity);
    }

    public ItemStackList(ItemStack... elements) {
        super(elements);
    }

    public ItemStackList(@NotNull Collection<? extends ItemStack> c) {
        super(c);
    }

    public ItemStackList() {
        super();
    }

    public ItemStackList(String serializableData) {
        super(ItemStack.class, serializableData);
    }

    public ItemStackList give(Inventory inventory) {
        this.forEach(inventory::addItem);
        return this;
    }

    public ItemStackList smartGive(Player player, boolean putOnArmor) {
        Utils.Items.give(player, this, putOnArmor);
        return this;
    }

    public ItemStackList smartGive(Player player) {
        return this.smartGive(player, true);
    }

    public ItemStack first(Material material) {
        return this.first(i -> i != null && i.getType() == material);
    }
}
