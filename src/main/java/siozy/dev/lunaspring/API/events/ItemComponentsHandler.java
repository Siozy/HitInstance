package siozy.dev.lunaspring.API.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import siozy.dev.lunaspring.API.items.ComponentStorage;
import siozy.dev.lunaspring.API.items.secondary.*;
import siozy.dev.lunaspring.API.items.task.TickableTask;
import siozy.dev.lunaspring.API.util.service.managers.TaskManager;
import siozy.dev.lunaspring.LunaSpring;

import java.util.UUID;

public class ItemComponentsHandler implements Listener {
    private final CooldownPrevent<UUID> cache = new CooldownPrevent<>(75);

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        ItemStack itemStack = e.getItemInHand();

        BlockPlaceItemComponent component = ComponentStorage.getComponent(itemStack, BlockPlaceItemComponent.class);
        if (component != null) {
            if (component.onPlace(e, itemStack)) e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand.getType().isAir()) return;

        ClickableItemComponent component = ComponentStorage.getComponent(hand, ClickableItemComponent.class);
        if (component == null || this.cache.isCancelled(e, player.getUniqueId())) return;

        if (component.onClick(e, hand)) e.setCancelled(true);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType().isAir()) return;

        boolean isCursored = false;
        InventoryClickItemComponent component = ComponentStorage.getComponent(item, InventoryClickItemComponent.class);
        if (component == null) {
            item = e.getCursor();
            if (item == null || item.getType().isAir()) return;

            component = ComponentStorage.getComponent(item, InventoryClickItemComponent.class);
            if (component == null) return;

            isCursored = true;
        }

        if (this.cache.isCancelled(e, player.getUniqueId())) return;

        component.onClick(e, item, isCursored);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        new TickableTask(player).runTaskLaterAsynchronously(LunaSpring.getInstance(), 30L);

        PlayerInventory playerInventory = player.getInventory();
        ComponentStorage.getRealizedComponents(JoinItemComponent.class).forEach(c -> {
            c.onJoin(player, ComponentStorage.scanInventory(playerInventory, c));
        });
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        TaskManager.get(TickableTask.class, t -> player.equals(t.getPlayer())).ifPresent(TickableTask::stop);

        PlayerInventory playerInventory = player.getInventory();
        ComponentStorage.getRealizedComponents(QuitItemComponent.class).forEach(c -> {
            c.onQuit(player, ComponentStorage.scanInventory(playerInventory, c));
        });
    }

    @EventHandler
    public void onPickup(PlayerAttemptPickupItemEvent e) {
        ItemStack itemStack = e.getItem().getItemStack();
        PickupItemComponent component = ComponentStorage.getComponent(itemStack, PickupItemComponent.class);
        if (component == null) return;

        if (this.cache.isCancelled(e, e.getPlayer().getUniqueId())) return;
        component.onPickup(e, itemStack);
    }
}
