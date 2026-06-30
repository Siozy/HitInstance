package siozy.dev.lunaspring.self.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import siozy.dev.lunaspring.API.commands.Invocation;
import siozy.dev.lunaspring.API.commands.annotations.Check;
import siozy.dev.lunaspring.API.commands.annotations.Permissions;
import siozy.dev.lunaspring.API.commands.annotations.SubCommand;
import siozy.dev.lunaspring.API.commands.annotations.TabCompleteIgnore;
import siozy.dev.lunaspring.API.util.service.managers.ColorManager;
import siozy.dev.lunaspring.API.util.utilities.AnnounceUtils;
import siozy.dev.lunaspring.LunaSpring;
import siozy.dev.lunaspring.self.configuration.LSConfig;

import java.util.List;

@SubCommand(commandIdentifiers = {"pl", "plugins"}, appliedCommand = "lunaspring")
@TabCompleteIgnore("pl")
@Permissions("#.plugins")
public class PluginsSubCommand implements Invocation {
    @Override
    public void invoke(CommandSender sender, String[] args) {
        String enabledPlugin = LSConfig.getString("pl_command.enabledPlugin");
        String disabledPlugin = LSConfig.getString("pl_command.disabledPlugin");
        String unknownAuthors = LSConfig.getString("pl_command.unknownAuthors");

        List<String> list = LSConfig.getStringList("plugins");
        for (Plugin plugin : LunaSpring.getInstance().getServer().getPluginManager().getPlugins()) {
            List<String> authorList = plugin.getDescription().getAuthors();
            String authors = authorList.isEmpty() ? unknownAuthors : String.join(", ", authorList);

            String status = plugin.isEnabled() ? enabledPlugin : disabledPlugin;
            AnnounceUtils.sendMessage(sender, list,
                    "plugin-authors-%-" + authors,
                    "plugin-name-%-" + plugin.getName(),
                    "plugin-version-%-" + plugin.getDescription().getVersion(),
                    "status-%-" + status);
        }
    }
}
