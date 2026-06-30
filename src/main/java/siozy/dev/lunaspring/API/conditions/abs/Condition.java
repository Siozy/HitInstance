package siozy.dev.lunaspring.API.conditions.abs;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public interface Condition<E> {
    boolean check(E object, Object... objects);
    Object[] generateObjects(ConfigurationSection section);
    E cast(OfflinePlayer player);
    default boolean unknownCheck(ConfigurationSection section) {
        return false;
    }

    default @Nullable String[] getParams() {
        ConditionParams annot = this.getClass().getAnnotation(ConditionParams.class);
        return annot == null ? null : annot.identifiers();
    }

    default @Nullable Class<?>[] getParamsClasses() {
        ConditionParams annot = this.getClass().getAnnotation(ConditionParams.class);
        return annot == null ? null : annot.idClasses();
    }

    default @Nullable Class<?> getParamClass(String param) {
        ConditionParams annot = this.getClass().getAnnotation(ConditionParams.class);
        if (annot == null) return null;

        for (int index = 0; index < annot.identifiers().length; index++) {
            if (annot.identifiers()[index].equals(param)) {
                return annot.idClasses()[index];
            }
        }

        return null;
    }

    default @Nullable Map<String, Class<?>> getMapParams() {
        ConditionParams annot = this.getClass().getAnnotation(ConditionParams.class);
        if (annot == null) return null;

        Map<String, Class<?>> map = new HashMap<>();
        for (int index = 0; index < annot.identifiers().length; index++) {
            map.put(annot.identifiers()[index], annot.idClasses()[index]);
        }

        return map;
    }
}
