package siozy.dev.lunaspring.self.conditions;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import siozy.dev.lunaspring.API.conditions.abs.ConditionId;
import siozy.dev.lunaspring.API.conditions.abs.ConditionNullable;
import siozy.dev.lunaspring.API.conditions.abs.ConditionParams;
import siozy.dev.lunaspring.API.conditions.abs.StringCondition;

@ConditionId("STARTS_WITH")
@ConditionNullable
@ConditionParams(identifiers = {"input", "check"}, idClasses = {String.class, String.class})
public class StartsWithCondition implements StringCondition {
    @Override
    public boolean check(OfflinePlayer player, String[] strings) {
        return strings[0].startsWith(strings[1]);
    }

    @Override
    public Object[] generateObjects(ConfigurationSection section) {
        return new Object[]{
                section.getString("input"),
                section.getString("check")
        };
    }
}
