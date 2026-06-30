package siozy.dev.lunaspring.API.menus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;
import siozy.dev.lunaspring.API.menus.updatable.UpdatableIMenu;
import siozy.dev.lunaspring.API.menus.updatable.tasks.UpdatableTask;
import siozy.dev.lunaspring.LunaSpring;

public interface IMenu extends Cloneable {
    default boolean onCloseFirstUnregisterFlag() {
        return false;
    }
    Inventory getInventory();
    Player getPlayer();
    void onOpen(InventoryOpenEvent event);
    void onClick(InventoryClickEvent event);
    default void onClose(InventoryCloseEvent event) {
        if (IMenu.this instanceof UpdatableIMenu updatableMenu) {
            UpdatableTask task = updatableMenu.getRunnable();
            if (task != null) task.cancel();
        }
    }
    void onDrag(InventoryDragEvent event);
    boolean isCancelled(Cancellable event, int slot);
    default IMenu open() {
        MenuManager.openInventory(this);
        return this;
    }
    default IMenu open(int afterTick) {
        Bukkit.getScheduler().runTaskLater(LunaSpring.getInstance(), () -> open(), afterTick);
        return this;
    }
    default void close(@Nullable InventoryCloseEvent.Reason reason) {
        Player player = getPlayer();
        if (player != null) {
            if (reason != null) player.closeInventory(reason);
            else player.closeInventory();
        }
    }
    default void close() {
        close(null);
    }
}
