package siozy.dev.lunaspring.self.conditions;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import siozy.dev.lunaspring.API.conditions.abs.ConditionId;
import siozy.dev.lunaspring.API.conditions.abs.ConditionParams;
import siozy.dev.lunaspring.API.conditions.abs.OfflinePlayerCondition;
import siozy.dev.lunaspring.API.util.service.managers.VaultManager;

@ConditionId("MONEY")
@ConditionParams(identifiers = "money", idClasses = double.class)
public class MoneyCondition implements OfflinePlayerCondition {
    @Override
    public boolean check(OfflinePlayer player, Object... objects) {
        return VaultManager.hasEnoughMoney(player, (double) objects[0]);
    }

    @Override
    public Object[] generateObjects(ConfigurationSection section) {
        return new Object[]{section.getDouble("money")};
    }
}
