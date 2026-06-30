package siozy.dev.lunaspring.API.util.utilities.rarities.loot;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import siozy.dev.lunaspring.API.util.utilities.LunaMath;
import siozy.dev.lunaspring.API.util.utilities.rarities.RarityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SectionLoot extends InventoryLoot<ItemStack, ConfigurationSection> {
    public SectionLoot(ConfigurationSection raritiesSection, int maximumItems) {
        super(raritiesSection, maximumItems);
    }

    @Override
    public void insert(Inventory inventory, int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    @Override
    protected void generate(ConfigurationSection section) {
        Set<String> keys = section.getKeys(false);

        String calculated = RarityManager.calculate(keys, k -> section.getDouble(k + ".chance"));
        if (calculated == null) return;

        ConfigurationSection items = section.getConfigurationSection(calculated + ".items");
        if (items == null) return;

        List<String> itemKeys = new ArrayList<>(items.getKeys(false));
        if (!itemKeys.isEmpty())
            for (int i = 0; i < this.getMaximumItems(); i++) {
                String key = itemKeys.get(LunaMath.getRandomInt(0, itemKeys.size()));

                ConfigurationSection itemSection = items.getConfigurationSection(key);
                if (itemSection == null) continue;

                this.add(Loot.getStackFromSection(itemSection));
            }
    }
}
