package siozy.dev.lunaspring.API.util.service.managers;

import lombok.experimental.UtilityClass;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import siozy.dev.lunaspring.API.util.service.realized.nbt.NBTService;
import siozy.dev.lunaspring.API.util.service.realized.nbt.PersistentDataService;
import siozy.dev.lunaspring.API.util.service.realized.nbt.INBTService;
import siozy.dev.lunaspring.API.util.utilities.Utils;
import siozy.dev.lunaspring.LunaSpring;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@UtilityClass
public class NBTManager {
    private final INBTService<?> nbtService;
    static {
        boolean hasNBTAPI = Utils.isPluginEnabled("NBTAPI");
        nbtService = hasNBTAPI ? new NBTService() : new PersistentDataService(LunaSpring.getInstance());
    }

    public void base64head(ItemStack head, OfflinePlayer player) throws IllegalArgumentException {
        nbtService.base64head(head, player);
    }

    public void base64head(ItemStack head, String value, UUID uuid) throws IllegalArgumentException {
        nbtService.base64head(head, value, uuid);
    }

    public void base64head(ItemStack head, String value) throws IllegalArgumentException {
        nbtService.base64head(head, value);
    }

    public <E> E getRoot(ItemStack item) {
        return (E) nbtService.getRoot(item);
    }

    public boolean hasTag(ItemStack item, String tag) {
        return nbtService.hasTag(item, tag);
    }
    
    public boolean hasTag(Block block, String key) {
        return nbtService.hasTag(block, key);
    }

    public void setInt(Block block, String key, int value) {
        nbtService.setInt(block, key, value);
    }

    public void setLong(Block block, String key, long value) {
        nbtService.setLong(block, key, value);
    }

    public void setDouble(Block block, String key, double value) {
        nbtService.setDouble(block, key, value);
    }

    public void setByte(Block block, String key, byte value) {
        nbtService.setByte(block, key, value);
    }

    public void setString(Block block, String key, String value) {
        nbtService.setString(block, key, value);
    }

    public void setBool(Block block, String key, boolean value) {
        nbtService.setBoolean(block, key, value);
    }

    public void setUUID(Block block, String key, UUID value) {
        nbtService.setUUID(block, key, value);
    }

    public void setFloat(Block block, String key, float value) {
        nbtService.setFloat(block, key, value);
    }

    public void setItemStack(Block block, String key, ItemStack value) {
        nbtService.setItemStack(block, key, value);
    }

    public String getType(ItemStack item, String key) {
        return nbtService.getType(item, key);
    }

    public void removeKey(ItemStack item, String key) {
        nbtService.removeKey(item, key);
    }

    public void removeKey(Block block, String key) {
        nbtService.removeKey(block, key);
    }

    public void setString(ItemStack item, String tag, String value) {
        nbtService.setString(item, tag, value);
    }
    public void setInt(ItemStack item, String tag, int value) {
        nbtService.setInt(item, tag, value);
    }

    public void setByte(ItemStack item, String tag, byte value) {
        nbtService.setByte(item, tag, value);
    }

    public void setLong(ItemStack item, String tag, long value) {
        nbtService.setLong(item, tag, value);
    }

    public void setDouble(ItemStack item, String tag, double value) {
        nbtService.setDouble(item, tag, value);
    }

    public void setBool(ItemStack item, String tag, boolean value) {
        nbtService.setBool(item, tag, value);
    }

    public void setUUID(ItemStack item, String tag, UUID value) {
        nbtService.setUUID(item, tag, value);
    }

    public void setItem(ItemStack item, String tag, ItemStack value) {
        nbtService.setItem(item, tag, value);
    }

    public void setList(ItemStack item, String tag, List<String> stringList) {
        nbtService.setList(item, tag, stringList);
    }

    public List<String> getList(ItemStack item, String tag) {
        return nbtService.getList(item, tag);
    }

    public String getString(ItemStack item, String tag) {
        return nbtService.getString(item, tag);
    }

    public int getInt(ItemStack item, String tag) {
        return nbtService.getInt(item, tag);
    }

    public double getDouble(ItemStack item, String tag) {
        return nbtService.getDouble(item, tag);
    }

    public byte getByte(ItemStack item, String tag) {
        return nbtService.getByte(item, tag);
    }

    public ItemStack getItemStack(ItemStack item, String tag) {
        return nbtService.getItemStack(item, tag);
    }

    public long getLong(ItemStack item, String tag) {
        return nbtService.getLong(item, tag);
    }

    public boolean getBoolean(ItemStack item, String tag) {
        return nbtService.getBoolean(item, tag);
    }

    public ItemStack[] getItemStacks(ItemStack item, String tag) {
        return nbtService.getItemStacks(item, tag);
    }

    public float getFloat(ItemStack item, String tag) {
        return nbtService.getFloat(item, tag);
    }

    public Set<String> getKeys(ItemStack item) {
        return nbtService.getKeys(item);
    }

    public UUID getUUID(ItemStack item, String tag) {
        return nbtService.getUUID(item, tag);
    }

    public boolean isSimilar(ItemStack item1, ItemStack item2) {
        return nbtService.isSimilar(item1, item2);
    }

    public String getBase64FromHead(ItemStack head) {
        return nbtService.getBase64FromHead(head);
    }

    public void setList(Block block, String tag, List<String> stringList) {
        setString(block, tag, String.join(" <]- ", stringList));
    }

    public String getString(Block block, String tag) {
        return nbtService.getString(block, tag);
    }

    public int getInt(Block block, String tag) {
        return nbtService.getInt(block, tag);
    }

    public double getDouble(Block block, String tag) {
        return nbtService.getDouble(block, tag);
    }

    public float getFloat(Block block, String tag) {
        return nbtService.getFloat(block, tag);
    }

    public long getLong(Block block, String tag) {
        return nbtService.getLong(block, tag);
    }

    public byte getByte(Block block, String tag) {
        return nbtService.getByte(block, tag);
    }

    public UUID getUUID(Block block, String tag) {
        return nbtService.getUUID(block, tag);
    }

    public List<String> getList(Block block, String tag) {
        return nbtService.getList(block, tag);
    }

    public void setFloat(ItemStack item, String tag, float value) {
        nbtService.setFloat(item, tag, value);
    }
}
