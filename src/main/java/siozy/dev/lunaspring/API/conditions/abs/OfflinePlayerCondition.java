package siozy.dev.lunaspring.API.conditions.abs;

import org.bukkit.OfflinePlayer;

public interface OfflinePlayerCondition extends Condition<OfflinePlayer> {
    @Override
    default OfflinePlayer cast(OfflinePlayer player) {
        return player;
    }
}
