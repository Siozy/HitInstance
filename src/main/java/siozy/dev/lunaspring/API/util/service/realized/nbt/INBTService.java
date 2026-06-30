package siozy.dev.lunaspring.API.util.service.realized.nbt;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import siozy.dev.lunaspring.API.menus.items.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface INBTService<E> {
    default void base64head(ItemStack head, OfflinePlayer player) throws IllegalArgumentException {
        UUID uuid = player.getUniqueId();
        this.base64head(head, uuid.toString().replace("-", ""), uuid);
    }

    default void base64head(ItemStack head, String value, UUID uuid) throws IllegalArgumentException {
        if (value != null && !value.isEmpty()) {
            if (!head.getType().equals(Material.PLAYER_HEAD)) throw new IllegalArgumentException("ItemStack должен иметь материал PLAYER_HEAD! Текущий: " + head.getType().name());
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            PlayerProfile playerProfile = Bukkit.createProfile(uuid);
            playerProfile.setProperty(new ProfileProperty("textures", value));
            meta.setPlayerProfile(playerProfile);
            head.setItemMeta(meta);
        }
    }

    default void base64head(ItemStack head, String value) throws IllegalArgumentException {
        this.base64head(head, value, UUID.fromString(Item.BASEHEAD_VALUE));
    }

    E getRoot(ItemStack item);
    String getType(ItemStack item, String key);
    boolean hasTag(ItemStack item, String tag);
    boolean hasTag(Block block, String key);
    void setInt(Block block, String key, int value);
    void setLong(Block block, String key, long value);
    void setDouble(Block block, String key, double value);
    void setByte(Block block, String key, byte value);
    void setString(Block block, String key, String value);
    void setBoolean(Block block, String key, boolean value);
    void setUUID(Block block, String key, UUID value);
    void setFloat(Block block, String key, float value);
    void setItemStack(Block block, String key, ItemStack value);
    void removeKey(ItemStack item, String key);
    void removeKey(Block block, String key);
    void setString(ItemStack item, String tag, String value);
    void setInt(ItemStack item, String tag, int value);
    void setByte(ItemStack item, String tag, byte value);
    void setLong(ItemStack item, String tag, long value);
    void setFloat(ItemStack item, String tag, float value);
    void setDouble(ItemStack item, String tag, double value);
    void setBool(ItemStack item, String tag, boolean value);
    void setUUID(ItemStack item, String tag, UUID value);
    void setItem(ItemStack item, String tag, ItemStack value);
    default void setList(ItemStack item, String tag, List<String> stringList) {
        setString(item, tag, String.join(" <]- ", stringList));
    }

    default void setList(Block block, String tag, List<String> stringList) {
        setString(block, tag, String.join(" <]- ", stringList));
    }
    String getString(Block block, String tag);
    int getInt(Block block, String tag);
    double getDouble(Block block, String tag);
    float getFloat(Block block, String tag);
    long getLong(Block block, String tag);
    byte getByte(Block block, String tag);
    UUID getUUID(Block block, String tag);

    default List<String> getList(Block block, String tag) {
        String value = getString(block, tag);

        List<String> list = new ArrayList<>();
        if (value != null && !value.isEmpty()) list.addAll(List.of(value.split(" <]- ")));
        return list;
    }

    default List<String> getList(ItemStack item, String tag) {
        String value = getString(item, tag);

        List<String> list = new ArrayList<>();
        if (value != null && !value.isEmpty()) list.addAll(List.of(value.split(" <]- ")));
        return list;
    }
    String getString(ItemStack item, String tag);
    int getInt(ItemStack item, String tag);
    double getDouble(ItemStack item, String tag);
    byte getByte(ItemStack item, String tag);
    ItemStack getItemStack(ItemStack item, String tag);
    long getLong(ItemStack item, String tag);
    boolean getBoolean(ItemStack item, String tag);
    ItemStack[] getItemStacks(ItemStack item, String tag);
    float getFloat(ItemStack item, String tag);
    Set<String> getKeys(ItemStack item);
    UUID getUUID(ItemStack item, String tag);
    boolean isSimilar(ItemStack item1, ItemStack item2);

    default String getBase64FromHead(ItemStack head) {
        if (head == null || head.getType() != Material.PLAYER_HEAD) return null;

        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        PlayerProfile profile = skullMeta.getPlayerProfile();
        if (profile == null) return null;

        ProfileProperty property = profile.getProperties()
                .stream()
                .filter(p -> p.getName().equalsIgnoreCase("textures"))
                .findFirst()
                .orElse(null);
        return property == null ? null : property.getValue();
    }

//    PlayerInventory loadInventory(UUID targetUUID);
//    boolean saveInventory(UUID targetUUID, PlayerInventory inventory);
}
