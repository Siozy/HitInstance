package siozy.dev.lunaspring.API.util.utilities;

import lombok.Setter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import siozy.dev.lunaspring.LunaPlugin;

public class LunaPAPIExpansion extends PlaceholderExpansion {
    @Setter private String identifier;
    @Setter private Request request;

    private final LunaPlugin lunaPlugin;
    public LunaPAPIExpansion(LunaPlugin lunaPlugin) {
        this(lunaPlugin, lunaPlugin.getName());
    }

    public LunaPAPIExpansion(LunaPlugin lunaPlugin, String identifier) {
        this.lunaPlugin = lunaPlugin;
        this.identifier = (identifier == null ? lunaPlugin.getName() : identifier).toLowerCase();
    }

    public LunaPAPIExpansion(LunaPlugin lunaPlugin, Request request) {
        this(lunaPlugin);
        this.request = request;
    }

    public LunaPAPIExpansion(LunaPlugin lunaPlugin, String identifier, Request request) {
        this(lunaPlugin, identifier);
        this.request = request;
    }

    @Override
    public @NotNull String getIdentifier() {
        return this.identifier;
    }

    @Override
    public @NotNull String getAuthor() {
        return this.lunaPlugin.getAuthors();
    }

    @Override
    public @NotNull String getVersion() {
        return this.lunaPlugin.getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        return this.request == null ? null : this.request.sendRequest(offlinePlayer, params);
    }

    @FunctionalInterface
    public interface Request {
        String sendRequest(OfflinePlayer offlinePlayer, String params);
    }
}
