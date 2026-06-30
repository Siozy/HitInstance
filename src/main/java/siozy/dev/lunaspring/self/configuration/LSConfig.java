package siozy.dev.lunaspring.self.configuration;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import siozy.dev.lunaspring.API.configuration.IConfig;
import siozy.dev.lunaspring.API.util.service.managers.ColorManager;
import siozy.dev.lunaspring.LunaSpring;

import java.util.List;
import java.util.stream.Collectors;


public final class LSConfig {
    @Getter private static final IConfig config;
    @Getter private static boolean debugEnabled;
    @Getter private static boolean moveCheckingEnabled;
    static {
        config = new IConfig(LunaSpring.getInstance());
        ColorManager.init(config);
        debugEnabled = getBoolean("debug");
        moveCheckingEnabled = getBoolean("moveCheckingEnabled");
    }

    public static void reload() {
        config.reload(LunaSpring.getInstance());
        debugEnabled = getBoolean("debug");
        moveCheckingEnabled = getBoolean("moveCheckingEnabled");
    }

    public static String getMessage(String path) {
        return getString(String.format("messages.%s", path));
    }

    public static List<String> getStringList(String path) {
        return config.getStringList(String.format("messages.%s", path)).stream().map(ColorManager::color).collect(Collectors.toList());
    }

    public static String getString(String path) {
        return ColorManager.color(config.getString(path));
    }

    public static boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public static void sendMessage(CommandSender sender, String id, String... rpl) {
        config.sendMessage(sender, id, rpl);
    }
    public static void sendMessage(CommandSender sender, Message message, String... rpl) {
        config.sendMessage(sender, message.getMessageId(), rpl);
    }
}
