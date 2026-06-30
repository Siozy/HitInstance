package siozy.dev.lunaspring.API.util.utilities.rarities.loot;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import siozy.dev.lunaspring.API.util.utilities.LunaMath;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@Setter @Getter
public abstract class InventoryLoot<T, E> extends Loot<T, E> {
    private boolean mayDuplicate = true;
    public InventoryLoot(E object, Collection<T> collection, int maximumItems) {
        super(object, collection, maximumItems);
    }

    public InventoryLoot(E object, int maximumItems) {
        super(object, maximumItems);
    }

    public void insert(Inventory inventory) {
        List<T> list = mayDuplicate ? this.getList() : new ArrayList<>(this.getList());

        Set<Integer> leftSlots = new HashSet<>();
        for (int i = 0; i < inventory.getSize(); i++) leftSlots.add(i);

        for (byte i = 0; i < this.getMaximumItems(); i++) {
            if (list.isEmpty()) break;

            Integer slot = LunaMath.getRandom(leftSlots);
            if (slot == null) break;

            leftSlots.remove(slot);

            int index = ThreadLocalRandom.current().nextInt(list.size());
            T item = list.get(index);
            if (mayDuplicate) list.remove(index);

            this.insert(inventory, slot, item);
        }
    }

    public abstract void insert(Inventory inventory, int slot, T item);
}
