package siozy.dev.lunaspring.API.menus.updatable;

import org.bukkit.event.inventory.InventoryOpenEvent;
import siozy.dev.lunaspring.API.menus.ItemListMenu;
import siozy.dev.lunaspring.API.menus.updatable.tasks.UpdatableTask;
import siozy.dev.lunaspring.LunaSpring;

public interface UpdatableIMenu extends ItemListMenu {
    UpdatableTask getRunnable();

    @Override
    default void onOpen(InventoryOpenEvent e) {
        if (this.getRunnable() == null) return;
        this.getRunnable().runTaskAsynchronously(LunaSpring.getInstance());
    }
}
