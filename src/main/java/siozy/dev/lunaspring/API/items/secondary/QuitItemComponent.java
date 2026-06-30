package siozy.dev.lunaspring.API.items.secondary;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import siozy.dev.lunaspring.API.items.Component;
import siozy.dev.lunaspring.API.items.SlotFilteringComponent;

import java.util.stream.Stream;

@Component
public interface QuitItemComponent extends SlotFilteringComponent {
    void onQuit(Player handler, Stream<ItemStack> componentItems);
}