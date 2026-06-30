package siozy.dev.lunaspring.API.util.service.realized;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import siozy.dev.lunaspring.API.util.modules.Modules;
import siozy.dev.lunaspring.API.util.modules.realized.BasicVanishModule;
import siozy.dev.lunaspring.API.util.modules.realized.abs.IVanishModule;
import siozy.dev.lunaspring.API.util.service.LunaService;
import siozy.dev.lunaspring.LunaPlugin;
import siozy.dev.lunaspring.LunaSpring;

import java.util.UUID;
import java.util.function.Supplier;

@Getter
public class VanishService implements LunaService {
    private final IVanishModule module;
    public VanishService() {
        this.module = Modules.provide(IVanishModule.class, () -> new BasicVanishModule(LunaSpring.getInstance(), null));
    }

    public LunaPlugin getOwnPlugin() {
        return mayUse() ? module.getOwnPlugin() : null;
    }

    public boolean enable(Player player) {
        return mayUse() && module.enable(player);
    }

    public boolean disable(Player player) {
        return mayUse() && module.disable(player);
    }

    public boolean isVanished(UUID uuid) {
        return mayUse() && module.isVanished(uuid);
    }

    public boolean isVanished(@NotNull OfflinePlayer player) {
        return mayUse() && module.isVanished(player);
    }

    public boolean view(CommandSender viewer, @NotNull OfflinePlayer player) {
        return !mayUse() || module.view(viewer, player);
    }

    public boolean view(CommandSender viewer, UUID uuid) {
        return !mayUse() || module.view(viewer, uuid);
    }

    public Player exact(Supplier<Player> supplier) {
        return mayUse() ? module.exact(supplier) : supplier.get();
    }

    public Player exact(String name) {
        return mayUse() ? module.exact(name) : Bukkit.getPlayer(name);
    }

    public Player exact(UUID uuid) {
        return mayUse() ? module.exact(uuid) : Bukkit.getPlayer(uuid);
    }

    public Player exact(CommandSender viewer, Supplier<Player> supplier) {
        return mayUse() ? module.exact(viewer, supplier) : supplier.get();
    }

    public Player exact(CommandSender viewer, String name) {
        return mayUse() ? module.exact(viewer, name) : Bukkit.getPlayer(name);
    }

    public Player exact(CommandSender viewer, UUID uuid) {
        return mayUse() ? module.exact(viewer, uuid) : Bukkit.getPlayer(uuid);
    }

    public void processJoinHandler(PlayerJoinEvent event) {
        if (!mayUse()) return;
        module.processJoinHandler(event);
    }

    public boolean mayUse() {
        return module != null;
    }
}
