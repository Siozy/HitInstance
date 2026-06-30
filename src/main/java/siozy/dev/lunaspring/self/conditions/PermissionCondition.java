package siozy.dev.lunaspring.self.conditions;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permissible;
import siozy.dev.lunaspring.API.conditions.abs.Condition;
import siozy.dev.lunaspring.API.conditions.abs.ConditionId;
import siozy.dev.lunaspring.API.conditions.abs.ConditionParams;
import siozy.dev.lunaspring.API.util.utilities.Utils;

@ConditionId("PERMISSION")
@ConditionParams(identifiers = "permission", idClasses = String.class)
public class PermissionCondition implements Condition<Permissible> {
    @Override
    public boolean check(Permissible permissible, Object... objects) {
        if (objects.length < 1) return false;

        String permission = (String) objects[0];
        if (permissible instanceof OfflinePlayer p) permission = Utils.setPlaceholders(p, permission);

        return permissible.hasPermission(permission);
    }

    @Override
    public Object[] generateObjects(ConfigurationSection section) {
        return new Object[]{section.getString("permission")};
    }

    @Override
    public Permissible cast(OfflinePlayer player) {
        return player instanceof Permissible p ? p : null;
    }
}
