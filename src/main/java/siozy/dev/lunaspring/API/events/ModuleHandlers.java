package siozy.dev.lunaspring.API.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import siozy.dev.lunaspring.API.util.service.managers.VanishManager;

public class ModuleHandlers implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        VanishManager.processJoinHandler(e);
    }
}
