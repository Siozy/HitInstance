package siozy.dev.lunaspring.API.items.secondary;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import siozy.dev.lunaspring.API.items.Component;
import siozy.dev.lunaspring.API.items.ItemComponent;

@Component
public interface BlockPlaceItemComponent extends ItemComponent {
    boolean onPlace(BlockPlaceEvent e, ItemStack itemStack);
}
