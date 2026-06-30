package siozy.dev.lunaspring.API.util.service.managers;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import siozy.dev.lunaspring.API.util.modules.Modules;
import siozy.dev.lunaspring.API.util.modules.realized.abs.IVanishModule;
import siozy.dev.lunaspring.API.util.service.realized.VanishService;
import siozy.dev.lunaspring.API.util.service.realized.VaultService;
import siozy.dev.lunaspring.LunaPlugin;

import java.util.UUID;
import java.util.function.Supplier;

@UtilityClass
public class VanishManager {
    @Getter private VanishService vanishService;

    public void initialize() {
        vanishService = new VanishService();
    }

    public LunaPlugin getOwnPlugin() {
        return vanishService.getOwnPlugin();
    }

    public boolean enable(Player player) {
        return vanishService.enable(player);
    }

    public boolean disable(Player player) {
        return vanishService.disable(player);
    }

    public boolean mayUse() {
        return vanishService.mayUse();
    }

    public boolean isVanished(UUID uuid) {
        return vanishService.isVanished(uuid);
    }

    public void processJoinHandler(PlayerJoinEvent event) {
        vanishService.processJoinHandler(event);
    }

    public boolean isVanished(@NotNull OfflinePlayer player) {
        return vanishService.isVanished(player);
    }

    public boolean view(CommandSender viewer, @NotNull OfflinePlayer player) {
        return vanishService.view(viewer, player);
    }

    public boolean view(CommandSender viewer, UUID uuid) {
        return vanishService.view(viewer, uuid);
    }

    public Player exact(CommandSender viewer, UUID uuid) {
        return vanishService.exact(viewer, uuid);
    }

    public Player exact(Supplier<Player> supplier) {
        return vanishService.exact(supplier);
    }

    public Player exact(CommandSender viewer, String name) {
        return vanishService.exact(viewer, name);
    }

    public Player exact(String name) {
        return vanishService.exact(name);
    }

    public Player exact(UUID uuid) {
        return vanishService.exact(uuid);
    }

    public Player exact(CommandSender viewer, Supplier<Player> supplier) {
        return vanishService.exact(viewer, supplier);
    }
}
