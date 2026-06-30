package siozy.dev.lunaspring.API.menus.items;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import siozy.dev.lunaspring.API.menus.ItemListMenu;
import siozy.dev.lunaspring.API.util.exceptions.SlotIsNotPositiveException;
import siozy.dev.lunaspring.API.util.service.managers.NBTManager;
import siozy.dev.lunaspring.API.util.utilities.LunaMath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Accessors(chain = true, fluent = false)
@SuppressWarnings({"unused"})
public class Item extends NonMenuItem {
    public static final String MARKER_NBT = "lunaspring_marker_menu_item";
    public static final String BASEHEAD_VALUE = "b7bfad8d-6790-453a-bf3c-e1d78eca0ee5";

    @Setter protected List<String> defaultLore;
    @Setter protected String defaultName;

    protected ItemListMenu menu;
    @Setter protected byte slot = 0;

    @Builder
    public Item(Material material, String displayName, List<String> lore, int amount, @Range(from = 0, to = 53) byte slot) {
        super(material, displayName, lore, amount);
        this.slot = slot;
        this.defaultLore = new ArrayList<>(this.getLore());
        this.defaultName = this.getDisplayName();
        this.applyMenuNBT();
    }

    public Item(Material material, int amount) {
        super(material, amount);
        this.defaultLore = new ArrayList<>(this.getLore());
        this.defaultName = this.getDisplayName();
        this.applyMenuNBT();
    }

    public Item(NonMenuItem nonMenuItem, @Range(from = 0, to = 53) byte slot) {
        this(nonMenuItem.getMaterial(), nonMenuItem.getDisplayName(), nonMenuItem.getLore(), nonMenuItem.getAmount(), slot);
        ItemMeta meta = nonMenuItem.getMeta();

        if (meta.hasEnchants()) this.glowing = true;
        this.itemFlags = new ArrayList<>(meta.getItemFlags());

        if (meta instanceof PotionMeta potionMeta) {
            PotionMeta nonMenuPotionMeta = this.getMeta(PotionMeta.class);
            if (nonMenuPotionMeta != null) {
                nonMenuPotionMeta.setBasePotionType(potionMeta.getBasePotionType());

                this.setMeta(nonMenuPotionMeta);
                for (PotionEffect customEffect : potionMeta.getCustomEffects()) {
                    this.setPotionEffect(customEffect);
                }
            }
        }

        String headValue = nonMenuItem.headValue;
        if (headValue != null && !headValue.isEmpty()) this.headValue = headValue;

        this.enchantments = new HashMap<>(nonMenuItem.getEnchantments());
        this.update();
    }

    public Item() {
        super();
        this.defaultLore = new ArrayList<>(this.getLore());
        this.defaultName = this.getDisplayName();
        this.applyMenuNBT();
    }

    public Item(Material material) {
        this(material, 1);
    }

    public Item(Material material, @Range(from = 0, to = 53) byte slot) {
        this(material);
        this.slot = slot;
    }

    public Item(@NotNull ConfigurationSection section, @Range(from = 0, to = 53) int slot) {
        super(section);
        this.slot = (byte) slot;
        this.defaultLore = new ArrayList<>(this.getLore());
        this.defaultName = this.getDisplayName();
        this.applyMenuNBT();
    }

    public Item(@NotNull ConfigurationSection section) {
        this(section, section.getInt("slot"));
    }

    public Item(@NotNull ConfigurationSection section, boolean rowCol) {
        this(section, 0);
        if (rowCol)
            this.slot = (byte) LunaMath.getIndex(section.getInt("slot.row"), section.getInt("slot.column"));
        else this.slot = (byte) section.getInt("slot");
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + this.getId() + '\'' +
                ", material=" + this.getMaterial() +
                ", displayName='" + this.getDisplayName() + '\'' +
                ", lore=" + this.getLore() +
                ", amount=" + this.getAmount() +
                ", slot=" + this.slot +
                '}';
    }

    public Item insert() {
        this.insert(this.slot);
        return this;
    }

    public Item insert(@Range(from = 0, to = 53) byte slot) {
        if (this.menu != null) this.insert(this.menu, slot);
        return this;
    }

    public Item insert(@NotNull ItemListMenu itemListMenu, @Range(from = 0, to = 53) byte slot) {
        this.menu = itemListMenu;
        this.slot = slot;

        itemListMenu.getInventory().setItem(slot, this.getItemStack());
        return this;
    }

    public Item insert(@NotNull ItemListMenu itemListMenu) {
        this.insert(itemListMenu, this.slot);
        return this;
    }

    public Item insert(@NotNull ItemListMenu itemListMenu, @Range(from = 0, to = 6) byte row, @Range(from = 0, to = 9) byte column) {
        this.insert(itemListMenu, (byte) LunaMath.getIndex(row, column));
        return this;
    }

    @Override
    public Item setAll(@NotNull ConfigurationSection itemSection) throws SlotIsNotPositiveException {
        super.setAll(itemSection);
        this.defaultLore = new ArrayList<>(itemSection.getStringList("lore"));
        this.defaultName = itemSection.getString("displayName");
        return this;
    }

    public Item setAll(Material material, int amount, String displayName, List<String> lore, boolean enchanted, int slot) {
        super.setAll(material, amount, displayName, lore, enchanted);
        this.defaultLore = new ArrayList<>(lore);
        this.defaultName = displayName;
        if (slot >= 0) this.slot = (byte) slot;
        return this;
    }

    public Item setAll(Material material, int amount, String displayName, List<String> lore, boolean enchanted, int row, int column) {
        return this.setAll(material, amount, displayName, lore, enchanted, LunaMath.getIndex(row, column));
    }

    public Item setAll(Material material, int amount, String displayName, List<String> lore, boolean enchanted) {
        return this.setAll(material, amount, displayName, lore, enchanted, -1);
    }

    public Item applyMenuNBT() {
        if (!this.getClass().isAnnotationPresent(IgnoreMenuNBT.class)) {
            Item.marker(this.getItemStack());
        }

        return this;
    }

    public boolean equalsStacks(ItemStack itemStack) {
        ItemStack forCheckItem = this.getItemStack().clone();
        return itemStack.equals(Item.demarker(forCheckItem));
    }

    @Override
    public Item update() {
        super.update();
        this.applyMenuNBT();
        return this;
    }

    public Item applyPlaceholders() {
        if (this.menu == null) return this;
        this.replaceLore(lr -> PlaceholderAPI.setPlaceholders(this.menu.getPlayer(), lr));
        return this;
    }

    public Item remove(@NotNull ItemListMenu itemListMenu) {
        itemListMenu.getItemList().remove(this);
        itemListMenu.getInventory().setItem(this.slot, null);
        return this;
    }

    public Item onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        return this;
    }

    @Override
    public Item clone() throws CloneNotSupportedException {
        Item copy = (Item) super.clone();
        copy.defaultLore = new ArrayList<>(this.defaultLore);
        return copy;
    }

    public static boolean isMarkered(ItemStack itemStack) {
        return itemStack != null && !itemStack.getType().isAir() && NBTManager.hasTag(itemStack, MARKER_NBT);
    }

    public static ItemStack marker(@NotNull ItemStack itemStack) {
        NBTManager.setBool(itemStack, MARKER_NBT, true);
        return itemStack;
    }

    public static ItemStack demarker(@NotNull ItemStack itemStack) {
        NBTManager.removeKey(itemStack, MARKER_NBT);
        return itemStack;
    }
}