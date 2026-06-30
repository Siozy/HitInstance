package siozy.dev.lunaspring.API.events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import siozy.dev.lunaspring.API.menus.MenuManager;
import siozy.dev.lunaspring.API.menus.items.Item;
import siozy.dev.lunaspring.LunaSpring;

public class MarkedItemEraserHandler implements Listener {
    @EventHandler
    private void onPickup(EntityPickupItemEvent event) {
        org.bukkit.entity.Item item = event.getItem();
        if (!Item.isMarkered(item.getItemStack())) return;

        item.remove();
    }

    @EventHandler
    private void onDrop(PlayerDropItemEvent event) {
        org.bukkit.entity.Item item = event.getItemDrop();
        if (!Item.isMarkered(item.getItemStack())) return;

        item.remove();
    }

    @EventHandler
    private void onLogin(PlayerJoinEvent event) {
        Bukkit.getServer().getScheduler().runTaskLater(LunaSpring.getInstance(), () -> MenuManager.cleanInventory(event.getPlayer()), 10L);
    }
}
