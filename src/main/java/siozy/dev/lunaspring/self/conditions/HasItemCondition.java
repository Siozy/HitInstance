package siozy.dev.lunaspring.self.conditions;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import siozy.dev.lunaspring.API.conditions.abs.Condition;
import siozy.dev.lunaspring.API.conditions.abs.ConditionId;
import siozy.dev.lunaspring.API.conditions.abs.ConditionParams;
import siozy.dev.lunaspring.API.util.service.managers.ColorManager;
import siozy.dev.lunaspring.API.util.service.managers.NBTManager;
import siozy.dev.lunaspring.API.util.utilities.Utils;

import java.util.HashSet;
import java.util.List;

@ConditionId("HAS_ITEM")
@ConditionParams(
        identifiers = {"material", "amount", "displayName", "lore", "modelData", "nbt"},
        idClasses = {String.class, int.class, String.class, List.class, int.class, List.class})
public class HasItemCondition implements Condition<Inventory> {
    // MATERIAL AMOUNT DISPLAYNAME LORE MODEL_DATA NBT_KEYS

    @Override
    public boolean check(Inventory inventory, Object... objects) {
        if (objects.length == 0) return false;

        Material material = Material.matchMaterial((String) objects[0]);
        int amount = objects.length > 1 && objects[1] != null ? (int) objects[1] : 1;
        String displayName = objects.length > 2 && objects[2] != null ? (String) objects[2] : null;
        List<String> lore = objects.length > 3 && objects[3] != null ? (List<String>) objects[3] : null;
        int modelData = objects.length > 4 && objects[4] != null ? (int) objects[4] : -1;
        List<String> nbts = objects.length > 5 && objects[5] != null ? (List<String>) objects[5] : null;

        ItemStack[] storage = Utils.Items.getStorage(inventory);
        int get = Utils.Items.getAmount(storage, i -> {
            if (i.getType() != material) return false;

            ItemMeta meta = i.getItemMeta();
            if (meta == null) return displayName == null && lore == null && modelData == -1;

            if (displayName != null) {
                if (!meta.getDisplayName().equalsIgnoreCase(displayName)) return false;
            }

            if (lore != null) {
                if (!meta.hasLore()) return false;

                List<String> itemLore = meta.getLore();
                if (!new HashSet<>(lore).containsAll(itemLore)) return false;
            }

            if (modelData != -1 && meta.getCustomModelData() != modelData) return false;

            if (nbts != null) {
                for (String nbt : nbts) {
                    if (!NBTManager.hasTag(i, nbt)) return false;
                }
            }

            return true;
        });

        return get >= amount;
    }

    @Override
    public Object[] generateObjects(ConfigurationSection section) {
        return new Object[]{
                section.getString("material"),
                section.getInt("amount", -1),
                ColorManager.color(section.getString("displayName")),
                section.getStringList("lore"),
                section.getInt("modelData", -1),
                section.getStringList("nbt"),
        };
    }

    @Override
    public Inventory cast(OfflinePlayer player) {
        return player instanceof HumanEntity e ? e.getInventory() : null;
    }
}
