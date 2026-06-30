package siozy.dev.lunaspring.API.menus.items;

import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import siozy.dev.lunaspring.API.util.utilities.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Decoration extends ArrayList<Item> implements Cloneable {
    public Decoration(ConfigurationSection decorationSection, Inventory inventory) {
        initialize(decorationSection, inventory);
    }

    public Decoration(List<Item> items) {
        super(items);
    }

    public Decoration() {
    }

    public void initialize(ConfigurationSection decorationSection, Inventory inventory) {
        if (decorationSection == null) return;
        boolean fillType = decorationSection.getBoolean("fillType.enabled");

        if (fillType) {
            ConfigurationSection section = decorationSection.getConfigurationSection("fillType.item");

            assert section != null;
            for (int i = 0; i < inventory.getSize(); i++) {
                if (inventory.getItem(i) == null) this.add(new Item(section, i));
            }
        }
        else {
            for (String key : decorationSection.getKeys(false)) {
                ConfigurationSection itemSection = decorationSection.getConfigurationSection(key);
                assert itemSection != null;

                List<String> slots = itemSection.getStringList("slots");
                this.addAll(Utils.getItems(itemSection, slots));
            }
        }
    }

    @Deprecated
    public List<Item> getDecorationItems() {
        return this;
    }

    public void insert(Inventory inventory) {
        this.forEach(i -> inventory.setItem(i.getSlot(), i.getItemStack()));
    }

    public int getDecorationsAmount() {
        return this.size();
    }

    public boolean checkSlot(byte slot) {
        return this.stream().anyMatch(i -> i.getSlot() == slot);
    }

    public boolean checkMaterial(Material material) {
        return this.stream().anyMatch(i -> i.getMaterial().equals(material));
    }

    public boolean checkItemStack(ItemStack itemStack) {
        return this.stream().anyMatch(i -> i.getItemStack().equals(itemStack));
    }

    public boolean checkAll(ItemStack itemStack, byte slot) {
        return this.stream().anyMatch(i -> i.getItemStack().equals(itemStack) && i.getSlot() == slot);
    }

    public boolean checkAll(Material material, byte slot) {
        return this.stream().anyMatch(i -> i.getMaterial() == material && i.getSlot() == slot);
    }

    public Set<Byte> getInsertedSlots() {
        return this
                .stream()
                .filter(i -> i.getMenu() != null)
                .map(Item::getSlot)
                .collect(Collectors.toSet());
    }

    @Override
    @SneakyThrows
    public Decoration clone() {
        return (Decoration) super.clone();
    }
}
