package siozy.dev.lunaspring.API.items.secondary;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import siozy.dev.lunaspring.API.items.Component;
import siozy.dev.lunaspring.API.items.ItemComponent;

@Component
public interface ClickableItemComponent extends ItemComponent {
    boolean onClick(PlayerInteractEvent event, ItemStack itemStack);
}
