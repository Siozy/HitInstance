package siozy.dev.lunaspring.API.items.secondary;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import siozy.dev.lunaspring.API.items.Component;
import siozy.dev.lunaspring.API.items.SlotFilteringComponent;

import java.util.stream.Stream;

@Component
public interface JoinItemComponent extends SlotFilteringComponent {
    void onJoin(Player handler, Stream<ItemStack> componentItems);
}