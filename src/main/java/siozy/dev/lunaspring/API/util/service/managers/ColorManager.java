package siozy.dev.lunaspring.API.util.service.managers;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import siozy.dev.lunaspring.API.configuration.IConfig;
import siozy.dev.lunaspring.API.util.exceptions.ColorSectionException;
import siozy.dev.lunaspring.API.util.service.realized.ColorService;
import siozy.dev.lunaspring.API.util.utilities.Color;

/**
 * Менеджер для управления кастомными цветами. Подробнее в <a href="https://github.com/NovaSparkle/LunaSpring/wiki/Глава-V.-Сервисы-и-Менеджеры#colorservice--colormanager">документации</a>.
 */

@UtilityClass
public class ColorManager {
    @Getter private ColorService colorService;
    public void init(IConfig config) {
        colorService = new ColorService(config);
    }

    public <E extends Color> void reloadColors(Class<E> targetColorClass, ConfigurationSection section) throws ColorSectionException {
        colorService.reload(targetColorClass, section);
    }

    public void reloadColors() throws ColorSectionException {
        colorService.reload();
    }

    /**
     * Поскраска текста.
     */

    public static String color(String text) {
        return colorService.color(text);
    }

    /**
     * Получить цвет по аббревиатуре.
     */
    public Color getColor(@NonNull String abbr) {
        return colorService.getColor(abbr);
    }

    /**
     * Получить цвет по сокращённой аббревиатуре (например, если в конфиге стоит - \{S\}, то в данный метод надо передать {S}
     * @param replacerAbbr - формат аббревиатуры без \
     */
    public @Nullable Color getColorFromReplacer(String replacerAbbr) {
        return colorService.getColorFromReplacer(replacerAbbr);
    }

    /**
     * Определяет, содержит ли строка только цвета, или нет
     * @param description - строка
     * @param color - цветовой символ, например &
     * @return true - если содержит только цвета
     */
    public boolean containsColor(String description, char color) {
        String regex = "^(§[0-9a-fk-orA-FK-OR])+?$";
        return description.replace(color, '§').matches(regex);
    }

    /**
     * Преобразует строку с HEX-цветами (&x&R&G&B) в формат BungeeCordAPI
     * @param text - Входная строка
     * @return - Строка вида BungeeCordAPI
     */
    public String parseHexColors(String text) {
        return colorService.parseHexColors(text);
    }

    /**
     * Добавляет новый цвет color в список значений для использования, если color уже загружен через конфиг LunaSpring, то тот обновлён не будет
     * @param color - значение цвета
     * @return true - если новый color успешно добавлен
     */
    public boolean addColor(Color color) {
        return colorService.addColor(color);
    }
}
