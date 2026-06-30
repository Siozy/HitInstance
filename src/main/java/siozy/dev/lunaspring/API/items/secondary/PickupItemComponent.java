package siozy.dev.lunaspring.API.items.secondary;

import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import siozy.dev.lunaspring.API.items.Component;
import siozy.dev.lunaspring.API.items.ItemComponent;

@Component
public interface PickupItemComponent extends ItemComponent {
    void onPickup(PlayerAttemptPickupItemEvent e, ItemStack itemStack);
}
