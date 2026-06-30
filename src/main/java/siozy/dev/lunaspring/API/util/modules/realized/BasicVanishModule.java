package siozy.dev.lunaspring.API.util.modules.realized;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import siozy.dev.lunaspring.API.events.vanish.VanishDisableEvent;
import siozy.dev.lunaspring.API.events.vanish.VanishEnableEvent;
import siozy.dev.lunaspring.API.util.modules.realized.abs.IVanishModule;
import siozy.dev.lunaspring.API.util.utilities.Utils;
import siozy.dev.lunaspring.LunaPlugin;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Getter
public class BasicVanishModule implements IVanishModule {
    protected final LunaPlugin ownPlugin;
    protected final Predicate<CommandSender> checkView;
    protected final double[] radiusEntityCheck;
    public BasicVanishModule(LunaPlugin ownPlugin, Predicate<Player> checkView, double[] radiusEntityCheck) {
        this.radiusEntityCheck = radiusEntityCheck;
        this.ownPlugin = ownPlugin;
        this.checkView = s -> checkView == null || (!(s instanceof Player p) || checkView.test(p));
    }

    public BasicVanishModule(LunaPlugin ownPlugin, Predicate<Player> checkView) {
        this(ownPlugin, checkView, new double[]{48, 48, 48});
    }

    public boolean enable(Player player) {
        VanishEnableEvent event = new VanishEnableEvent(player, checkView);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        Utils.playersAction(p -> {
            if (event.getCheckViewPredicate() == null || event.getCheckViewPredicate().test(p)) p.hidePlayer(ownPlugin, player);
        });

        player.getNearbyEntities(radiusEntityCheck[0], radiusEntityCheck[1], radiusEntityCheck[2])
                .stream()
                .filter(e -> e instanceof Mob mob && Objects.equals(mob.getTarget(), player))
                .forEach(e -> ((Mob) e).setTarget(null));

        player.setMetadata("vanished", new FixedMetadataValue(ownPlugin, true));
        player.setCollidable(false);
        player.setSilent(true);
        return true;
    }

    public boolean disable(Player player) {
        VanishDisableEvent event = new VanishDisableEvent(player);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        Utils.playersAction(p -> p.showPlayer(ownPlugin, player));
        player.removeMetadata("vanished", ownPlugin);

        player.setCollidable(true);
        player.setSilent(false);
        return true;
    }

    @Override
    public boolean isVanished(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        return this.isVanished(offlinePlayer);
    }

    @Override
    public boolean isVanished(@NotNull OfflinePlayer player) {
        return player.isOnline() &&
                player instanceof Player p &&
                p.hasMetadata("vanished") &&
                p.getMetadata("vanished").get(0).asBoolean();
    }

    public boolean view(CommandSender viewer, UUID uuid) {
        return !this.isVanished(uuid) || this.checkView.test(viewer);
    }

    public boolean view(CommandSender viewer, @NotNull OfflinePlayer player) {
        return view(viewer, player.getUniqueId());
    }

    public Player exact(Supplier<Player> supplier) {
        Player player = supplier.get();
        return player != null && this.isVanished(player) ? null : player;
    }

    public Player exact(String name) {
        return exact(() -> Bukkit.getPlayer(name));
    }

    public Player exact(UUID uuid) {
        return exact(() -> Bukkit.getPlayer(uuid));
    }

    public Player exact(CommandSender viewer, Supplier<Player> supplier) {
        Player player = supplier.get();
        return player != null && this.isVanished(player) ? null : player;
    }

    public Player exact(CommandSender viewer, String name) {
        return exact(viewer, () -> Bukkit.getPlayer(name));
    }

    public Player exact(CommandSender viewer, UUID uuid) {
        return exact(viewer, () -> Bukkit.getPlayer(uuid));
    }

    @Override
    public void processJoinHandler(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        boolean isVanished = this.isVanished(player);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (this.isVanished(onlinePlayer) && !this.view(player, onlinePlayer))
                player.hidePlayer(ownPlugin, onlinePlayer);
            if (isVanished && !this.view(onlinePlayer, player))
                onlinePlayer.hidePlayer(ownPlugin, player);
        }
    }
}
