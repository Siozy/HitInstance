package siozy.dev.lunaspring.API.util.service.realized.nbt;

import de.tr7zw.nbtapi.*;
import de.tr7zw.nbtapi.iface.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;
import siozy.dev.lunaspring.API.util.service.PluginService;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class NBTService extends PluginService implements INBTService<ReadableNBT> {
    public NBTService() {
        super("NBTAPI");
    }

    public ReadableNBT getRoot(ItemStack item) {
        return NBT.readNbt(item);
    }

    public boolean hasTag(ItemStack item, String tag) {
        return getRoot(item).hasTag(tag);
    }

    public void set(ItemStack item, Consumer<ReadWriteItemNBT> consumer) {
        NBT.modify(item, consumer);
    }

    public NBTCompound getBlockData(Block block) {
        return new NBTBlock(block).getData();
    }

    public boolean hasTag(Block block, String key) {
        return getBlockData(block).hasTag(key);
    }

    public void setInt(Block block, String key, int value) {
        getBlockData(block).setInteger(key, value);
    }

    public void setLong(Block block, String key, long value) {
        getBlockData(block).setLong(key, value);
    }

    public void setDouble(Block block, String key, double value) {
        getBlockData(block).setDouble(key, value);
    }

    public void setByte(Block block, String key, byte value) {
        getBlockData(block).setByte(key, value);
    }

    public void setString(Block block, String key, String value) {
        getBlockData(block).setString(key, value);
    }

    public void setBoolean(Block block, String key, boolean value) {
        getBlockData(block).setBoolean(key, value);
    }

    public void setUUID(Block block, String key, UUID value) {
        getBlockData(block).setUUID(key, value);
    }

    public void setFloat(Block block, String key, float value) {
        getBlockData(block).setFloat(key, value);
    }

    public void setItemStack(Block block, String key, ItemStack value) {
        getBlockData(block).setItemStack(key, value);
    }

    public String getType(ItemStack item, String key) {
        return getRoot(item).getType(key).name();
    }

    public void removeKey(ItemStack item, String key) {
        this.set(item, nbt -> nbt.removeKey(key));
    }

    public void removeKey(Block block, String key) {
        this.getBlockData(block).removeKey(key);
    }

    public void setString(ItemStack item, String tag, String value) {
        this.set(item, nbt -> nbt.setString(tag, value));
    }
    public void setInt(ItemStack item, String tag, int value) {
        this.set(item, nbt -> nbt.setInteger(tag, value));
    }

    public void setByte(ItemStack item, String tag, byte value) {
        this.set(item, nbt -> nbt.setByte(tag, value));
    }

    public void setLong(ItemStack item, String tag, long value) {
        this.set(item, nbt -> nbt.setLong(tag, value));
    }

    public void setFloat(ItemStack item, String tag, float value) {
        this.set(item, nbt -> nbt.setFloat(tag, value));
    }

    public void setDouble(ItemStack item, String tag, double value) {
        this.set(item, nbt -> nbt.setDouble(tag, value));
    }

    public void setBool(ItemStack item, String tag, boolean value) {
        this.set(item, nbt -> nbt.setBoolean(tag, value));
    }

    public void setUUID(ItemStack item, String tag, UUID value) {
        this.set(item, nbt -> nbt.setUUID(tag, value));
    }

    public void setItem(ItemStack item, String tag, ItemStack value) {
        this.set(item, nbt -> nbt.setItemStack(tag, value));
    }

    public String getString(Block block, String tag) {
        return getBlockData(block).getString(tag);
    }

    public int getInt(Block block, String tag) {
        return getBlockData(block).getInteger(tag);
    }

    public double getDouble(Block block, String tag) {
        return getBlockData(block).getDouble(tag);
    }

    public float getFloat(Block block, String tag) {
        return getBlockData(block).getFloat(tag);
    }

    public long getLong(Block block, String tag) {
        return getBlockData(block).getLong(tag);
    }

    public byte getByte(Block block, String tag) {
        return getBlockData(block).getByte(tag);
    }

    public UUID getUUID(Block block, String tag) {
        return getBlockData(block).getUUID(tag);
    }

    public String getString(ItemStack item, String tag) {
        return getRoot(item).getString(tag);
    }

    public int getInt(ItemStack item, String tag) {
        return getRoot(item).getInteger(tag);
    }

    public double getDouble(ItemStack item, String tag) {
        return getRoot(item).getDouble(tag);
    }

    public byte getByte(ItemStack item, String tag) {
        return getRoot(item).getByte(tag);
    }

    public ItemStack getItemStack(ItemStack item, String tag) {
        return getRoot(item).getItemStack(tag);
    }

    public long getLong(ItemStack item, String tag) {
        return getRoot(item).getLong(tag);
    }

    public boolean getBoolean(ItemStack item, String tag) {
        return getRoot(item).getBoolean(tag);
    }

    public ItemStack[] getItemStacks(ItemStack item, String tag) {
        return getRoot(item).getItemStackArray(tag);
    }

    public float getFloat(ItemStack item, String tag) {
        return getRoot(item).getFloat(tag);
    }

    public Set<String> getKeys(ItemStack item) {
        return getRoot(item).getKeys();
    }

    public UUID getUUID(ItemStack item, String tag) {
        return getRoot(item).getUUID(tag);
    }

    public boolean isSimilar(ItemStack item1, ItemStack item2) {
        return getRoot(item1).equals(getRoot(item2));
    }

//    @Override
//    public @Nullable PlayerInventory loadInventory(UUID targetUUID) {
//        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetUUID);
//        if (offlinePlayer.isOnline() && offlinePlayer instanceof Player player) {
//            return player.getInventory();
//        }
//
//        File worldFolder = Bukkit.getWorlds().get(0).getWorldFolder();
//        File playerDataFile = new File(worldFolder, "playerdata/" + targetUUID + ".dat");
//        if (!playerDataFile.exists()) {
//            return null;
//        }
//
//        try {
//            NBTFileHandle nbtFile = NBT.getFileHandle(playerDataFile);
//            ReadWriteNBTCompoundList inventory = nbtFile.getCompoundList("Inventory");
//
//            PlayerInventory playerInventory = (PlayerInventory) Bukkit.createInventory(null, InventoryType.PLAYER);
//            for (ReadWriteNBT itemNBT : inventory) {
//                int slot = itemNBT.getByte("Slot");
//
//                ItemStack item = NBT.itemStackFromNBT(itemNBT);
//                if (slot >= 0 && slot < 36) {
//                    playerInventory.setItem(slot, item);
//                }
//                else if (slot >= 100 && slot <= 103) {
//                    // Броня
//                    int armorSlot = slot - 100;
//                    playerInventory.setItem(36 + armorSlot, item);
//                }
//                else if (slot == -106) {
//                    // оффхэнд
//                    playerInventory.setItem(40, item);
//                }
//            }
//
//            return playerInventory;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

//    @Override
//    public boolean saveInventory(UUID uuid, PlayerInventory inventory) {
//        File worldFolder = Bukkit.getWorlds().get(0).getWorldFolder();
//        File playerDataFile = new File(worldFolder, "playerdata/" + uuid + ".dat");
//        if (!playerDataFile.exists()) {
//            Bukkit.getLogger().warning("Файл данных игрока не найден: " + playerDataFile.getPath());
//            return false;
//        }
//
//        File backupFile = null;
//        try {
//            backupFile = new File(worldFolder, "playerdata/" + uuid + ".dat.backup_" + System.currentTimeMillis());
//            try {
//                java.nio.file.Files.copy(playerDataFile.toPath(), backupFile.toPath());
//            } catch (IOException e) {
//                Bukkit.getLogger().warning("Не удалось создать бэкап: " + e.getMessage());
//                return false;
//            }
//
//            NBTFile nbtFile = new NBTFile(playerDataFile);
//            NBTCompoundList inventoryList = nbtFile.getCompoundList("Inventory");
//            inventoryList.clear(); // Очищаем старый инвентарь
//
//            for (int slot = 0; slot < 36; slot++) {
//                ItemStack item = inventory.getItem(slot);
//                if (item != null) {
//                    saveItemToNBTList(inventoryList, item, slot);
//                }
//            }
//
//            // 2. Броня (слоты 100-103)
//            // Порядок в Minecraft: 100-обувь, 101-поножи, 102-нагрудник, 103-шлем
//            // В PlayerInventory: armor[0]-обувь, armor[1]-поножи, armor[2]-нагрудник, armor[3]-шлем
//            ItemStack[] armor = inventory.getArmorContents();
//            for (int i = 0; i < armor.length; i++) {
//                if (armor[i] != null) {
//                    saveItemToNBTList(inventoryList, armor[i], 100 + i);
//                }
//            }
//
//            // 3. Оффхенд (слот -106)
//            ItemStack offhand = inventory.getItemInOffHand();
//            if (offhand != null) {
//                saveItemToNBTList(inventoryList, offhand, -106);
//            }
//
//            // 4. Элитра (слот -114) - если используется
//            ItemStack chestplate = inventory.getChestplate();
//            if (chestplate != null && chestplate.getType().name().contains("ELYTRA")) {
//                // Элитра сохраняется как часть брони (слот 102)
//                // Уже сохранена выше, ничего не делаем
//            }
//
//            // Сохраняем изменения
//            nbtFile.save();
//
//            Bukkit.getLogger().info("Инвентарь сохранен в файл игрока " + offlinePlayer.getName());
//            return true;
//
//        } catch (Exception e) {
//            Bukkit.getLogger().severe("Ошибка при сохранении инвентаря: " + e.getMessage());
//            e.printStackTrace();
//
//            // Восстанавливаем из бэкапа при ошибке
//            if (backupFile != null && backupFile.exists()) {
//                try {
//                    java.nio.file.Files.copy(backupFile.toPath(), playerDataFile.toPath(),
//                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
//                    Bukkit.getLogger().info("Восстановлен бэкап для " + offlinePlayer.getName());
//                } catch (IOException ex) {
//                    Bukkit.getLogger().severe("Не удалось восстановить бэкап: " + ex.getMessage());
//                }
//            }
//
//            return false;
//        } finally {
//            // Удаляем бэкап после успешного сохранения
//            if (backupFile != null && backupFile.exists()) {
//                // Можно оставить для отладки или удалить
//                // backupFile.delete();
//            }
//        }
//    }
//
//    private void saveItemToNBTList(NBTCompoundList inventoryList, ItemStack item, int slot) {
//        try {
//            NBTItem nbtItem = new NBTItem(item, true);
//            NBTCompound itemCompound = nbtItem.getCompound();
//            NBTContainer container = new NBTContainer();
//
//            container.mergeCompound(itemCompound);
//
//            // Устанавливаем слот
//            container.setByte("Slot", (byte) slot);
//
//            // Добавляем в список
//            inventoryList.addCompound(container);
//
//        } catch (Exception e) {
//            Bukkit.getLogger().warning("Ошибка при сохранении предмета в слот " + slot + ": " + e.getMessage());
//        }
//    }
}
