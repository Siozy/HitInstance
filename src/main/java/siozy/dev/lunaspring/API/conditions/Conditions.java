package siozy.dev.lunaspring.API.conditions;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import siozy.dev.lunaspring.API.conditions.abs.*;
import siozy.dev.lunaspring.API.util.utilities.Utils;
import siozy.dev.lunaspring.API.util.utilities.reflection.AnnotationScanner;
import siozy.dev.lunaspring.API.util.utilities.reflection.ClassEntry;
import siozy.dev.lunaspring.LunaPlugin;

import java.util.*;
import java.util.function.Function;

@UtilityClass
public class Conditions {
    public final Function<ConfigurationSection, List<String>> getErrorMessagesFunction;
    public final Map<String, Condition<?>> types;
    public final Class<ConditionNullable> CACHED_ANNOTATION;
    static {
        types = new HashMap<>();
        getErrorMessagesFunction = s -> s.getStringList("errorActions");
        CACHED_ANNOTATION = ConditionNullable.class;
    }

    public Set<String> keys() {
        return types.keySet();
    }

    public boolean register(String id, Condition<?> condition) {
        id = id.toUpperCase();
        if (types.containsKey(id)) return false;
        types.put(id, condition);
        return true;
    }

    public boolean register(@NotNull Condition<?> condition) {
        ConditionId conditionId = condition.getClass().getAnnotation(ConditionId.class);
        return conditionId != null && register(conditionId.value(), condition);
    }

    public void unregister(String id) {
        types.remove(id);
    }

    @SneakyThrows
    public void load(LunaPlugin plugin, String... packages) {
        Set<ClassEntry<ConditionId>> set = AnnotationScanner.findAnnotatedClasses(
                plugin,
                ConditionId.class,
                packages);
        for (ClassEntry<ConditionId> classEntry : set) {
            Class<?> clazz = classEntry.getClazz();
            if (!Condition.class.isAssignableFrom(clazz)) continue;

            Condition<?> condition = (Condition<?>) clazz.getDeclaredConstructor().newInstance();
            String id = classEntry.getAnnotation().value();

            register(id, condition);
            if (classEntry.getAnnotation().hasNegativeVersion()) {
                Condition<?> negativeCondition = new NegativeCondition<>(condition);
                register("not " + id, negativeCondition);
            }
        }
    }

    public @Nullable Condition<?> getCondition(String id) {
        return id == null ? null : types.get(id.toUpperCase());
    }

    public @Nullable Condition<?> getCondition(@NotNull ConfigurationSection section) {
        String type = section.getString("type");
        return getCondition(type);
    }

    public <E> boolean checkGenericCondition(@Nullable E handler,
                                             @Nullable Condition<E> condition,
                                             @NotNull ConfigurationSection section,
                                             boolean printErrorMessage) {
        if (condition == null) return false;

        if (handler == null && !condition.getClass().isAnnotationPresent(CACHED_ANNOTATION)) {
            if (condition.unknownCheck(section)) return true;

            if (printErrorMessage)
                Utils.processCommandsWithActions(
                        Bukkit.getConsoleSender(),
                        getErrorMessagesFunction.apply(section)
                );
            return false;
        }

        try {
            if (condition.check(handler, condition.generateObjects(section))) return true;
        }
        catch (Exception e) {
            String name = condition.getClass().getSimpleName();
            Utils.debug("Ошибка загрузки " + name + ": " + e.getMessage());
        }

        if (printErrorMessage) {
            CommandSender sender = handler instanceof Player p ? p : Bukkit.getConsoleSender();
            Utils.processCommandsWithActions(
                    sender,
                    getErrorMessagesFunction.apply(section),
                    "player-%-" + sender.getName());
        }

        return false;
    }

    public <E> boolean checkCondition(@Nullable OfflinePlayer player,
                                  @Nullable Condition<E> condition,
                                  @NotNull ConfigurationSection section,
                                  boolean printErrorMessage) {
        if (condition == null) return false;

        E object = condition.cast(player);
        return checkGenericCondition(object, condition, section, printErrorMessage);
    }

    public boolean checkCondition(@Nullable OfflinePlayer player,
                                  @NotNull ConfigurationSection section,
                                  boolean printErrorMessage) {
        Condition<?> condition = getCondition(section);
        return checkCondition(player, condition, section, printErrorMessage);
    }

    public boolean checkConditions(@Nullable Player player,
                                   @NotNull ConfigurationSection section,
                                   Operation operation,
                                   boolean printErrorMessage) {
        List<ConfigurationSection> list = section.getKeys(false)
                .stream()
                .map(section::getConfigurationSection)
                .filter(Objects::nonNull)
                .toList();

        if (operation == null || operation == Operation.AND) {
            for (ConfigurationSection conditionSection : list) {
                if (!checkCondition(player, conditionSection, printErrorMessage)) return false;
            }

            return true;
        }
        else {
            for (ConfigurationSection conditionSection : list) {
                if (checkCondition(player, conditionSection, false)) return true;
            }

            if (printErrorMessage) {
                boolean hasPlayer = player != null;
                Utils.processCommandsWithActions(
                        hasPlayer ? player : Bukkit.getConsoleSender(),
                        getErrorMessagesFunction.apply(section),
                        hasPlayer ? "player-%-" + player.getName() : null
                );
            }

            return false;
        }
    }

    public boolean checkConditions(@Nullable Player player, @NotNull ConfigurationSection section) {
        Operation operation = Utils.getEnumValue(Operation.class, section.getString("operation"));
        return checkConditions(
                player,
                section,
                operation,
                section.getBoolean("invokeErrorActions", true));
    }

    public Object[] generateObjects(ConfigurationSection section) {
        Condition<?> condition = getCondition(section);
        return condition == null ? null : condition.generateObjects(section);
    }

    public enum Operation {
        AND,
        OR
    }
}
