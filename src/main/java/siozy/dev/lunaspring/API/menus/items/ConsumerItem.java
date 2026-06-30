package siozy.dev.lunaspring.API.menus.items;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.List;
import java.util.function.BiConsumer;

public class ConsumerItem extends Item {
    private final BiConsumer<Player, InventoryClickEvent> consumer;
    public ConsumerItem(Material material, String displayName, List<String> lore, int amount, @Range(from = 0, to = 53) byte slot, BiConsumer<Player, InventoryClickEvent> consumer) {
        super(material, displayName, lore, amount, slot);
        this.consumer = consumer;
    }

    public ConsumerItem(Material material, int amount, BiConsumer<Player, InventoryClickEvent> consumer) {
        super(material, amount);
        this.consumer = consumer;
    }

    public ConsumerItem(NonMenuItem nonMenuItem, @Range(from = 0, to = 53) byte slot, BiConsumer<Player, InventoryClickEvent> consumer) {
        super(nonMenuItem, slot);
        this.consumer = consumer;
    }

    public ConsumerItem(BiConsumer<Player, InventoryClickEvent> consumer) {
        super();
        this.consumer = consumer;
    }

    public ConsumerItem(Material material, BiConsumer<Player, InventoryClickEvent> consumer) {
        super(material);
        this.consumer = consumer;
    }

    public ConsumerItem(Material material, @Range(from = 0, to = 53) byte slot, BiConsumer<Player, InventoryClickEvent> consumer) {
        super(material, slot);
        this.consumer = consumer;
    }

    public ConsumerItem(@NotNull ConfigurationSection section, @Range(from = 0, to = 53) int slot, BiConsumer<Player, InventoryClickEvent> consumer) {
        super(section, slot);
        this.consumer = consumer;
    }

    public ConsumerItem(@NotNull ConfigurationSection section, boolean rowCol, BiConsumer<Player, InventoryClickEvent> consumer) {
        super(section, rowCol);
        this.consumer = consumer;
    }

    public ConsumerItem(@NotNull ConfigurationSection section, BiConsumer<Player, InventoryClickEvent> consumer) {
        super(section);
        this.consumer = consumer;
    }

    @Override
    public ConsumerItem onClick(InventoryClickEvent e) {
        this.consumer.accept((Player) e.getWhoClicked(), e);
        return this;
    }
}
