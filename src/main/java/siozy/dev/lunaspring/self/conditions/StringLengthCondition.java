package siozy.dev.lunaspring.self.conditions;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import siozy.dev.lunaspring.API.conditions.abs.ConditionId;
import siozy.dev.lunaspring.API.conditions.abs.ConditionNullable;
import siozy.dev.lunaspring.API.conditions.abs.ConditionParams;
import siozy.dev.lunaspring.API.conditions.abs.StringCondition;
import siozy.dev.lunaspring.API.util.utilities.LunaMath;

@ConditionId("STRING_LENGTH")
@ConditionNullable
@ConditionParams(identifiers = {"input", "min", "max"}, idClasses = {String.class, int.class, int.class})
public class StringLengthCondition implements StringCondition {
    @Override
    public boolean check(OfflinePlayer player, String[] strings) {
        if (strings.length == 0) return false;

        int length = strings[0].length();
        if (strings.length == 1) return length > 0;

        int min = LunaMath.toInt(strings[1]);
        if (strings.length == 2) {
            return length >= min;
        }

        int max = LunaMath.toInt(strings[2]);
        return length >= min && length <= max;
    }

    @Override
    public Object[] generateObjects(ConfigurationSection section) {
        return new Object[]{
                section.getString("input"),
                section.getInt("min"),
                section.getInt("max")
        };
    }
}
