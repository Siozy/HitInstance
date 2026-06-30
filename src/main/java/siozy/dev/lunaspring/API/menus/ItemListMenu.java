package siozy.dev.lunaspring.API.menus;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import siozy.dev.lunaspring.API.menus.items.Item;

import java.util.Collection;
import java.util.List;

public interface ItemListMenu extends IMenu {
    List<Item> getItemList();
    Collection<Item> findItems(ItemStack itemStack);
    Collection<Item> findItems(Material material);
    Collection<Item> findItems(Class<?> clazz);
    Collection<Item> findItems(String displayName);

    Item findFirstItem(ItemStack itemStack);
    Item findFirstItem(Class<?> clazz);
    Item findFirstItem(String displayName);
    Item findFirstItem(Material material);
    Item findFirstItem(int slot);
    Item findFirstItem(ItemStack itemStack, int slot);

    boolean itemClick(@NotNull Material material, InventoryClickEvent event);
    boolean itemClick(@NotNull String displayName, InventoryClickEvent event);
    boolean itemClick(@NotNull Class<?> clazz, InventoryClickEvent event);
    boolean itemClick(@NotNull ItemStack itemStack, InventoryClickEvent event);
    boolean itemClick(int slot, InventoryClickEvent event);
    boolean itemClick(@NotNull ItemStack itemStack, int slot, InventoryClickEvent event);
    boolean itemClick(@NotNull InventoryClickEvent event);

    void addItems(boolean insert, Item... items);
    Collection<Item> addItems(Collection<Item> items, boolean insert);

    Collection<Item> insertAll();
    void clear();
}
