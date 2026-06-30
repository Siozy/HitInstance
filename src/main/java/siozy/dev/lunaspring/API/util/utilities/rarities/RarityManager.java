package siozy.dev.lunaspring.API.util.utilities.rarities;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import siozy.dev.lunaspring.API.util.utilities.LunaMath;
import siozy.dev.lunaspring.API.util.utilities.Utils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.ToDoubleFunction;

@UtilityClass
public class RarityManager {
    public @Nullable <T extends Rarity> T calculate(Collection<T> rarities) {
        return calculate(rarities, Rarity::getChance);
    }

    public @Nullable <R> R calculate(Map<R, Double> tMap) {
        return calculate(tMap, tMap::get);
    }

    public @Nullable <K, V> K calculate(Map<K, V> tMap, ToDoubleFunction<K> getChanceFunction) {
        return calculate(tMap.keySet(), getChanceFunction);
    }

    public @Nullable <R> R calculate(Collection<R> collection, ToDoubleFunction<R> getChanceFunction) {
        double allChances = collection.stream().mapToDouble(getChanceFunction).sum();
        double randomValue = allChances * ThreadLocalRandom.current().nextDouble();

        double currentSum = 0.0;
        for (R rarity : collection) {
            currentSum += getChanceFunction.applyAsDouble(rarity);
            if (randomValue < currentSum) return rarity;
        }

        return null;
    }

    public @Nullable <R> R calculateSection(ConfigurationSection section, BiFunction<ConfigurationSection, String, R> getterFunction) {
        List<String> keys = section.getKeys(false).stream().toList();
        if (!keys.isEmpty()) {
            String key = keys.get(LunaMath.getRandomInt(0, keys.size()));
            return getterFunction.apply(section, key);
        }

        return null;
    }

    public @NotNull ItemStack calculateItemStack(ConfigurationSection section, Collection<StackRandomizer> randomizers) {
        ItemStack stack = calculateSection(section, ConfigurationSection::getItemStack);
        if (stack == null) stack = new ItemStack(Material.STONE);
        else stack = stack.clone();

        for (StackRandomizer randomizer : randomizers) randomizer.process(stack, randomizers);
        return stack;
    }

    public @NotNull ItemStack calculateItemStack(ConfigurationSection section, StackRandomizer... randomizers) {
        return calculateItemStack(section, Arrays.asList(randomizers));
    }
}
