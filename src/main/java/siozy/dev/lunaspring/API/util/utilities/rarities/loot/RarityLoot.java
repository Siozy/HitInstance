package siozy.dev.lunaspring.API.util.utilities.rarities.loot;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import siozy.dev.lunaspring.API.util.utilities.rarities.RarityManager;

import java.util.Collection;

public class RarityLoot extends InventoryLoot<ItemStack, StackRarity> {
    public RarityLoot(StackRarity rarity, Collection<ItemStack> collection, int maximumItems) {
        super(rarity, collection, maximumItems);
    }

    public RarityLoot(StackRarity rarity, int maximumItems) {
        super(rarity, maximumItems);
    }

    public RarityLoot(Collection<StackRarity> rarities, int maximumItems) {
        super(RarityManager.calculate(rarities), maximumItems);
    }

    @Override
    public void insert(Inventory inventory, int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    @Override
    protected void generate(StackRarity rarity) {
        for (int i = 0; i < this.getMaximumItems(); i++) {
            ItemStack stack = rarity.calculate();
            if (stack != null) this.add(stack);
        }
    }
}
