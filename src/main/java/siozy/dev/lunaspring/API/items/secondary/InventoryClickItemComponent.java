package siozy.dev.lunaspring.API.items.secondary;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import siozy.dev.lunaspring.API.items.Component;
import siozy.dev.lunaspring.API.items.ItemComponent;

@Component
public interface InventoryClickItemComponent extends ItemComponent {
    void onClick(InventoryClickEvent event, ItemStack itemStack, boolean inCursor);
}