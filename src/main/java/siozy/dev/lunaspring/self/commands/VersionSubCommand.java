package siozy.dev.lunaspring.self.commands;

import org.bukkit.command.CommandSender;
import siozy.dev.lunaspring.API.commands.Invocation;
import siozy.dev.lunaspring.API.commands.annotations.Permissions;
import siozy.dev.lunaspring.API.commands.annotations.SubCommand;
import siozy.dev.lunaspring.LunaSpring;
import siozy.dev.lunaspring.self.configuration.LSConfig;

@SubCommand(commandIdentifiers = {"version", "-v"}, appliedCommand = "lunaspring")
@Permissions("#.version")
public class VersionSubCommand implements Invocation {
    @Override
    public void invoke(CommandSender sender, String[] args) {
        LSConfig.sendMessage(sender, "showVersion", "pluginName-%-" + LunaSpring.getInstance().getName(), "version-%-" + LunaSpring.getInstance().getVersion());
    }
}
