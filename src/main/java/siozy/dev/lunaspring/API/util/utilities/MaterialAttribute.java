package siozy.dev.lunaspring.API.util.utilities;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum MaterialAttribute {
    NETHERITE_AXE(10, 1),
    DIAMOND_AXE(9, 1),
    IRON_AXE(9, 0.9),
    GOLDEN_AXE(7, 1),
    STONE_AXE(9, 0.8),
    WOODEN_AXE(8, 0.8),

    NETHERITE_PICKAXE(6, 1.2),
    DIAMOND_PICKAXE(5, 1.2),
    IRON_PICKAXE(4, 1.2),
    GOLDEN_PICKAXE(2, 1.2),
    STONE_PICKAXE(3, 1.2),
    WOODEN_PICKAXE(2, 1.2),

    NETHERITE_HOE(1, 4),
    DIAMOND_HOE(1, 4),
    IRON_HOE(1, 3),
    GOLDEN_HOE(1, 1),
    STONE_HOE(1, 2),
    WOODEN_HOE(1, 1),

    NETHERITE_SHOVEL(6.5, 1),
    DIAMOND_SHOVEL(5.5, 1),
    IRON_SHOVEL(4.5, 1),
    GOLDEN_SHOVEL(2.5, 1),
    STONE_SHOVEL(3.5, 1),
    WOODEN_SHOVEL(2.5, 1),

    TRIDENT(9, 1.1),
    NETHERITE_SWORD(8, 1.6),
    DIAMOND_SWORD(7, 1.6),
    IRON_SWORD(6, 1.6),
    GOLDEN_SWORD(4, 1.6),
    STONE_SWORD(5, 1.6),
    WOODEN_SWORD(4, 1.6),

    TURTLE_HELMET(2, 0, 0),
    NETHERITE_HELMET(3, 3, 1),
    DIAMOND_HELMET(3, 2, 0),
    IRON_HELMET(2, 0, 0),
    GOLDEN_HELMET(2, 0, 0),
    LEATHER_HELMET(1, 0, 0),
    CHAINMAIL_HELMET(2, 0, 0),

    NETHERITE_CHESTPLATE(8, 3, 1),
    DIAMOND_CHESTPLATE(8, 2, 0),
    IRON_CHESTPLATE(6, 0, 0),
    GOLDEN_CHESTPLATE(5, 0, 0),
    LEATHER_CHESTPLATE(3, 0, 0),
    CHAINMAIL_CHESTPLATE(5, 0, 0),

    NETHERITE_LEGGINGS(6, 3, 1),
    DIAMOND_LEGGINGS(6, 2, 0),
    IRON_LEGGINGS(5, 0, 0),
    GOLDEN_LEGGINGS(3, 0, 0),
    LEATHER_LEGGINGS(2, 0, 0),
    CHAINMAIL_LEGGINGS(4, 0, 0),

    NETHERITE_BOOTS(3, 3, 1),
    DIAMOND_BOOTS(3, 2, 0),
    IRON_BOOTS(2, 0, 0),
    GOLDEN_BOOTS(1, 0, 0),
    LEATHER_BOOTS(1, 0, 0),
    CHAINMAIL_BOOTS(1, 0, 0);

    private double damage = 0;
    private double speed = 0;
    MaterialAttribute(double damage, double speed) {
        this.damage = damage;
        this.speed = speed;
    }

    private double armor_protection = 0;
    private double armor_weight = 0;
    private double armor_akb = 0;
    MaterialAttribute(double armor_protection, double armor_weight, double armor_akb) {
        this.armor_akb = armor_akb;
        this.armor_protection = armor_protection;
        this.armor_weight = armor_weight;
    }
}
