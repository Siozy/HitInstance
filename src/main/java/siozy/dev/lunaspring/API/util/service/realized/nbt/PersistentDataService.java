package siozy.dev.lunaspring.API.util.service.realized.nbt;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class PersistentDataService implements INBTService<PersistentDataContainer> {

    private final Plugin plugin;
    private final Map<String, PersistentDataType<?, ?>> typeCache = new HashMap<>();

    public PersistentDataService(Plugin plugin) {
        this.plugin = plugin;
        initTypeCache();
    }

    private void initTypeCache() {
        typeCache.put("STRING", PersistentDataType.STRING);
        typeCache.put("INTEGER", PersistentDataType.INTEGER);
        typeCache.put("LONG", PersistentDataType.LONG);
        typeCache.put("DOUBLE", PersistentDataType.DOUBLE);
        typeCache.put("FLOAT", PersistentDataType.FLOAT);
        typeCache.put("BYTE", PersistentDataType.BYTE);
        typeCache.put("BOOLEAN", PersistentDataType.BYTE);
        typeCache.put("UUID", PersistentDataType.STRING);
        typeCache.put("ITEMSTACK", PersistentDataType.BYTE_ARRAY);
    }

    private NamespacedKey createKey(String tag) {
        String safeTag = tag.toLowerCase()
                .replace(" ", "_")
                .replace("[^a-z0-9_.-]", "");
        return new NamespacedKey(plugin, safeTag);
    }

    @Nullable
    private PersistentDataContainer getItemPDC(ItemStack item, boolean createIfNull) {
        if (item == null) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null && createIfNull) {
            meta = Bukkit.getItemFactory().getItemMeta(item.getType());
            if (meta != null) {
                item.setItemMeta(meta);
            }
        }
        return meta != null ? meta.getPersistentDataContainer() : null;
    }

    @Nullable
    private PersistentDataContainer getBlockPDC(Block block) {
        if (block == null) return null;

        BlockState state = block.getState();
        if (state instanceof PersistentDataHolder) {
            return ((PersistentDataHolder) state).getPersistentDataContainer();
        }
        return null;
    }

    private void saveBlockPDC(Block block) {
        if (block == null) return;
        BlockState state = block.getState();
        if (state instanceof TileState) {
            state.update();
        }
    }

    @Override
    public PersistentDataContainer getRoot(ItemStack item) {
        return getItemPDC(item, false);
    }

    @Override
    public String getType(ItemStack item, String key) {
        PersistentDataContainer pdc = getItemPDC(item, false);
        if (pdc == null) return null;

        NamespacedKey nk = createKey(key);
        for (Map.Entry<String, PersistentDataType<?, ?>> entry : this.typeCache.entrySet()) {
            if (pdc.has(nk, entry.getValue())) return entry.getKey();
        }
        return null;
    }

    @Override
    public boolean hasTag(ItemStack item, String tag) {
        PersistentDataContainer pdc = getItemPDC(item, false);
        if (pdc == null) return false;

        NamespacedKey key = createKey(tag);
        return this.typeCache.values().stream().anyMatch(v -> pdc.has(key, v));
    }

    @Override
    public void setString(ItemStack item, String tag, String value) {
        PersistentDataContainer pdc = getItemPDC(item, true);
        if (pdc != null) {
            pdc.set(createKey(tag), PersistentDataType.STRING, value);
            item.setItemMeta(item.getItemMeta());
        }
    }

    @Override
    public void setInt(ItemStack item, String tag, int value) {
        PersistentDataContainer pdc = getItemPDC(item, true);
        if (pdc != null) {
            pdc.set(createKey(tag), PersistentDataType.INTEGER, value);
            item.setItemMeta(item.getItemMeta());
        }
    }

    @Override
    public void setByte(ItemStack item, String tag, byte value) {
        PersistentDataContainer pdc = getItemPDC(item, true);
        if (pdc != null) {
            pdc.set(createKey(tag), PersistentDataType.BYTE, value);
            item.setItemMeta(item.getItemMeta());
        }
    }

    @Override
    public void setLong(ItemStack item, String tag, long value) {
        PersistentDataContainer pdc = getItemPDC(item, true);
        if (pdc != null) {
            pdc.set(createKey(tag), PersistentDataType.LONG, value);
            item.setItemMeta(item.getItemMeta());
        }
    }

    @Override
    public void setFloat(ItemStack item, String tag, float value) {
        PersistentDataContainer pdc = getItemPDC(item, true);
        if (pdc != null) {
            pdc.set(createKey(tag), PersistentDataType.FLOAT, value);
            item.setItemMeta(item.getItemMeta());
        }
    }

    @Override
    public void setDouble(ItemStack item, String tag, double value) {
        PersistentDataContainer pdc = getItemPDC(item, true);
        if (pdc != null) {
            pdc.set(createKey(tag), PersistentDataType.DOUBLE, value);
            item.setItemMeta(item.getItemMeta());
        }
    }

    @Override
    public void setBool(ItemStack item, String tag, boolean value) {
        setByte(item, tag, (byte) (value ? 1 : 0));
    }

    @Override
    public void setUUID(ItemStack item, String tag, UUID value) {
        if (value != null) {
            setString(item, tag, value.toString());
        }
    }

    @Override
    public void setItem(ItemStack item, String tag, ItemStack value) {
        if (value != null) {
            try {
                String serialized = Base64.getEncoder().encodeToString(value.serializeAsBytes());
                setString(item, tag, serialized);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to serialize ItemStack: " + e.getMessage());
            }
        }
    }

    @Override
    public void removeKey(ItemStack item, String key) {
        PersistentDataContainer pdc = getItemPDC(item, false);
        if (pdc != null) {
            pdc.remove(createKey(key));
            item.setItemMeta(item.getItemMeta());
        }
    }

    @Override
    public String getString(ItemStack item, String tag) {
        PersistentDataContainer pdc = getItemPDC(item, false);
        if (pdc != null) {
            return pdc.get(createKey(tag), PersistentDataType.STRING);
        }
        return null;
    }

    @Override
    public int getInt(ItemStack item, String tag) {
        PersistentDataContainer pdc = getItemPDC(item, false);
        if (pdc != null) {
            Integer value = pdc.get(createKey(tag), PersistentDataType.INTEGER);
            return value != null ? value : 0;
        }
        return 0;
    }

    @Override
    public double getDouble(ItemStack item, String tag) {
        PersistentDataContainer pdc = getItemPDC(item, false);
        if (pdc != null) {
            Double value = pdc.get(createKey(tag), PersistentDataType.DOUBLE);
            return value != null ? value : 0.0;
        }
        return 0.0;
    }

    @Override
    public byte getByte(ItemStack item, String tag) {
        PersistentDataContainer pdc = getItemPDC(item, false);
        if (pdc != null) {
            Byte value = pdc.get(createKey(tag), PersistentDataType.BYTE);
            return value != null ? value : 0;
        }
        return 0;
    }

    @Override
    public ItemStack getItemStack(ItemStack item, String tag) {
        String serialized = getString(item, tag);
        if (serialized != null && !serialized.isEmpty()) {
            try {
                byte[] data = Base64.getDecoder().decode(serialized);
                return ItemStack.deserializeBytes(data);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to deserialize ItemStack: " + e.getMessage());
            }
        }
        return null;
    }

    @Override
    public ItemStack[] getItemStacks(ItemStack item, String tag) {
        String serialized = getString(item, tag);
        if (serialized != null && !serialized.isEmpty()) {
            try {
                String[] parts = serialized.split(";;;");
                ItemStack[] result = new ItemStack[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    byte[] data = Base64.getDecoder().decode(parts[i]);
                    result[i] = ItemStack.deserializeBytes(data);
                }
                return result;
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to deserialize ItemStack array: " + e.getMessage());
            }
        }
        return new ItemStack[0];
    }

    @Override
    public long getLong(ItemStack item, String tag) {
        PersistentDataContainer pdc = getItemPDC(item, false);
        if (pdc != null) {
            Long value = pdc.get(createKey(tag), PersistentDataType.LONG);
            return value != null ? value : 0L;
        }
        return 0L;
    }

    @Override
    public boolean getBoolean(ItemStack item, String tag) {
        byte value = getByte(item, tag);
        return value == 1;
    }

    @Override
    public float getFloat(ItemStack item, String tag) {
        PersistentDataContainer pdc = getItemPDC(item, false);
        if (pdc != null) {
            Float value = pdc.get(createKey(tag), PersistentDataType.FLOAT);
            return value != null ? value : 0.0f;
        }
        return 0.0f;
    }

    @Override
    public UUID getUUID(ItemStack item, String tag) {
        String uuidStr = getString(item, tag);
        if (uuidStr != null && !uuidStr.isEmpty()) {
            try {
                return UUID.fromString(uuidStr);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID format in tag '" + tag + "': " + uuidStr);
            }
        }
        return null;
    }

    @Override
    public Set<String> getKeys(ItemStack item) {
        PersistentDataContainer pdc = getItemPDC(item, false);
        if (pdc != null) {
            return pdc.getKeys().stream()
                    .map(key -> key.getKey().replace(plugin.getName().toLowerCase() + "_", ""))
                    .collect(Collectors.toSet());
        }
        return new HashSet<>();
    }

    @Override
    public boolean isSimilar(ItemStack item1, ItemStack item2) {
        if (item1 == null || item2 == null) return false;
        if (!item1.isSimilar(item2)) return false;
        return getRoot(item1).equals(getRoot(item2));
    }

    @Override
    public boolean hasTag(Block block, String key) {
        PersistentDataContainer pdc = getBlockPDC(block);
        if (pdc == null) return false;

        NamespacedKey nk = createKey(key);
        return this.typeCache.values().stream().anyMatch(v -> pdc.has(nk, v));
    }

    @Override
    public void setInt(Block block, String key, int value) {
        PersistentDataContainer pdc = getBlockPDC(block);
        if (pdc != null) {
            pdc.set(createKey(key), PersistentDataType.INTEGER, value);
            saveBlockPDC(block);
        }
    }

    @Override
    public void setLong(Block block, String key, long value) {
        PersistentDataContainer pdc = getBlockPDC(block);
        if (pdc != null) {
            pdc.set(createKey(key), PersistentDataType.LONG, value);
            saveBlockPDC(block);
        }
    }

    @Override
    public void setDouble(Block block, String key, double value) {
        PersistentDataContainer pdc = getBlockPDC(block);
        if (pdc != null) {
            pdc.set(createKey(key), PersistentDataType.DOUBLE, value);
            saveBlockPDC(block);
        }
    }

    @Override
    public void setByte(Block block, String key, byte value) {
        PersistentDataContainer pdc = getBlockPDC(block);
        if (pdc != null) {
            pdc.set(createKey(key), PersistentDataType.BYTE, value);
            saveBlockPDC(block);
        }
    }

    @Override
    public void setString(Block block, String key, String value) {
        PersistentDataContainer pdc = getBlockPDC(block);
        if (pdc != null) {
            pdc.set(createKey(key), PersistentDataType.STRING, value);
            saveBlockPDC(block);
        }
    }

    @Override
    public void setBoolean(Block block, String key, boolean value) {
        setByte(block, key, (byte) (value ? 1 : 0));
    }

    @Override
    public void setUUID(Block block, String key, UUID value) {
        if (value != null) {
            setString(block, key, value.toString());
        }
    }

    @Override
    public void setFloat(Block block, String key, float value) {
        PersistentDataContainer pdc = getBlockPDC(block);
        if (pdc != null) {
            pdc.set(createKey(key), PersistentDataType.FLOAT, value);
            saveBlockPDC(block);
        }
    }

    @Override
    public void setItemStack(Block block, String key, ItemStack value) {
        if (value != null) {
            try {
                String serialized = Base64.getEncoder().encodeToString(value.serializeAsBytes());
                setString(block, key, serialized);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to serialize ItemStack for block: " + e.getMessage());
            }
        }
    }

    @Override
    public void removeKey(Block block, String key) {
        PersistentDataContainer pdc = getBlockPDC(block);
        if (pdc != null) {
            pdc.remove(createKey(key));
            saveBlockPDC(block);
        }
    }

    @Override
    public String getString(Block block, String tag) {
        PersistentDataContainer pdc = getBlockPDC(block);
        if (pdc != null) {
            return pdc.get(createKey(tag), PersistentDataType.STRING);
        }
        return null;
    }

    @Override
    public int getInt(Block block, String tag) {
        PersistentDataContainer pdc = getBlockPDC(block);
        if (pdc != null) {
            Integer value = pdc.get(createKey(tag), PersistentDataType.INTEGER);
            return value != null ? value : 0;
        }
        return 0;
    }

    @Override
    public double getDouble(Block block, String tag) {
        PersistentDataContainer pdc = getBlockPDC(block);
        if (pdc != null) {
            Double value = pdc.get(createKey(tag), PersistentDataType.DOUBLE);
            return value != null ? value : 0.0;
        }
        return 0.0;
    }

    @Override
    public float getFloat(Block block, String tag) {
        PersistentDataContainer pdc = getBlockPDC(block);
        if (pdc != null) {
            Float value = pdc.get(createKey(tag), PersistentDataType.FLOAT);
            return value != null ? value : 0.0f;
        }
        return 0.0f;
    }

    @Override
    public long getLong(Block block, String tag) {
        PersistentDataContainer pdc = getBlockPDC(block);
        if (pdc != null) {
            Long value = pdc.get(createKey(tag), PersistentDataType.LONG);
            return value != null ? value : 0L;
        }
        return 0L;
    }

    @Override
    public byte getByte(Block block, String tag) {
        PersistentDataContainer pdc = getBlockPDC(block);
        if (pdc != null) {
            Byte value = pdc.get(createKey(tag), PersistentDataType.BYTE);
            return value != null ? value : 0;
        }
        return 0;
    }

    @Override
    public UUID getUUID(Block block, String tag) {
        String uuidStr = getString(block, tag);
        if (uuidStr != null && !uuidStr.isEmpty()) {
            try {
                return UUID.fromString(uuidStr);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID format in block tag '" + tag + "': " + uuidStr);
            }
        }
        return null;
    }
}