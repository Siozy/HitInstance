package siozy.dev.lunaspring.API.menus.updatable;

import org.bukkit.inventory.ItemStack;
import siozy.dev.lunaspring.API.menus.ItemListMenu;
import siozy.dev.lunaspring.API.menus.items.Item;

public interface UpdatableItem {
    ItemStack getItemStack();
    Item insert(ItemListMenu menu);
    void tick(UpdatableIMenu updatableMenu);
}
