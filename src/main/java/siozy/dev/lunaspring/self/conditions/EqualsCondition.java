package siozy.dev.lunaspring.self.conditions;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import siozy.dev.lunaspring.API.conditions.abs.ConditionId;
import siozy.dev.lunaspring.API.conditions.abs.ConditionNullable;
import siozy.dev.lunaspring.API.conditions.abs.ConditionParams;
import siozy.dev.lunaspring.API.conditions.abs.StringCondition;

@ConditionId("EQUALS")
@ConditionNullable
@ConditionParams(identifiers = {"input", "check"}, idClasses = {String.class, String.class})
public class EqualsCondition implements StringCondition {
    @Override
    public boolean check(OfflinePlayer player, String[] strings) {
        return strings.length >= 2 && strings[1].equals(strings[0]);
    }

    @Override
    public Object[] generateObjects(ConfigurationSection section) {
        return new Object[]{
                section.getString("input"),
                section.getString("check")
        };
    }
}
