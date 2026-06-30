package siozy.dev.lunaspring.API.configuration;

import org.bukkit.plugin.Plugin;

public class PluginConfig extends IConfig {
    private final Plugin plugin;
    public PluginConfig(Plugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void reload() {
        this.reload(plugin);
    }
}
