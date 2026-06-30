package siozy.dev.lunaspring.API.util.utilities.rarities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import siozy.dev.lunaspring.API.util.utilities.LunaMath;

import java.util.*;
import java.util.function.Consumer;

@Getter @RequiredArgsConstructor @SuppressWarnings("deprecation")
public enum StackRandomizer {

    ADVANCED_AMOUNT(null, i -> {
        if (i.getAmount() > 1) i.setAmount(LunaMath.getRandomInt(1, i.getAmount()));
    }),

    AMOUNT(ADVANCED_AMOUNT, i -> {
        i.setAmount(LunaMath.getRandomInt(1, i.getMaxStackSize()));
    }),

    ADVANCED_ENCHANTS(null, i -> {
        Map<Enchantment, Integer> enchantments = new HashMap<>(i.getEnchantments());
        for (Enchantment enchantment : enchantments.keySet()) {
            i.removeEnchantment(enchantment);
            i.addUnsafeEnchantment(enchantment, LunaMath.getRandomInt(1, enchantments.get(enchantment) + 1));
        }
    }),

    ENCHANTS(ADVANCED_ENCHANTS, i -> {
        List<Enchantment> enchantments = new ArrayList<>(i.getEnchantments().keySet());
        for (Enchantment enchantment : enchantments) {
            i.removeEnchantment(enchantment);
            i.addUnsafeEnchantment(enchantment, LunaMath.getRandomInt(1, enchantment.getMaxLevel() + 1));
        }
    }),

    ADVANCED_DURABILITY(null, i -> {
        if (i.getType().getMaxDurability() > 0) i.setDurability((short) LunaMath.getRandomInt(0, i.getType().getMaxDurability() - i.getDurability()));
    }),

    DURABILITY(ADVANCED_DURABILITY, i -> {
        if (i.getType().getMaxDurability() > 0) i.setDurability((short) LunaMath.getRandomInt(0, i.getType().getMaxDurability()));
    });

    private final StackRandomizer priorityRandomizer;
    private final Consumer<ItemStack> consumer;

    public void process(@NotNull ItemStack itemStack, @Nullable Collection<StackRandomizer> forCheckRandomizersList) {
        if (forCheckRandomizersList != null && forCheckRandomizersList.contains(priorityRandomizer)) return;
        this.consumer.accept(itemStack);
    }
}
