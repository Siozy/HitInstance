package siozy.dev.lunaspring.API.configuration;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import siozy.dev.lunaspring.API.util.utilities.AnnounceUtils;

import java.io.File;
import java.util.*;

public class MessageConfig extends IConfig {
    public final Map<String, Collection<String>> messages = new HashMap<>();

    public MessageConfig(Plugin plugin) {
        super(plugin);
        reloadMessages("messages");
    }

    public MessageConfig(File container, String fileName) {
        super(container, fileName);
        reloadMessages("messages");
    }

    public MessageConfig(File file) {
        super(file);
        reloadMessages("messages");
    }

    public MessageConfig(String filePath) {
        super(filePath);
        reloadMessages("messages");
    }

    @Override
    public void reload() {
        super.reload();
        reloadMessages("messages");
    }

    @Override
    public void reload(Plugin plugin) {
        super.reload(plugin);
        reloadMessages("messages");
    }

    public void reloadMessages(String path) {
        ConfigurationSection section = this.getSection(path);
        if (section != null) loadMessagesFromSection(null, section);
    }

    protected void loadMessagesFromSection(String path, ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            List<String> msg = section.getStringList(key);

            String targetPath = path == null ? key : path + "." + key;
            if (msg.isEmpty()) {
                String msgLine = section.getString(key, null);
                if (msgLine == null) {
                    ConfigurationSection childrenSection = section.getConfigurationSection(key);
                    if (childrenSection != null) {
                        loadMessagesFromSection(targetPath, childrenSection);
                    }
                    continue;
                }

                msg.add(msgLine);
            }

            messages.put(targetPath, msg);
        }
    }

    public void clearMessages() {
        messages.clear();
    }

    @Override
    public void sendMessage(CommandSender sender, String id, String... replacements) {
        Collection<String> list = messages.get(id);
        if (list == null) return;

        AnnounceUtils.sendMessage(sender, list, replacements);
    }

    @Override
    public void sendMessage(String messagesPath, CommandSender sender, String id, String... replacements) {
        this.sendMessage(sender, messagesPath + "." + id, replacements);
    }
}
