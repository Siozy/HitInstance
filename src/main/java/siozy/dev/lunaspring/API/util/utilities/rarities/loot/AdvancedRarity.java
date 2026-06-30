package siozy.dev.lunaspring.API.util.utilities.rarities.loot;

import com.google.common.collect.Maps;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import siozy.dev.lunaspring.API.util.utilities.rarities.Rarity;
import siozy.dev.lunaspring.API.util.utilities.rarities.RarityManager;

import java.util.Map;

@Getter
public class AdvancedRarity<E> implements Rarity {
    private final double chance;
    private final Map<E, Double> lootMap;
    public AdvancedRarity(double chance, Map<E, Double> lootMap) {
        this.chance = chance;
        this.lootMap = lootMap;
    }

    public AdvancedRarity(double chance) {
        this.chance = chance;
        this.lootMap = Maps.newHashMap();
    }

    public void add(@NotNull E loot, double chance) {
        this.lootMap.put(loot, chance);
    }

    public @Nullable E calculate() {
        return RarityManager.calculate(this.lootMap);
    }
}
