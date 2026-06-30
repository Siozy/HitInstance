package siozy.dev.lunaspring.API.items;

import org.bukkit.inventory.ItemStack;
import siozy.dev.lunaspring.API.menus.items.NonMenuItem;
import siozy.dev.lunaspring.API.util.service.managers.NBTManager;

@Component
public interface ItemComponent {
    String getId();
    NonMenuItem createItem();
    default boolean itemIsComponent(ItemStack itemStack) {
        return itemStack != null && !itemStack.getType().isAir() && NBTManager.hasTag(itemStack, getId());
    }
}