package siozy.dev.lunaspring.API.conditions.abs;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface PlayerCondition extends Condition<Player> {
    @Override
    default Player cast(OfflinePlayer player) {
        return player != null && player.isOnline() && player instanceof Player p ? p : null;
    }
}
