package siozy.dev.lunaspring.API.menus.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import siozy.dev.lunaspring.API.util.exceptions.NoItemMetaException;
import siozy.dev.lunaspring.API.util.service.managers.ColorManager;
import siozy.dev.lunaspring.API.util.service.managers.NBTManager;
import siozy.dev.lunaspring.API.util.utilities.LunaMath;
import siozy.dev.lunaspring.API.util.utilities.Utils;

import java.io.Serializable;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Getter
@SuppressWarnings({"unused", "deprecation"})
@Accessors(chain = true, fluent = false)
public class NonMenuItem implements Cloneable {
    @Setter private ItemStack itemStack;
    @Setter private String id = Utils.getRKey((byte) 14);
    protected Material material;
    protected String displayName;
    protected List<String> lore;
    @Range(from = 1, to = 64)
    protected int amount;
    protected boolean glowing = false;
    protected String headValue;
    protected Map<Enchantment, Integer> enchantments = Maps.newHashMap();
    protected List<ItemFlag> itemFlags = Lists.newArrayList();

    @Builder(builderMethodName = "superbuilder", buildMethodName = "superbuild")
    public NonMenuItem(Material material, String displayName, List<String> lore, int amount) {
        if (material == null) material = Material.STONE;
        this.material = material;
        this.amount = Math.max(amount, 1);
        this.itemStack = new ItemStack(this.material, this.amount);
        this.displayName = ColorManager.color(displayName);
        this.setLore(lore);
    }

    public NonMenuItem() {
        this(Material.STONE);
    }

    public NonMenuItem(Material material) {
        this(material, null, Lists.newArrayList(), 1);
    }

    public NonMenuItem(Material material, int amount) {
        this(material, null, Lists.newArrayList(), amount);
    }

    public NonMenuItem(@NotNull ConfigurationSection section) {
        this(Utils.getMaterial(section.getString("material")),
                section.getString("displayName"),
                section.getStringList("lore"),
                section.getInt("amount"));

        this.setGlowing(section.getBoolean("enchanted"));

        // Enchantments
        this.applyEnchantments(section);

        // ItemFlags
        this.applyItemFlags(section);

        // NBT
        ConfigurationSection nbtSection = section.getConfigurationSection("NBT");
        this.applyNBT(nbtSection);

        // Head
        this.applyBaseHead(section);

        // ATTRIBUTES
        this.applyAttributes(section);

        // META COLOR
        this.setMetaColor(section.getString("color"));

        // DURABILITY
        this.setDurability(section);

        // UNBREAKABLE
        this.setUnbreakable(section.getBoolean("unbreakable", false));

        // MODELDATA
        this.applyModelData(section);

        // POTION EFFECTS
        this.applyPotionEffects(section);
    }

    // GETTERS

    public @NotNull ItemMeta getMeta() throws NoItemMetaException {
        ItemMeta meta = this.itemStack.getItemMeta();
        if (meta == null) throw new NoItemMetaException(this.itemStack);

        return meta;
    }

    public @Nullable <E extends ItemMeta> E getMeta(Class<E> targetClass) throws NoItemMetaException {
        ItemMeta meta = this.getMeta();
        try {
            if (targetClass.isAssignableFrom(meta.getClass())) return targetClass.cast(meta);
        } catch (ClassCastException ignored) {}
        return null;
    }

    public boolean isUnbreakable() {
        return getMeta().isUnbreakable();
    }

    public int getModelData() {
        return getMeta().getCustomModelData();
    }

    // SETTERS
    
    public NonMenuItem setMeta(ItemMeta meta) {
        this.itemStack.setItemMeta(meta);
        return this;
    }

    public NonMenuItem setModelData(int modelData) {
        ItemMeta meta = this.getMeta();
        meta.setCustomModelData(modelData);
        this.setMeta(meta);
        return this;
    }

    public NonMenuItem setMaterial(@NotNull Material material) {
        this.material = material;
        this.update();
        return this;
    }

    public NonMenuItem setAmount(int amount) {
        this.amount = Math.max(amount, 1);
        this.update();
        return this;
    }

    public NonMenuItem setPotionEffect(PotionEffect effect) {
        ItemMeta meta = this.getMeta();
        if (meta instanceof PotionMeta potionMeta) {
            potionMeta.addCustomEffect(effect, true);
            this.setMeta(potionMeta);
        }
        
        return this;
    }

    public NonMenuItem setPotionEffect(PotionEffectType effectType, int duration, int amplifier) {
        return this.setPotionEffect(new PotionEffect(effectType, duration, amplifier));
    }

    public NonMenuItem increase() {
        return this.setAmount(this.amount + 1);
    }

    public NonMenuItem decrease() {
        return this.setAmount(this.getAmount() - 1);
    }

    public NonMenuItem addAmount(int add) {
        return this.setAmount(this.getAmount() + add);
    }

    public NonMenuItem setDisplayName(String displayName, String... replacements) {
        this.displayName = ColorManager.color(Utils.applyReplacements(displayName, replacements));
        this.update();
        return this;
    }

    public NonMenuItem setLore(List<String> lore, String... replacements) {
        this.lore = lore;
        this.replaceLore(l -> ColorManager.color(Utils.applyReplacements(l, replacements)));
        return this;
    }

    public NonMenuItem setGlowing(boolean enchanted) {
        this.glowing = enchanted;
        if (enchanted) {
            this.itemStack.addUnsafeEnchantment(Enchantment.LUCK_OF_THE_SEA, 1);
            this.itemStack.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            this.itemStack.removeEnchantment(Enchantment.LUCK_OF_THE_SEA);
            this.itemStack.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public NonMenuItem setUnbreakable(boolean value) {
        ItemMeta meta = this.getMeta();
        meta.setUnbreakable(value);
        this.itemStack.setItemMeta(meta);
        return this;
    }

    public NonMenuItem setAll(Material material, int amount, String displayName, List<String> lore, boolean enchanted) {
        if (material != null)
            this.setMaterial(material);
        if (amount > 0)
            this.setAmount(amount);
        if (lore != null && !lore.isEmpty())
            this.setLore(lore);
        if (displayName != null && !displayName.isEmpty())
            this.setDisplayName(displayName);
        this.setGlowing(enchanted);
        return this;
    }

    public NonMenuItem setAll(@NotNull ConfigurationSection itemSection) {
        String strMaterial = itemSection.getString("material");
        Material newMaterial = strMaterial == null || strMaterial.isEmpty() ? null : Material.getMaterial(strMaterial);

        int amount = itemSection.getInt("amount");
        String displayName = itemSection.getString("displayName");
        List<String> lore = new ArrayList<>(itemSection.getStringList("lore"));
        this.setAll(newMaterial, amount, displayName, lore, itemSection.getBoolean("enchanted"));

        this.itemFlags.clear();
        this.enchantments.clear();
        this.applyBaseHead(itemSection);
        this.applyEnchantments(itemSection);
        this.applyItemFlags(itemSection);
        this.applyAttributes(itemSection);
        this.setMetaColor(itemSection.getString("color"));
        this.setDurability(itemSection);
        this.setUnbreakable(itemSection.getBoolean("unbreakable", false));
        this.applyModelData(itemSection);
        this.applyPotionEffects(itemSection);

        return this;
    }

    public NonMenuItem update() throws NoItemMetaException {
        this.itemStack.setType(this.material);
        ItemMeta meta = this.getMeta();

        if (this.displayName != null && !this.displayName.isEmpty())
            meta.setDisplayName(this.displayName);

        if (this.lore != null && !this.lore.isEmpty())
            meta.setLore(new ArrayList<>(this.lore));


        this.itemStack.setItemMeta(meta);
        this.itemStack.setAmount(this.amount);
        return this;
    }

    public NonMenuItem replaceLore(UnaryOperator<String> operator) {
        this.getLore().replaceAll(operator);
        this.update();
        return this;
    }

    public EquipmentSlot getEquipmentSlot() {
        return this.material.getEquipmentSlot();
    }

    // APPLIERS - NBT, ItemFlags, Attributes, Enchantments, BaseHeads

    public NonMenuItem applyNBT(Map<String, String> nbtTags) {
        nbtTags.forEach((key, value) -> NBTManager.setString(this.itemStack, key, value));
        return this;
    }

    public NonMenuItem applyNBT(ConfigurationSection nbtSection) {
        if (nbtSection != null) {
            nbtSection.getValues(false).forEach((key, value) -> {
                if (!NBTManager.hasTag(this.itemStack, key)) {
                    if (value instanceof String strValue) NBTManager.setString(this.itemStack, key, strValue);

                    else if (value instanceof Integer intValue) NBTManager.setInt(this.itemStack, key, intValue);

                    else if (value instanceof Boolean boolValue) NBTManager.setBool(this.itemStack, key, boolValue);

                    else if (value instanceof Double dValue) NBTManager.setDouble(itemStack, key, dValue);
                }
            });
        }
        return this;
    }

    public NonMenuItem applyItemFlags(ConfigurationSection section) {
        this.applyItemFlags(section.getStringList("itemflags").stream().map(ItemFlag::valueOf).collect(Collectors.toSet()));
        return this;
    }

    public NonMenuItem applyItemFlags(Collection<ItemFlag> itemFlags) {
        return this.applyItemFlags(itemFlags.toArray(new ItemFlag[0]));
    }

    public NonMenuItem applyItemFlags(ItemFlag... itemFlags) throws NoItemMetaException {
        ItemMeta meta = this.getMeta();
        if (itemFlags != null) {
            this.itemFlags.addAll(List.of(itemFlags));
            meta.addItemFlags(itemFlags);
            this.itemStack.setItemMeta(meta);
        }
        return this;
    }


    public NonMenuItem applyBaseHead(ConfigurationSection section) {
        String baseHeadValue = section.getString("baseHead");
        if (baseHeadValue != null && !baseHeadValue.isEmpty()) {
            if (!this.material.equals(Material.PLAYER_HEAD)) {
                this.setMaterial(Material.PLAYER_HEAD);
            }

            this.applyBaseHead(baseHeadValue);
        }
        return this;
    }

    public NonMenuItem applyBaseHead(@NotNull String text) {
        OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(text);
        if (player != null) {
            return this.applyBaseHead(player);
        }

        this.headValue = text;
        NBTManager.base64head(this.itemStack, text);
        return this;
    }

    public NonMenuItem applyBaseHead(@NotNull OfflinePlayer player) {
        this.headValue = null;
        NBTManager.base64head(this.itemStack, player);
        return this;
    }

    public NonMenuItem applyEnchantment(Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
        Utils.Items.enchant(this.itemStack, enchantment, level);
        return this;
    }

    public NonMenuItem applyEnchantments(Map<Enchantment, Integer> enchants) {
        if (enchants != null) {
            this.enchantments.putAll(enchants);
            Utils.Items.enchant(this.itemStack, enchants);
        }
        return this;
    }

    public NonMenuItem applyEnchantments(ConfigurationSection section) {
        ConfigurationSection eSection = section.getConfigurationSection("enchants");
        if (eSection != null)
            eSection.getValues(false).forEach((enchant, level) -> {
                        Enchantment enchantment = Utils.getEnchantment(enchant);
                        this.enchantments.put(enchantment, (Integer) level);
                        Utils.Items.enchant(this.itemStack, enchantment, (Integer) level);
                    });
        return this;
    }

    public NonMenuItem applyModelData(ConfigurationSection section) {
        if (section.getKeys(false).contains("modeldata")) return this.setModelData(section.getInt("modeldata"));
        return this;
    }

    public NonMenuItem applyAttributes(ConfigurationSection section) {
        ConfigurationSection aSection = section.getConfigurationSection("attributes");
        if (aSection == null) return this;

        ItemMeta meta = this.getMeta();

        for (String key : aSection.getKeys(false)) {
            ConfigurationSection attributeSection = aSection.getConfigurationSection(key);
            if (attributeSection == null) continue;

            Attribute attribute = Registry.ATTRIBUTE.get(NamespacedKey.minecraft(key.toLowerCase()));
            if (attribute == null) continue;

            String stringOperation = attributeSection.getString("operation", "ADD_NUMBER").toUpperCase();
            AttributeModifier.Operation operation = Utils.getEnumValue(AttributeModifier.Operation.class, stringOperation, AttributeModifier.Operation.ADD_NUMBER);

            double value = attributeSection.getDouble("value");

            // Получаем слот из конфига
            String slotString = attributeSection.getString("slot", "HAND").toUpperCase();
            EquipmentSlotGroup group = EquipmentSlotGroup.getByName(slotString);
            if (group == null) continue;

            String name = attributeSection.getString("name", key.toLowerCase() + "_mod");

            // Создаем модификатор
            AttributeModifier modifier = new AttributeModifier(
                    NamespacedKey.minecraft(name.toLowerCase()),
                    value,
                    operation,
                    group
            );

            // ВАЖНО: Добавляем модификатор с учетом группы слотов
            meta.addAttributeModifier(attribute, modifier);
        }

        this.setMeta(meta);
        return this;
    }
    
    public NonMenuItem applyPotionEffects(ConfigurationSection section) {
        List<String> potions = section.getStringList("potion_effects");
        if (potions.isEmpty()) return this;

        for (String potion : potions) {
            String[] split = potion.split(" <S> ");
            if (split.length == 0) continue;

            PotionEffectType effectType = PotionEffectType.getByName(split[0]);
            int duration = split.length > 1 ? LunaMath.toInt(split[1],  100) : 100;
            int amplifier = split.length > 2 ? LunaMath.toInt(split[2]) : 0;

            this.setPotionEffect(effectType, duration, amplifier);
        }

        return this;
    }

    public NonMenuItem setDurability(int amount, boolean isSpentMode) {
        this.getItemStack().setDurability((short) (isSpentMode ? amount : this.material.getMaxDurability() - amount));
        return this;
    }

    public NonMenuItem setDurability(ConfigurationSection section) {
        ConfigurationSection durabilitySection = section.getConfigurationSection("durability");
        if (durabilitySection == null) {
            int durability = section.getInt("durability", -1);
            return durability >= 0 ? this.setDurability(durability, false) : this;
        }

        int durability = durabilitySection.getInt("amount");
        boolean isSpentMode = durabilitySection.getBoolean("spent");
        return this.setDurability(durability, isSpentMode);
    }

    public int getDurability() {
        return this.material.getMaxDurability() - this.getItemStack().getDurability();
    }

    public NonMenuItem setMetaColor(Color color) {
        ItemMeta meta = this.getMeta();
        if (meta instanceof LeatherArmorMeta colorMeta) {
            colorMeta.setColor(color);
        }
        else if (meta instanceof PotionMeta colorMeta) {
            colorMeta.setColor(color);
        }
        else if (meta instanceof MapMeta colorMeta) {
            colorMeta.setColor(color);
        }
        else {
            meta = null;
        }

        if (meta != null) this.itemStack.setItemMeta(meta);
        return this;
    }

    public NonMenuItem setMetaColor(int red, int green, int blue) {
        return this.setMetaColor(Color.fromRGB(red, green, blue));
    }

    public NonMenuItem setMetaColor(int rgb) {
        return this.setMetaColor(Color.fromRGB(rgb));
    }

    // red;green;blue
    public NonMenuItem setMetaColor(String rgbDelimiter) {
        if (rgbDelimiter == null || rgbDelimiter.isEmpty()) return this;
        String[] split = rgbDelimiter.split(";");
        if (split.length < 3) return this;
        else return this.setMetaColor(LunaMath.toInt(split[0]), LunaMath.toInt(split[1]), LunaMath.toInt(split[2]));
    }

    public NonMenuItem addAttribute(ItemMeta meta, @NotNull Attribute attribute, @NotNull AttributeModifier modifier) throws NoItemMetaException {
        meta.addAttributeModifier(attribute, modifier);
        return this;
    }

    public NonMenuItem addAttribute(@NotNull Attribute attribute, @NotNull AttributeModifier modifier) throws NoItemMetaException {
        ItemMeta meta = getMeta();
        this.addAttribute(meta, attribute, modifier);
        setMeta(meta);
        return this;
    }

    public void removeAttribute(Attribute attribute, AttributeModifier.Operation operation, double checkedAmount, boolean removeAll) throws NoItemMetaException {
        ItemMeta meta = this.getMeta();

        Collection<AttributeModifier> modifierMap = meta.getAttributeModifiers(attribute);
        if (modifierMap == null || modifierMap.isEmpty()) return;

        for (AttributeModifier modifier : modifierMap) {
            if (modifier.getOperation() != operation || modifier.getAmount() != checkedAmount) continue;

            meta.removeAttributeModifier(attribute, modifier);
            if (!removeAll) break;
        }
        this.itemStack.setItemMeta(meta);
    }

    // CHECKS

    /**
     * Cравнение с без учета кол-ва предмета
     */
    public boolean isSimilar(ItemStack itemStack) {
        if (itemStack == this.itemStack) return true;
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return false;
        return this.getMaterial().equals(itemStack.getType()) &&
                this.getLore().equals(meta.getLore()) &&
                this.getDisplayName().equals(meta.getDisplayName()) &&
                NBTManager.isSimilar(this.itemStack, itemStack);
    }

    /**
     * Cравнение с учетом кол-ва предмета
     *
     */
    @Override
    public boolean equals(Object item) {
        if (this == item) return true;
        if (item == null || this.getClass() != item.getClass()) return false;
        NonMenuItem that = (NonMenuItem) item;
        return this.isSimilar(that.itemStack) && that.getAmount() == this.getAmount();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getItemStack(), getId(), getMaterial(), getDisplayName(), getLore(), getAmount(), isGlowing(), getHeadValue());
    }

    @Override
    public String toString() {
        return "NonMenuItem{" +
                "itemStack=" + itemStack +
                ", id='" + id + '\'' +
                ", material=" + material +
                ", displayName='" + displayName + '\'' +
                ", lore=" + lore +
                ", amount=" + amount +
                ", glowing=" + glowing +
                ", headValue='" + headValue + '\'' +
                ", enchantments=" + enchantments +
                ", itemFlags=" + itemFlags +
                '}';
    }



    // ACTIONS

    public Item drop(Location location) {
        return location.getWorld().dropItem(location, this.getItemStack());
    }

    public Item dropNaturally(Location location) {
        return location.getWorld().dropItemNaturally(location, this.getItemStack());
    }

    public void give(@NotNull Player player) {
        this.replaceLore(lr -> PlaceholderAPI.setPlaceholders(player, lr));
        Utils.Items.give(player, this.itemStack);
    }

    public NonMenuItem serialize(@NotNull ConfigurationSection section, boolean asItemStack) {
        if (asItemStack)
            section.set("item", this.itemStack);
        else {
            section.set("material", this.getMaterial().name());
            section.set("amount", this.getAmount());
            section.set("displayName", this.getDisplayName());
            section.set("lore", this.getLore());
            section.set("enchanted", this.glowing);
            section.set("headValue", this.headValue);
            section.set("enchants", this.enchantments);
            section.set("flags", this.itemFlags);
            section.set("id", this.id);
        }
        return this;
    }

    public static NonMenuItem fromItemStack(@NotNull ItemStack stack) {
        stack = stack.clone();
        NonMenuItem nonMenuItem = new NonMenuItem(stack.getType(), stack.getAmount());

        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            nonMenuItem.displayName = meta.getDisplayName();

            List<String> lore = meta.getLore();
            if (lore != null && !lore.isEmpty()) nonMenuItem.lore = new ArrayList<>(lore);

            if (meta.hasEnchants()) nonMenuItem.glowing = true;
            nonMenuItem.itemFlags = new ArrayList<>(meta.getItemFlags());

            if (meta instanceof PotionMeta potionMeta) {
                PotionMeta nonMenuPotionMeta = nonMenuItem.getMeta(PotionMeta.class);
                if (nonMenuPotionMeta != null) {
                    nonMenuPotionMeta.setBasePotionType(potionMeta.getBasePotionType());
                    nonMenuItem.setMeta(nonMenuPotionMeta);

                    stack.setItemMeta(meta);
                    for (PotionEffect customEffect : potionMeta.getCustomEffects()) {
                        nonMenuItem.setPotionEffect(customEffect);
                    }
                }
            }
        }

        String headValue = NBTManager.getBase64FromHead(stack);
        if (headValue != null && !headValue.isEmpty()) nonMenuItem.headValue = headValue;

        nonMenuItem.enchantments = new HashMap<>(stack.getEnchantments());
        nonMenuItem.itemStack = stack;

        return nonMenuItem.update();
    }

    @Override
    public NonMenuItem clone() throws CloneNotSupportedException {
        NonMenuItem copy = (NonMenuItem) super.clone();
        copy.itemStack = this.itemStack.clone();
        copy.lore = new ArrayList<>(this.lore);
        copy.enchantments = new HashMap<>(this.enchantments);
        copy.itemFlags = new ArrayList<>(this.itemFlags);
        return copy;
    }
}