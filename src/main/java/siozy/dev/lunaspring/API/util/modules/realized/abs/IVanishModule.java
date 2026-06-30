package siozy.dev.lunaspring.API.util.modules.realized.abs;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import siozy.dev.lunaspring.API.util.modules.LunaModule;

import java.util.UUID;
import java.util.function.Supplier;

public interface IVanishModule extends LunaModule {
    boolean enable(Player player);
    boolean disable(Player player);
    boolean isVanished(UUID uuid);
    boolean isVanished(@NotNull OfflinePlayer player);
    boolean view(CommandSender viewer, UUID uuid);
    boolean view(CommandSender viewer, @NotNull OfflinePlayer player);
    Player exact(Supplier<Player> supplier);
    Player exact(String name);
    Player exact(UUID uuid);
    Player exact(CommandSender viewer, Supplier<Player> supplier);
    Player exact(CommandSender viewer, String name);
    Player exact(CommandSender viewer, UUID uuid);
    void processJoinHandler(PlayerJoinEvent event);
}
