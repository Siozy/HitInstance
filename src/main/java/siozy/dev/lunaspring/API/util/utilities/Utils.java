package siozy.dev.lunaspring.API.util.utilities;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import siozy.dev.lunaspring.API.menus.items.Item;
import siozy.dev.lunaspring.API.util.exceptions.SerializerException;
import siozy.dev.lunaspring.API.util.service.managers.VanishManager;
import siozy.dev.lunaspring.API.util.service.managers.ColorManager;
import siozy.dev.lunaspring.self.configuration.LSConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class Utils {
    public final UtilsObjects utilsObjects = new UtilsObjects();

    public void print(String message, boolean inCaseColoring) {
        Bukkit.getConsoleSender().sendMessage(inCaseColoring ? ColorManager.color(message) : message);
    }

    public void print(String message) {
        print(message, true);
    }

    public void print(Collection<String> messages, boolean inCaseColoring) {
        messages.forEach(l -> print(l, inCaseColoring));
    }

    public void print(Collection<String> messages) {
        print(messages, true);
    }

    public void print(@NotNull Plugin plugin, String message, boolean inCaseColoring) {
        print("[" + plugin.getName() + "] " + message, inCaseColoring);
    }

    public void print(@NotNull Plugin plugin, String message) {
        print(plugin, message, true);
    }

    public void print(@NotNull Plugin plugin, Collection<String> messages, boolean inCaseColoring) {
        messages.forEach(l -> print(plugin, l, inCaseColoring));
    }

    public void print(@NotNull Plugin plugin, Collection<String> messages) {
        print(plugin, messages, true);
    }

    /**
     * Покраска текста
     */
    public String color(String text, Collection<Character> chars) {
        if (text != null && !text.isEmpty()) {
            char[] b = text.toCharArray();
            for(int i = 0; i < b.length - 1; ++i) {
                if (chars.contains(b[i]) && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                    b[i] = 167;
                    b[i + 1] = Character.toLowerCase(b[i + 1]);
                }
            }

            return new String(b);
        }
        return null;
    }

    public String color(String text) {
        return color(text, utilsObjects.CHAR_COLORS_COLLECTION);
    }

    public void debug(String text, boolean inCaseColoring) {
        if (LSConfig.isDebugEnabled()) {
            print("LSDebug: " + text, inCaseColoring);
        }
    }

    public void debug(String text) {
        debug(text, true);
    }

    public void debug(Collection<String> text) {
        debug(text, true);
    }

    public void debug(Collection<String> text, boolean inCaseColoring) {
        if (LSConfig.isDebugEnabled()) {
            print(text.stream().map(s -> "LSDebug: " + s).toList(), inCaseColoring);
        }
    }

    public void debug(String prefix, Object... objects) {
        if (LSConfig.isDebugEnabled()) {
            for (Object object : objects) {
                System.out.println(prefix + " " + object);
            }
        }
    }

    public void debug(Object... objects) {
        debug("LSDebug:", objects);
    }

    public String leaveNumbers(String startText) {
        StringBuilder sb = new StringBuilder();
        for (char c : startText.toCharArray()) {
            if (c >= '0' && c <= '9') {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public double toDoubleVersion(String version) {
        String[] split = version.split("\\.");

        StringBuilder builder = new StringBuilder(leaveNumbers(split[0]) + ".");
        for (int i = 1; i < split.length; i++) {
            if (split[i].contains("-")) {
                String[] partSplit = split[i].split("-");
                if (partSplit.length >= 2) {
                    String result = switch (partSplit[1].toUpperCase()) {
                        case "SNAPSHOT", "SNAP", "S" -> "001";
                        case "ALPHA", "A" -> "002";
                        case "BETA", "B" -> "003";
                        case "GAMMA", "G" -> "004";
                        case "FIX", "FIXED", "F" -> "006";
                        case "RELEASE", "REL", "R" -> "007";
                        default -> "005";
                    };

                    builder.append(leaveNumbers(partSplit[0])).append(result);
                    continue;
                }
            }

            builder.append(leaveNumbers(split[i]));
        }

        return LunaMath.toDouble(builder.toString());
    }

    public <K, V, M extends Map<V, K>> M reverseMap(Map<K, V> original, Supplier<M> mapSupplier) {
        return original.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getValue,
                        Map.Entry::getKey,
                        (existing, replacement) -> existing,
                        mapSupplier
                ));
    }

    public void processCommandsWithActions(CommandSender sender, String line, String... replacements) {
        String command = Utils.applyReplacements(line, replacements);
        if (command.startsWith("[CONSOLE] ")) {
            command = setPlaceholders(sender, command.replace("[CONSOLE] ", ""));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            return;
        }

        if (command.startsWith("[PLAYER] ")) {
            command = setPlaceholders(sender, command.replace("[PLAYER] ", ""));
            Bukkit.dispatchCommand(sender, command);
            return;
        }

        if (command.startsWith("[SENDER] ")) {
            command = setPlaceholders(sender, command.replace("[SENDER] ", ""));
            Bukkit.dispatchCommand(sender, command);
            return;
        }

        AnnounceUtils.sendMessage(sender, command);
    }

    public void processCommandsWithActions(CommandSender sender, List<String> commands, String... replacements) {
        for (String line : commands) {
            processCommandsWithActions(sender, line, replacements);
        }
    }

    /**
     * Получить случайную последовательность.
     * @param size Длина строки
     * @param hasDuplicates Дублирование символов
     * @param kit Список символов
     */
    public String getRKey(byte size, String kit, boolean hasDuplicates) {
        StringBuilder endValue = new StringBuilder();
        byte kitSize = (byte) kit.length();

        if (!hasDuplicates && size > kitSize) size = kitSize;
        for (byte i = 0; i < size;) {
            char c = kit.charAt(ThreadLocalRandom.current().nextInt(kitSize));
            if (!hasDuplicates && endValue.toString().contains(String.valueOf(c))) continue;

            endValue.append(c);
            i++;
        }
        return endValue.toString();
    }

    public String getRKey(byte size) {
        return Utils.getRKey(size, true);
    }

    public String getRKey(byte size, boolean hasDuplicates) {
        return Utils.getRKey(size, utilsObjects.DEFAULT_RANDOM_KEY_KIT, hasDuplicates);
    }

    public Set<Location> generateCircleLocations(Location location,
                                                  int points,
                                                  double radius) {
        World world = location.getWorld();
        double centerX = location.getX() + 0.5;
        double centerY = location.getY() + 0.5;
        double centerZ = location.getZ() + 0.5;

        Set<Location> locations = new HashSet<>();
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;

            double x = centerX + radius * Math.cos(angle);
            double z = centerZ + radius * Math.sin(angle);

            locations.add(new Location(world, x, centerY, z));
        }

        return locations;
    }

    /**
     * Получить случайную локацию.
     * @param world Мир
     * @param maxX Ограничение по оси X
     * @param maxZ Ограничение по оси Z
     */
    public Location findRandomLocation(World world, int maxX, int maxZ) {
        if (world == null) return null;
        if (maxX == 0 || maxZ == 0) return findRandomLocation(world);

        int x = ThreadLocalRandom.current().nextInt(maxX * 2) - maxX;
        int z = ThreadLocalRandom.current().nextInt(maxZ * 2) - maxZ;
        int y = world.getHighestBlockYAt(x, z);

        return new Location(world, x, y, z);
    }
    /**
     * Получить случайную локацию.
     * Ограницениями выступают границы указанного мира.
     */
    public Location findRandomLocation(World world) {
        int border = (int) (world.getWorldBorder().getSize() / 2);
        if (border == 0) return null;

        return findRandomLocation(world, border, border);
    }

    public @Nullable Location findRandomLocations(World world,
                                        int maxX,
                                        int maxZ,
                                        final int maxAttempts,
                                        Predicate<Location> filter) {
        int attempt = maxAttempts;
        while (attempt-- > 0) {
            Location loc = findRandomLocation(world, maxX, maxZ);
            if (filter.test(loc)) return loc;
        }
        return null;
    }

    public Material getMaterial(@Nullable String string, boolean legacy) {
        if (string == null || string.isEmpty()) return null;
        return Material.matchMaterial(string, legacy);
    }

    public Material getMaterial(@Nullable String string) {
        if (string == null || string.isEmpty()) return null;
        return Material.matchMaterial(string);
    }

    public <E extends Enum<E>> E getEnumValue(@NotNull Class<E> clazz, @Nullable String string, E defaultValue) {
        if (string == null || string.isEmpty()) return defaultValue;
        try {
            return Enum.valueOf(clazz, string);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public <E extends Enum<E>> E getEnumValue(@NotNull Class<E> clazz, @Nullable String string) {
        return getEnumValue(clazz, string, null);
    }

    public Sound getSound(@Nullable String string, Sound defaultValue) {
        if (string == null || string.isEmpty()) return defaultValue;
        try {
            return Registry.SOUNDS.get(NamespacedKey.minecraft(string));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Sound getSound(@Nullable String string) {
        return getSound(string, null);
    }

    public Enchantment getEnchantment(@Nullable String string, Enchantment defaultValue) {
        if (string == null || string.isEmpty()) return defaultValue;
        try {
            return Enchantment.getByKey(NamespacedKey.minecraft(string.toLowerCase()));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Enchantment getEnchantment(@Nullable String string) {
        return getEnchantment(string, null);
    }

    public <T> @NotNull Optional<T> find(Stream<T> collection, Predicate<T> filterPredicate) {
        return collection.filter(filterPredicate).findFirst();
    }

    public <T> @NotNull Optional<T> find(Collection<T> collection, Predicate<T> filterPredicate) {
        return Utils.find(collection.stream(), filterPredicate);
    }

    /**
     * Проверка на наличие плагина.
     */
    public boolean hasPlugin(String name) {
        return Bukkit.getPluginManager().getPlugin(name) != null;
    }

    /**
     * Проверка, запущен ли плагин.
     */
    public boolean isPluginEnabled(String name) {
        return Bukkit.getPluginManager().isPluginEnabled(name);
    }

    /**
     * Получение списка предметов, созданных по одному шаблону section во всех переданных слотах.
     * @param slots Список строк, перечисляющих слоты.
     *               slots:
     *                  - '10'
     *                  - '11'
     *                  - '15'
     *                  - '19'
     *                  - '24-32'
     */
    @Deprecated
    public Set<Item> getItems(ConfigurationSection section, Collection<String> slots) {
        return getSlotList(slots).stream().map(s -> new Item(section, s)).collect(Collectors.toSet());
    }

    /**
     * Преобразование списка слотов формата ["1", "2-6", "7, 8, 10", "9"] в Set чисел формата [1, 2, 3, 4, 5, 6, ...]
     * @param slotList - начальный список слотов первого формата
     * @return List<Integer> list
     */
    public List<Integer> getSlotList(Collection<String> slotList) {
        List<Integer> set = Lists.newArrayList();
        for (String line : slotList) {
            if (line.contains("-")) {
                String[] split = line.split("-");
                for (int i = LunaMath.toInt(split[0]); i <= LunaMath.toInt(split[1]); i++) set.add(i);
            }
            else if (line.contains(",")) {
                String[] split = line.split(",");
                for (String string : split) set.add(LunaMath.toInt(string.replace(" ", "")));
            }
            else set.add(LunaMath.toInt(line));
        }
        return set;
    }

    public List<Integer> getSlotList(ConfigurationSection section) {
        List<Integer> set = getSlotList(section.getStringList("slots"));
        if (section.getKeys(false).contains("slot")) {
            set.add(section.getInt("slot"));
        }

        return set;
    }

    /**
     * Возвращает список ников всех игроков на сервере, которые начинаются со значения фильтра
     * Пример:
     *      - Список игроков: [ProGiple, NovaSparkle, Siozy, Nova, NovaBot1]
     *      - Значение фильтра: "Nov"
     *      - Вернёт: ["NovaSparkle", "Nova", "NovaBot1"]
     *
     * @param tabCompleterValueFilter - значение фильтра
     * @return List<String> list
     */
    public List<String> getPlayerNicks(String tabCompleterValueFilter, Predicate<Player> filter) {
        return tabCompleterFiltering(Bukkit.getOnlinePlayers()
                .stream()
                .filter(p -> filter == null || filter.test(p))
                .map(Player::getName).toList(),
                tabCompleterValueFilter);
    }

    public List<String> getPlayerNicks(String tabCompleterValueFilter) {
        return getPlayerNicks(tabCompleterValueFilter, (Predicate<Player>) null);
    }

    public List<String> getPlayerNicks(String tabCompleterValueFilter, CommandSender viewer) {
        return getPlayerNicks(tabCompleterValueFilter, p -> VanishManager.view(viewer, p));
    }

    public List<String> tabCompleterFiltering(Collection<String> collection, String tabCompleterValueFilter) {
        return tabCompleterFiltering(collection.stream(), tabCompleterValueFilter);
    }

    public List<String> tabCompleterFiltering(Stream<String> stream, String tabCompleterValueFilter) {
        return stream
                .filter(n -> n.toUpperCase().startsWith(tabCompleterValueFilter.toUpperCase()))
                .toList();
    }

    public void playersAction(Consumer<Player> playerConsumer, boolean containsVanished) {
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (containsVanished || !VanishManager.isVanished(p)) playerConsumer.accept(p);
        });
    }

    public void playersAction(Consumer<Player> playerConsumer) {
        playersAction(playerConsumer, true);
    }

    public String applyReplacements(String starterLine, String... replacements) {
        byte index = 0;

        String line = starterLine;
        for (String replacement : replacements) {
            if (replacement == null) continue;

            if (replacement.contains("-%-")) {
                String[] mass = replacement.split("-%-");

                String value = mass.length >= 2 ? mass[1] : "";
                line = line.replace("[" + mass[0] + "]", value);
                continue;
            }

            line = line.replace("[" + index + "]", replacement);
            index++;
        }

        return line;
    }

    public String applyLinkedReplacements(String starterLine, Object... replacements) {
        int size = replacements.length;
        if (!LunaMath.isEven(size)) size--;

        String line = starterLine;
        for (int i = 0; i < size; i++) {
            String key = "[" + replacements[i] + "]";

            Object objValue = replacements[++i];
            line = line.replace(key, objValue == null ? "" : objValue.toString());
        }

        return line;
    }

    @Deprecated
    public @NotNull EquipmentSlot getEquipmentSlot(Material material) {
        return material.getEquipmentSlot();
    }

    @Deprecated
    public String setOldPlaceholders(OfflinePlayer player, String line) {
        if (!isPluginEnabled("PlaceholderAPI") || line == null || line.isEmpty()) return line;
        return PlaceholderAPI.setPlaceholders(player, line);
    }

    public String setPlaceholders(ServerOperator sender, String line) {
        if (!isPluginEnabled("PlaceholderAPI")
                || line == null
                || line.isEmpty()
                || !(sender instanceof OfflinePlayer player)) return line;

        String previous;
        String current = line;

        int count = 0;
        do {
            previous = current;
            current = PlaceholderAPI.setPlaceholders(player, previous);
            count++;
        } while (!current.equals(previous) && count < utilsObjects.MAX_ATTEMPTS_ON_REPLACE_PLACEHOLDERS);

        return current;
    }

    public String setBracketPlaceholders(OfflinePlayer player, String line) {
        Matcher matcher = utilsObjects.PLACEHOLDER_BRACKET_PATTERN.matcher(line);

        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String raw = matcher.group(1);
            String replacement = setNakedPlaceholders(player, raw);
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(result);
        return Utils.setPlaceholders(player, result.toString());
    }


    public String setNakedPlaceholders(OfflinePlayer player, String line) {
        return setPlaceholders(player, "%" + line + "%");
    }

    public void cancelTask(int taskId) {
        if (Bukkit.getScheduler().isQueued(taskId) || Bukkit.getScheduler().isCurrentlyRunning(taskId)) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    @Deprecated
    public CompassDirection getCompassDirection(double angle) {
        return CompassDirection.of(angle);
    }

    public double getAngleFromLocations(Location from, Location to) {
        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();

        return Math.toDegrees(Math.atan2(-dz, dx));
    }

    public CompassDirection getCompassDirection(Location from, Location to) {
        return CompassDirection.of(getAngleFromLocations(from, to));
    }

    @UtilityClass @Deprecated
    public static class Time {
        public String getFormattingTime(long millis, String datePattern) {
            LocalDateTime dateTime = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Europe/Moscow")).toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);

            return dateTime.format(formatter);
        }

        /**
         * Получить следующую дату от текущей в коллекции.
         */
        public LocalTime getNextTime(Collection<LocalTime> times) {
            LocalTime now = LocalTime.now();
            return times.stream()
                    .filter(time -> time.isAfter(now))
                    .min(LocalTime::compareTo)
                    .orElseGet(() -> times.stream()
                            .min(LocalTime::compareTo)
                            .orElse(null));
        }

        public LocalTime getNextTime(List<String> times) {
            return getNextTime(times.stream().map(LocalTime::parse).collect(Collectors.toSet()));
        }

        public LocalTime parseTime(long totalSeconds) {
            long minutes = (totalSeconds % 3600) / 60;
            long hours = totalSeconds / 3600;
            long seconds = Math.max(totalSeconds - (minutes * 60 + hours * 3600), 0);
            return LocalTime.of((int) hours, (int) minutes, (int) seconds);
        }

        public String timeToString(LocalTime localTime) {
            int hours = localTime.getHour();
            int minutes = localTime.getMinute();
            int seconds = localTime.getSecond();
            return String.format("%s:%s:%s",
                    hours < 10 ? "0" + hours : hours,
                    minutes < 10 ? "0" + minutes : minutes,
                    seconds < 10 ? "0" + seconds : seconds);
        }

        /**
         * Получить время оставшееся до указанного времени в формате hh:mm.
         */
        public String getTimeBetween(LocalTime nextTime) {
            LocalTime nowTime = LocalTime.now();

            if (nextTime.isBefore(nowTime)) {
                nextTime = nextTime.plusHours(24);
            }

            Duration duration = Duration.between(nowTime, nextTime);
            long totalMinutes = duration.toMinutes();

            long hours = totalMinutes / 60;
            long minutes = totalMinutes % 60;

            return String.format("%02d:%02d", hours, minutes);
        }
    }

    @UtilityClass
    public static class Luckperms {
        public String getHighestGroup(OfflinePlayer player) {
            return setPlaceholders(player, "%luckperms_highest_group_by_weight%");
        }

        public String getGroupTime(OfflinePlayer player) {
            String highestGroup = getHighestGroup(player);
            if (highestGroup == null || highestGroup.isEmpty() || highestGroup.contains("%luckperms_")) return "∞";

            String timer = Utils.setPlaceholders(player, "%luckperms_group_expiry_time_" + highestGroup + "%");
            return timer == null || timer.isEmpty() ? "∞" : timer;
        }

        public String getFormatting(String text, TranslateType translateType, FormatType formatType) {
            if (text.length() == 1) return text;
            if (translateType == null) translateType = TranslateType.NONE;

            if (translateType != TranslateType.NONE) text = text
                        .replace("y", translateType.y)
                        .replace("mo", translateType.mo)
                        .replace("w", translateType.w)
                        .replace("d", translateType.d)
                        .replace("h", translateType.h)
                        .replace("m",translateType.m)
                        .replace("s", translateType.s);
            if (formatType == null || formatType == FormatType.NONE) return text;

            String[] parts = text.split(" ");
            long seconds = 0;
            for (String p : parts) {
                String part = p.replaceAll("\\D", "").replace(".", "");
                p = p.replaceAll("\\d", "").replace(".", "");

                long form = LunaMath.toInt(part);
                if (p.startsWith("с") || p.equalsIgnoreCase("s")) {
                    seconds += form;
                }
                else if ((p.startsWith("м") && !p.contains("с")) || p.startsWith("мин") || p.equalsIgnoreCase("m")) {
                    seconds += form * 60;
                }
                else if (p.startsWith("ч") || p.equalsIgnoreCase("h")) {
                    seconds += form * 60 * 60;
                }
                else if (p.startsWith("д") || p.equalsIgnoreCase("d")) {
                    seconds += form * 60 * 60 * 24;
                }
                else if (p.startsWith("н") || p.equalsIgnoreCase("w")) {
                    seconds += form * 60 * 60 * 24 * 7;
                }
                else if (p.startsWith("мес") || p.equalsIgnoreCase("mo")) {
                    seconds += form * 60 * 60 * 24 * 30;
                }
                else if (p.startsWith("г") || p.equalsIgnoreCase("y")) {
                    seconds += form * 60 * 60 * 24 * 30 * 12;
                }
            }

            switch (formatType) {
                case ONLY_DAYS -> {
                    int days = (int) (seconds / (60 * 60 * 24));
                    return (days > 0 ? days : "<1") + translateType.d;
                }
                case DAYS_OR_HOURS -> {
                    int days = (int) (seconds / (60 * 60 * 24));
                    int hours = (int) (seconds / 3600);
                    return days > 0 ? days + translateType.d : ((hours > 0 ? hours : "<1") + translateType.h);
                }
                case DAYS_WITH_HOURS -> {
                    int days = (int) (seconds / (60 * 60 * 24));
                    int hours = (int) ((seconds % (60 * 60 * 24)) / 3600);
                    return days > 0 ? days + translateType.d + (hours > 0 ? " " + hours + translateType.h : "") :
                            ((hours > 0 ? hours : "<1") + translateType.h);
                }
                case MAXIMAL_SC -> {
                    int years = (int) (seconds / (3600 * 24 * 30 * 12));
                    if (years > 0) return years + translateType.y;

                    int month = (int) (seconds / (3600 * 24 * 30));
                    if (month > 0) return month + translateType.mo;

                    int weeks = (int) (seconds / (3600 * 24 * 7));
                    if (weeks > 0) return weeks + translateType.w;

                    int days = (int) (seconds / (3600 * 24));
                    if (days > 0) return days + translateType.d;

                    int hours = (int) (seconds / 3600);
                    if (hours > 0) return hours + translateType.h;

                    int minutes = (int) (seconds / 60);
                    if (minutes > 0) return minutes + translateType.m;

                    return seconds + translateType.s;
                }
                case MAXIMAL_SC_DAYS -> {
                    int days = (int) (seconds / (3600 * 24));
                    if (days > 0) return days + translateType.d;

                    int hours = (int) (seconds / 3600);
                    if (hours > 0) return hours + translateType.h;

                    int minutes = (int) (seconds / 60);
                    if (minutes > 0) return minutes + translateType.m;

                    return seconds + translateType.s;
                }
            }
            return text;
        }

        public enum FormatType {
            NONE,
            ONLY_DAYS,
            DAYS_OR_HOURS,
            DAYS_WITH_HOURS,
            MAXIMAL_SC,
            MAXIMAL_SC_DAYS;
        }

        @Getter @AllArgsConstructor
        public enum TranslateType {
            NONE("y", "mo", "w", "d", "h", "m", "s"),
            ONLY_TRANSLATE("г", "мес", "нед", "д", "ч", "мин", "сек"),
            SHORT_TRANSLATE("г", "мес", "н", "д", "ч", "м", "с"),
            TRANSLATE_WITH_POINTS("г.", "мес.", "нед.", "д.", "ч.", "мин.", "сек."),
            SHORT_TRANSLATE_WITH_POINTS("г.", "мес.", "н.", "д.", "ч.", "м.", "с.");

            private final String y;
            private final String mo;
            private final String w;
            private final String d;
            private final String h;
            private final String m;
            private final String s;
        }
    }

    @UtilityClass
    public static class Base64 {
        public <E> String serialize(E object) throws SerializerException {
            if (object == null) return null;

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                 BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

                dataOutput.writeObject(object);
                return java.util.Base64.getEncoder().encodeToString(outputStream.toByteArray());
            } catch (IOException e) {
                throw new SerializerException(String.format("Объект %s невозможно сериализовать!", object.getClass().getSimpleName()));
            }
        }

        public <E> E deserialize(Class<E> dataClass, String data) throws SerializerException {
            if (data == null || data.isEmpty()) return null;

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(java.util.Base64.getDecoder().decode(data));
                 BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {

                return dataClass.cast(dataInput.readObject());
            } catch (IOException | ClassNotFoundException | ClassCastException e) {
                throw new SerializerException(String.format("Строку невозможно десериализовать в %s!", dataClass.getSimpleName()));
            }
        }

        public String serializeItemStack(ItemStack itemStack) throws SerializerException {
            return serialize(itemStack);
        }

        public ItemStack deserializeItemStack(String data) throws SerializerException {
            return deserialize(ItemStack.class, data);
        }

        public <E> String serializeList(Collection<E> items) throws SerializerException {
            if (items == null) return null;
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                 BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

                dataOutput.writeInt(items.size());
                for (E item : items) {
                    dataOutput.writeObject(item);
                }
                return java.util.Base64.getEncoder().encodeToString(outputStream.toByteArray());

            } catch (IOException e) {
                throw new SerializerException("Данную коллекцию объектов невозможно сериализовать!");
            }
        }

        public <E> List<E> deserializeList(Class<E> dataClass, String data) throws SerializerException {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(java.util.Base64.getDecoder().decode(data));
                 BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {

                int size = dataInput.readInt();
                List<E> items = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    items.add(dataClass.cast(dataInput.readObject()));
                }

                return items;

            } catch (IOException | ClassNotFoundException | ClassCastException e) {
                throw new SerializerException(String.format("Строку невозможно десериализовать в список %s!", dataClass.getSimpleName()));
            }
        }
    }

    @UtilityClass
    public static class Items {
        public ItemStack[] getStorage(Inventory inventory) {
            return inventory.getContents();
        }

        public ItemStack[] getStorage(Collection<ItemStack> collection) {
            return collection.toArray(new ItemStack[0]);
        }

        @SuppressWarnings("all")
        public int remove(@NotNull ItemStack[] storage, Predicate<ItemStack> checkFunction, final int amount) {
            int left = amount;
            for (ItemStack itemStack : storage) {
                if (left <= 0) break;

                if (itemStack == null) continue;
                if (checkFunction == null || checkFunction.test(itemStack)) {
                    int different = left - itemStack.getAmount();
                    left -= different > 0 ? itemStack.getAmount() : left;

                    itemStack.setAmount(different >= 0 ? 0 : Math.abs(different));
                }
            }

            return left;
        }

        public int remove(@NotNull ItemStack[] storage, @NotNull ItemStack similarItem, final int amount) {
            return remove(storage, i -> i != null && i.isSimilar(similarItem), amount);
        }

        public int remove(@NotNull ItemStack[] storage, @NotNull Material material, final int amount) {
            return remove(storage, i -> i != null && i.getType() == material, amount);
        }

        public Stream<ItemStack> get(@NotNull ItemStack[] storage, Predicate<ItemStack> predicate) {
            return Arrays.stream(storage).filter(predicate);
        }

        public Stream<ItemStack> get(@NotNull ItemStack[] storage, ItemStack similarItem) {
            return get(storage, i -> i != null && i.isSimilar(similarItem));
        }

        public Stream<ItemStack> get(@NotNull ItemStack[] storage, Material material) {
            return get(storage, i -> i != null && i.getType() == material);
        }

        public int getAmount(@NotNull ItemStack[] storage, Predicate<ItemStack> predicate) {
            return get(storage, predicate).mapToInt(ItemStack::getAmount).sum();
        }

        public int getAmount(@NotNull ItemStack[] storage, ItemStack similarItem) {
            return getAmount(storage, i -> i != null && i.isSimilar(similarItem));
        }

        public int getAmount(@NotNull ItemStack[] storage, Material material) {
            return getAmount(storage, i -> i != null && i.getType() == material);
        }

        public void give(@NotNull Inventory inv,
                         Collection<ItemStack> itemList,
                         boolean putOnArmor,
                         Consumer<ItemStack> leftoverConsumer) {
            putOnArmor = putOnArmor && inv instanceof PlayerInventory;
            for (ItemStack item : itemList) {
                if (item == null || item.getType().isAir()) continue;

                ItemStack toGive = item.clone();
                if (putOnArmor) {
                    PlayerInventory playerInventory = (PlayerInventory) inv;
                    EquipmentSlot slot = toGive.getType().getEquipmentSlot();

                    ItemStack slotItem = playerInventory.getItem(slot);
                    if (slot != EquipmentSlot.HAND && (slotItem == null || slotItem.getType().isAir())) {
                        playerInventory.setItem(slot, toGive);
                        continue;
                    }
                }

                HashMap<Integer, ItemStack> leftover = inv.addItem(toGive);
                if (!leftover.isEmpty()) {
                    leftover.values().forEach(leftoverConsumer);
                }
            }
        }

        public void give(@NotNull Player player, Collection<ItemStack> itemList, boolean putOnArmor) {
            give(player.getInventory(),
                    itemList,
                    putOnArmor,
                    i -> utilsObjects.DROP_ITEM_CONSUMER.accept(player.getLocation(), i));
        }

        public void give(@NotNull Player player, Collection<ItemStack> itemList) {
            give(player, itemList, true);
        }

        public void give(@NotNull Inventory inventory,
                         boolean putOnArmor,
                         Consumer<ItemStack> leftoverConsumer,
                         ItemStack... itemStacks) {
            give(inventory, List.of(itemStacks), putOnArmor, leftoverConsumer);
        }

        public void give(@NotNull Inventory inventory, Consumer<ItemStack> leftoverConsumer, ItemStack... itemStacks) {
            give(inventory, true, leftoverConsumer, itemStacks);
        }

        public void give(@NotNull Player player, ItemStack... itemStacks) {
            give(player.getInventory(),
                    false,
                    i -> utilsObjects.DROP_ITEM_CONSUMER.accept(player.getLocation(), i),
                    itemStacks);
        }

        public void enchant(ItemStack itemStack, Enchantment enchantment, int level) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta instanceof EnchantmentStorageMeta storageMeta) {
                storageMeta.addStoredEnchant(enchantment, level, true);

                itemStack.setItemMeta(storageMeta);
                return;
            }

            itemStack.addUnsafeEnchantment(enchantment, level);
        }

        public void enchant(ItemStack itemStack, Map<Enchantment, Integer> map) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta instanceof EnchantmentStorageMeta storageMeta) {
                map.forEach((e, i) -> storageMeta.addStoredEnchant(e, i, true));

                itemStack.setItemMeta(storageMeta);
                return;
            }

            itemStack.addUnsafeEnchantments(map);
        }

        public Map<Enchantment, Integer> getEnchantments(ItemStack itemStack) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta instanceof EnchantmentStorageMeta storageMeta) {
                return storageMeta.getStoredEnchants();
            }

            return itemStack.getEnchantments();
        }
    }
}