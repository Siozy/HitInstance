package siozy.dev.lunaspring.API.menus.items;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import siozy.dev.lunaspring.API.menus.ItemListMenu;
import siozy.dev.lunaspring.API.util.service.managers.ColorManager;
import siozy.dev.lunaspring.API.util.service.managers.NBTManager;

import java.util.List;

@SuppressWarnings("deprecation")
public abstract class StoreItem extends Item {
    private ItemStack storedItem;

    public StoreItem(Material material, String displayName, List<String> lore, int amount, @Range(from = 0, to = 53) byte slot) {
        super(material, displayName, lore, amount, slot);
    }

    public StoreItem(NonMenuItem nonMenuItem, @Range(from = 0, to = 53) byte slot) {
        super(nonMenuItem, slot);
    }

    public StoreItem(@NotNull ConfigurationSection section, boolean rowCol) {
        super(section, rowCol);
    }

    public StoreItem(@NotNull ConfigurationSection section, @Range(from = 0, to = 53) int slot) {
        super(section, slot);
    }

    public StoreItem(@NotNull ConfigurationSection section) {
        super(section);
    }

    @Override
    public ItemStack getItemStack() {
        return this.storedItem == null ? getSuperItemStack() : this.storedItem;
    }

    public ItemStack getSuperItemStack() {
        return super.getItemStack();
    }

    @Override
    public Item insert() {
        ItemListMenu menu = this.getMenu();
        if (menu == null) return this;

        if (this.storedItem != null) {
            NBTManager.setBool(this.storedItem, Item.MARKER_NBT, true);
            menu.getInventory().setItem(this.slot, this.storedItem);
        }
        else {
            super.insert();
        }

        return this;
    }

    @Override
    public Item onClick(InventoryClickEvent e) {
        super.onClick(e);
        ItemStack cursor = e.getCursor();

        String name = cursor == null ? null : cursor.getItemMeta().getDisplayName();
        if (name != null) {
            if (name.isEmpty()) name = cursor.getType().name();
            else name = ColorManager.color(name);
        }

        if (this.storedItem == null) {
            this.ifStoragedItemIsNull(e, name);
        }
        else {
            this.ifStoragedItemNotNull(e, name);
        }
        return this;
    }

    protected void ifStoragedItemIsNull(InventoryClickEvent e, String itemStackName) {
        ItemStack cursor = e.getCursor();
        Player player = (Player) e.getWhoClicked();
        if (cursor == null) {
            this.sendMessage(player, MessageID.NEED_PUT_ITEMS, "player-%-" + player.getName());
            return;
        }

        if (!this.canPutItem(cursor)) {
            this.sendMessage(player, MessageID.DISABLE_PUTTING, "player-%-" + player.getName(), "item-%-" + itemStackName);
            return;
        }

        this.storedItemStack(cursor.clone());
        e.setCursor(null);

        this.sendMessage(player, MessageID.PUTTING_FULL_ITEMS,
                "player-%-" + player.getName(),
                "item-%-" + itemStackName,
                "amount-%-" + this.storedItem.getAmount());
    }

    protected void ifStoragedItemNotNull(InventoryClickEvent e, String itemStackName) {
        ItemStack cursor = e.getCursor();
        Player player = (Player) e.getWhoClicked();
        if (cursor == null) {
            e.setCursor(this.storedItem.clone());
            this.removeStoraged();

            this.sendMessage(player, MessageID.PICKUP_ALL_ITEMS, "player-%-" + player.getName());
            return;
        }

        if (this.storedItem.isSimilar(cursor)) {
            int maxStack = storedItem.getType().getMaxStackSize();
            int currentAmount = storedItem.getAmount();
            int availableSpace = maxStack - currentAmount;

            if (availableSpace <= 0) {
                sendMessage(player, MessageID.MAX_STACKED, "player-%-" + player.getName());
                return;
            }

            int cursorAmount = cursor.getAmount();
            int toTransfer = Math.min(cursorAmount, availableSpace);

            storedItem.setAmount(currentAmount + toTransfer);
            cursor.setAmount(cursorAmount - toTransfer);

            this.sendMessage(player, MessageID.PUTTING_CONCRETE_VALUE,
                    "player-%-" + player.getName(),
                    "item-%-" + itemStackName,
                    "amount-%-" + toTransfer);
        }
        else {
            if (!this.canPutItem(cursor)) {
                this.sendMessage(player, MessageID.DISABLE_PUTTING, "player-%-" + player.getName(), "item-%-" + itemStackName);
                return;
            }

            ItemStack tempStack = this.storedItemStack();
            this.storedItemStack(cursor.clone());
            e.setCursor(tempStack);

            this.sendMessage(player, MessageID.SWITCH_ITEMS,
                    "player-%-" + player.getName(),
                    "item-%-" + itemStackName,
                    "amount-%-" + this.storedItem.getAmount());
        }
    }

    protected abstract boolean canPutItem(ItemStack itemStack);
    protected abstract StoreItem sendMessage(CommandSender sender, MessageID messageID, String... rpl);

    public StoreItem removeStoraged() {
        return this.storedItemStack(null);
    }

    public StoreItem storedItemStack(@Nullable ItemStack itemStack) {
        this.storedItem = itemStack;
        this.insert();
        return this;
    }

    public ItemStack storedItemStack(boolean removeFromItem) {
        ItemStack itemStack = this.storedItem.clone();
        NBTManager.removeKey(itemStack, Item.MARKER_NBT);

        if (removeFromItem)
            this.removeStoraged();

        return itemStack;
    }

    public ItemStack storedItemStack() {
        return this.storedItemStack(false);
    }

    @Getter
    public enum MessageID {
        NEED_PUT_ITEMS("need_put_items", "player"),
        PUTTING_FULL_ITEMS("put_full_items_in_storage", "player", "item", "amount"),
        PUTTING_CONCRETE_VALUE("put_any_items_in_storage", "player"),
        DISABLE_PUTTING("disable_put_item", "player", "item"),
        MAX_STACKED("storage_is_full", "player"),
        SWITCH_ITEMS("switch_items", "player", "item", "amount"),
        PICKUP_ALL_ITEMS("pickup_all_items", "player");

        private final String messageId;
        private final String[] replacementIds;
        MessageID(String messageId, String... replacementIds) {
            this.messageId = messageId;
            this.replacementIds = replacementIds;
        }
    }
}
