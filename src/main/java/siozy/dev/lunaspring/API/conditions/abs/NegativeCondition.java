package siozy.dev.lunaspring.API.conditions.abs;

import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@RequiredArgsConstructor
public class NegativeCondition<E> implements Condition<E> {
    private final Condition<E> condition;

    @Override
    public boolean check(E object, Object... objects) {
        return !condition.check(object, objects);
    }

    @Override
    public Object[] generateObjects(ConfigurationSection section) {
        return condition.generateObjects(section);
    }

    @Override
    public E cast(OfflinePlayer player) {
        return condition.cast(player);
    }

    @Override
    public boolean unknownCheck(ConfigurationSection section) {
        return condition.unknownCheck(section);
    }

    @Override
    public @Nullable Class<?> getParamClass(String param) {
        return condition.getParamClass(param);
    }

    @Override
    public @Nullable Class<?>[] getParamsClasses() {
        return condition.getParamsClasses();
    }

    @Override
    public @Nullable Map<String, Class<?>> getMapParams() {
        return condition.getMapParams();
    }

    @Override
    public @Nullable String[] getParams() {
        return condition.getParams();
    }
}
