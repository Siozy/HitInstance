package siozy.dev.lunaspring.API.items.task;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import siozy.dev.lunaspring.API.items.ComponentStorage;
import siozy.dev.lunaspring.API.items.secondary.TimedItemComponent;
import siozy.dev.lunaspring.API.util.service.managers.TaskManager;
import siozy.dev.lunaspring.API.util.utilities.tasks.LunaTask;
import siozy.dev.lunaspring.LunaSpring;

import java.util.List;

@Getter
public class TickableTask extends LunaTask {
    private final Player player;
    public TickableTask(Player player) {
        this.player = player;
    }

    @Override @SneakyThrows
    @SuppressWarnings("all")
    public void start() {
        TaskManager.register(this);
        while (this.isActive() && TaskManager.check(this)) {
            Thread.sleep(1000L);

            PlayerInventory inventory = this.player.getInventory();
            ComponentStorage.getRealizedComponents(TimedItemComponent.class).forEach(c -> {
                List<ItemStack> list = ComponentStorage.scanInventory(inventory, c).toList();
                if (!list.isEmpty()) {
                    if (c.isAsync()) {
                        c.tick(this.player, list);
                    }
                    else {
                        Bukkit.getScheduler().runTask(LunaSpring.getInstance(), () ->
                                c.tick(this.player, list));
                    }
                }
            });
        }
    }

    public void stop() {
        this.cancel();
        TaskManager.unregister(this);
    }
}