package siozy.dev.lunaspring.API.configuration;

import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import siozy.dev.lunaspring.API.util.utilities.Utils;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;


public class Configuration extends IConfig {

    public Configuration(String filePath) {
        super(filePath);
    }

    public Configuration(File container, String fileName) {
        super(container, fileName);
    }

    public Configuration(File file) {
        super(file);
    }

    /**
     * Установка произвольного значения по указанному пути.
     */
    public void set(String path, Object value) {
        this.config.set(path, value);
    }

    public void setString(String path, String value) {
        this.config.set(path, value);
    }

    public void setInt(String path, int value) {
        this.config.set(path, value);
    }

    public void setBoolean(String path, boolean value) {
        this.config.set(path, value);
    }

    public void setStringList(String path, List<String> value) {
        this.config.set(path, value);
    }

    public void setIntList(String path, List<Integer> value) {
        this.config.set(path, value);
    }

    public void setMaterial(String path, Material value) {
        this.config.set(path, value.toString());
    }

    public void setItemStack(String path, ItemStack value) {
        this.config.set(path, value);
    }

    public void setSection(String path, ConfigurationSection value) {
        this.config.set(path, value);
    }

    public void setLong(String path, long value) {
        this.config.set(path, value);
    }

    public void setDouble(String path, double value) {
        this.config.set(path, value);
    }

    /**
     * Запись локации.
     * @param asLocation если true, то локация запишется в одну строку, если false, то запишется в виде секции со всеми данными.
     * @param asBlock отвечает за сами данные, если стоит true - будут записаны только мир и координаты формата integer, а если поставить на false, то
     *      будут записаны мир, координаты в формате double и значения угла обзора (Yaw + Pitch).
     */
    public void setLocation(String path, Location value, boolean asLocation, boolean asBlock) {
        if (asLocation)
            this.config.set(path, value);
        else {
            this.config.set(path + ".world", value.getWorld().getName());
            if (asBlock) {
                this.config.set(path + ".x", value.getBlockX());
                this.config.set(path + ".y", value.getBlockY());
                this.config.set(path + ".z", value.getBlockZ());
            } else {
                this.config.set(path + ".x", value.getX());
                this.config.set(path + ".y", value.getY());
                this.config.set(path + ".z", value.getZ());
                this.config.set(path + ".yaw", value.getYaw());
                this.config.set(path + ".pitch", value.getPitch());
            }
        }
    }

    /**
     * Создать новую секцию.
     * @param section секция родитель.
     * @param name название новой секции.
     */
    public ConfigurationSection createSection(ConfigurationSection section, String name) {
        return section.createSection(name);
    }

    /**
     * Создать новую секцию.
     * @param path путь до секции родителя.
     * @param name название новой секции.
     */
    public ConfigurationSection createSection(String path, String name) {
        if (path == null)
            return this.config.createSection(name);
        return this.config.createSection(String.format("%s.%s", path, name));
    }

    @SneakyThrows
    public void writeFields(String path, Object object) {
        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(IgnoredField.class)) continue;

            this.set(path + field.getName(), field.get(object));
        }
    }

    public void serialize(String path, Serializable serializable) {
        this.setString(path, Utils.Base64.serialize(serializable));
    }

    /**
     * Сохранить конфигурацию.
     */
    @SneakyThrows
    public void save() {
        this.config.save(this.getFile());
    }
}
