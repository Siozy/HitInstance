package siozy.dev.lunaspring.self.commands;

import org.bukkit.command.CommandSender;
import siozy.dev.lunaspring.API.commands.Invocation;
import siozy.dev.lunaspring.API.commands.annotations.Check;
import siozy.dev.lunaspring.API.commands.annotations.Permissions;
import siozy.dev.lunaspring.API.commands.annotations.SubCommand;
import siozy.dev.lunaspring.API.util.service.managers.ColorManager;
import siozy.dev.lunaspring.self.configuration.LSConfig;

@SubCommand(commandIdentifiers = {"reload"}, appliedCommand = "lunaspring")
@Permissions("#.reload")
public class ReloadSubCommand implements Invocation {
    @Override
    public void invoke(CommandSender sender, String[] args) {
        LSConfig.reload();
        ColorManager.reloadColors();
        sender.sendMessage(LSConfig.getMessage("reloaded"));
    }
}