package siozy.dev.lunaspring.API.menus;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import siozy.dev.lunaspring.API.events.CooldownPrevent;
import siozy.dev.lunaspring.API.menus.items.Decoration;
import siozy.dev.lunaspring.API.menus.items.Item;
import siozy.dev.lunaspring.API.util.service.managers.ColorManager;
import siozy.dev.lunaspring.API.util.utilities.Utils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
@Getter
@SuppressWarnings({"deprecation", "unused"})
public abstract class AMenu implements ItemListMenu {
    private Inventory inventory;
    private String title;
    @Setter private Decoration decoration;
    @Setter private CooldownPrevent<Integer> cooldownPrevent = new CooldownPrevent<>();
    private Player player;

    private List<Item> itemList = new ArrayList<>();
    public AMenu(@NotNull Player player, String title, @Range(from = 9L, to=54) byte size) {
        this.player = player;
        this.title = title;
        this.inventory = Bukkit.createInventory(this.player, size, Utils.color(title));
    }

    public AMenu(@NotNull Player player, String title, InventoryType type) {
        this.player = player;
        this.title = title;
        this.inventory = Bukkit.createInventory(this.player, type, Utils.color(title));
    }

    public AMenu(@NotNull Player player, ConfigurationSection menuSection) {
        this.player = player;
        this.title = menuSection.getString("title");

        InventoryType type = Utils.getEnumValue(InventoryType.class, menuSection.getString("type"));
        if (type == null) this.inventory = Bukkit.createInventory(this.player, Math.min(menuSection.getInt("size"), 54), ColorManager.color(this.title));
        else this.inventory = Bukkit.createInventory(this.player, type, ColorManager.color(this.title));

        this.decoration = new Decoration(Objects.requireNonNull(menuSection.getConfigurationSection("decoration")), this.inventory);
        this.decoration.insert(this.inventory);
    }

    public AMenu(@NotNull Player player, String title, @Range(from = 9L, to=54) byte size, ConfigurationSection decorSection) {
        this.player = player;
        this.title = title;
        this.inventory = Bukkit.createInventory(this.player, size, ColorManager.color(title));
        this.decoration = new Decoration(decorSection, this.inventory);
        this.decoration.insert(this.inventory);
    }

    public AMenu(@NotNull Player player, String title, InventoryType type, ConfigurationSection decorSection) {
        this.player = player;
        this.title = title;
        this.inventory = Bukkit.createInventory(this.player, type, ColorManager.color(title));
        this.decoration = new Decoration(decorSection, this.inventory);
        this.decoration.insert(this.inventory);
    }

    public AMenu(@NotNull Player player) {
        this.player = player;
    }

    public int getSize() {
        return this.inventory.getSize();
    }

    public void setClickCooldown(long time, TimeUnit timeUnit) {
        this.cooldownPrevent = new CooldownPrevent<>(time, timeUnit);
    }

    public void setClickCooldown(int timeMillis) {
        this.cooldownPrevent = new CooldownPrevent<>(timeMillis);
    }

    @Override
    public boolean isCancelled(@Nullable Cancellable event, int slot) {
        return this.cooldownPrevent.isCancelled(event, slot);
    }

    public void initialize(ConfigurationSection section, boolean decorate) {
        String title = section.getString("title");
        ConfigurationSection decoration = section.getConfigurationSection("decoration");

        InventoryType type = Utils.getEnumValue(InventoryType.class, section.getString("type"));
        if (type == null) this.initialize(title, (byte) section.getInt("size"), decoration, decorate);
        else this.initialize(title, type, decoration, decorate);
    }

    public void initialize(ConfigurationSection section) {
        this.initialize(section, true);
    }

    public void initialize(String title, byte size, ConfigurationSection decorSection, boolean decorate) {
        this.inventory = Bukkit.createInventory(this.player, size, ColorManager.color(title));
        this.title = title;
        if (decorate) {
            this.decoration = new Decoration(decorSection, this.inventory);
            this.decoration.insert(this.inventory);
        }
    }

    public void initialize(String title, byte size, ConfigurationSection decorSection) {
        this.initialize(title, size, decorSection, true);
    }

    public void initialize(String title, InventoryType type, ConfigurationSection decorSection, boolean decorate) {
        this.inventory = Bukkit.createInventory(this.player, type, ColorManager.color(title));
        this.title = title;
        if (decorate) {
            this.decoration = new Decoration(decorSection, this.inventory);
            this.decoration.insert(this.inventory);
        }
    }

    public void initialize(String title, InventoryType type, ConfigurationSection decorSection) {
        this.initialize(title, type, decorSection, true);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (this.getDecoration() != null &&
                this.getDecoration().checkAll(clickedItem, (byte) event.getSlot())) {
            event.setCancelled(true);
            return;
        }

        if (clickedItem != null) {
            this.itemClick(event);
        }
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
        event.setCancelled(event.getRawSlots().stream().anyMatch(s -> s < inventory.getSize()));
    }

    @Override
    public void clear() {
        this.itemList.clear();
    }

    @Override
    public List<Item> findItems(ItemStack itemStack) {
        return this.itemList.stream().filter(i -> i.getItemStack().equals(itemStack)).collect(Collectors.toList());
    }

    @Override
    public Item findFirstItem(ItemStack itemStack) {
        return Utils.find(this.itemList, i -> i.getItemStack().equals(itemStack)).orElse(null);
    }

    @Override
    public List<Item> findItems(Material material) {
        return this.itemList.stream().filter(i -> i.getItemStack().getType().equals(material)).collect(Collectors.toList());
    }

    @Override
    public Item findFirstItem(Class<?> clazz) {
        return Utils.find(this.itemList, i -> i.getClass().equals(clazz)).orElse(null);
    }

    @Override
    public List<Item> findItems(Class<?> clazz) {
        return this.itemList.stream().filter(i -> i.getClass().equals(clazz)).collect(Collectors.toList());
    }

    @Override
    public Item findFirstItem(String displayName) {
        return Utils.find(this.itemList, i -> i.getDisplayName().equals(displayName)).orElse(null);
    }

    @Override
    public List<Item> findItems(String displayName) {
        return this.itemList.stream().filter(i -> i.getDisplayName().equals(displayName)).collect(Collectors.toList());
    }

    @Override
    public Item findFirstItem(Material material) {
        return Utils.find(this.itemList, i -> i.getMaterial().equals(material)).orElse(null);
    }

    @Override
    public Item findFirstItem(int slot) {
        return Utils.find(this.itemList, i -> i.getSlot() == slot).orElse(null);
    }

    @Override
    public Item findFirstItem(ItemStack itemStack, int slot) {
        return Utils.find(this.itemList, i -> i.getItemStack().equals(itemStack) &&
                i.getSlot() == slot).orElse(null);
    }

    @Override
    public boolean itemClick(@NotNull Material material, InventoryClickEvent event) {
        Item item = this.findFirstItem(material);
        if (item != null) {
            item.onClick(event);
            return true;
        }
        return false;
    }

    @Override
    public boolean itemClick(@NotNull String displayName, InventoryClickEvent event) {
        Item item = this.findFirstItem(displayName);
        if (item != null) {
            item.onClick(event);
            return true;
        }
        return false;
    }

    @Override
    public boolean itemClick(@NonNull Class<?> clazz, InventoryClickEvent event) {
        Item item = this.findFirstItem(clazz);
        if (item != null) {
            item.onClick(event);
            return true;
        }
        return false;
    }

    @Override
    public boolean itemClick(@NonNull ItemStack itemStack, InventoryClickEvent event) {
        Item item = this.findFirstItem(itemStack);
        if (item != null) {
            item.onClick(event);
            return true;
        }
        return false;
    }

    @Override
    public boolean itemClick(int slot, InventoryClickEvent event) {
        Item item = this.findFirstItem(slot);
        if (item != null) {
            item.onClick(event);
            return true;
        }
        return false;
    }

    @Override
    public boolean itemClick(@NotNull ItemStack itemStack, int slot, InventoryClickEvent event) {
        Item item = this.findFirstItem(itemStack, slot);
        if (item != null) {
            item.onClick(event);
            return true;
        }
        return false;
    }

    @Override
    public boolean itemClick(@NotNull InventoryClickEvent event) {
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null || itemStack.getType().isAir()) return false;

        Item item = this.findFirstItem(itemStack, event.getSlot());
        if (item != null) {
            item.onClick(event);
            return true;
        }
        return false;
    }

    @Override
    public Collection<Item> insertAll() {
        this.itemList.forEach(i -> i.insert(this));
        return this.itemList;
    }

    @Override
    public void addItems(boolean insert, Item... items) {
        List<Item> itemList = List.of(items);
        this.itemList.addAll(itemList);
        if (insert) itemList.forEach(i -> i.insert(this));
    }

    @Override
    public Collection<Item> addItems(Collection<Item> items, boolean insert) {
        this.itemList.addAll(items);
        if (insert) {
            items.forEach(i -> i.insert(this));
        }
        return this.itemList;
    }

    public void logItems() {
        this.itemList.forEach(System.out::println);
    }

    @Override
    public String toString() {
        return "AMenu{" +
                "inventory=" + inventory + '\n' +
                ", title='" + title + '\n' +
                ", cooldownPrevent=" + cooldownPrevent + '\n' +
                ", player=" + player + '\n' +
                ", itemList=" + itemList +
                '}';
    }

    public AMenu copy(Player player) {
        AMenu newMenu = this.clone();
        newMenu.player = player;
        return newMenu;
    }

    @Override
    @SneakyThrows
    public AMenu clone() {
        AMenu aMenu = (AMenu) super.clone();
        if (this.decoration != null) aMenu.decoration = this.decoration.clone();
        aMenu.inventory = this.inventory;
        if (this.cooldownPrevent != null) aMenu.cooldownPrevent = this.cooldownPrevent.clone();
        aMenu.itemList = new ArrayList<>();
        return aMenu;
    }
}
