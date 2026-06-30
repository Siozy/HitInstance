package siozy.dev.lunaspring.API.menus;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import siozy.dev.lunaspring.API.menus.items.Item;

import java.util.ArrayList;
import java.util.List;

public abstract class PageMenu<T> extends AMenu {
    protected final List<List<T>> items = new ArrayList<>();
    @Getter @Setter private int page;
    @Getter @Setter private List<Integer> itemsOrder;
    public PageMenu(Player player, List<Integer> itemsOrder) {
        super(player);
        this.itemsOrder = itemsOrder;
        this.page = 1;
        if (this.itemsOrder.isEmpty()) {
            throw new RuntimeException("Список слотов кнопок не может быть пустым!");
        }
    }

    public PageMenu(@NotNull Player player, String title, @Range(from = 9L, to = 54) byte size, List<Integer> itemsOrder) {
        super(player, title, size);
        this.page = 1;
        this.itemsOrder = itemsOrder;
    }

    public PageMenu(@NotNull Player player, ConfigurationSection menuSection, List<Integer> itemsOrder) {
        super(player, menuSection);
        this.page = 1;
        this.itemsOrder = itemsOrder;
    }

    public PageMenu(@NotNull Player player, String title, @Range(from = 9L, to = 54) byte size, ConfigurationSection decorSection, List<Integer> itemsOrder) {
        super(player, title, size, decorSection);
        this.page = 1;
        this.itemsOrder = itemsOrder;
    }

    public void partition(List<T> classifiedItems) {
        for (int i = 0; i < classifiedItems.size(); i += itemsOrder.size()) {
            int end = Math.min(i + itemsOrder.size(), classifiedItems.size());
            this.items.add(classifiedItems.subList(i, end));
        }
    }

    public abstract void reloadPage(int page);

    public class NextButton extends Item {
        public NextButton(ConfigurationSection section, boolean rowCol) {
            super(section, rowCol);
        }

        public NextButton(Material material, String displayName, List<String> lore, int amount, @Range(from = 0, to = 53) byte slot) {
            super(material, displayName, lore, amount, slot);
        }

        @Override
        public Item onClick(InventoryClickEvent event) {
            PageMenu.this.reloadPage(PageMenu.this.page + 1);
            return this;
        }
    }

    public class PreviousButton extends Item {
        public PreviousButton(ConfigurationSection section, boolean rowCol) {
            super(section, rowCol);
        }

        public PreviousButton(Material material, String displayName, List<String> lore, int amount, @Range(from = 0, to = 53) byte slot) {
            super(material, displayName, lore, amount, slot);
        }

        @Override
        public Item onClick(InventoryClickEvent event) {
            PageMenu.this.reloadPage(PageMenu.this.page - 1);
            return this;
        }
    }
}
