package siozy.dev.lunaspring.API.commands.processor;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import siozy.dev.lunaspring.API.commands.Invocation;
import siozy.dev.lunaspring.API.commands.LunaCompleter;
import siozy.dev.lunaspring.LunaPlugin;
import siozy.dev.lunaspring.self.configuration.LSConfig;

import java.util.List;

@Getter @Setter
public class SubCommand extends NoArgCommand {
    private final List<String> commandIdentifiers;
    private CommandReq commandRequirements;
    private LunaCompleter tabCompleter;

    @Builder
    public SubCommand(LunaPlugin plugin, String appliedCommand, CommandReq commandReq, String[] commandIdentifiers, Invocation invocation, LunaCompleter tabCompleter) {
        super(plugin, appliedCommand, commandReq.accessFlags(), commandReq.permissionMessagePath(), commandReq.permissions(), invocation);
        this.tabCompleter = tabCompleter;
        this.commandRequirements = commandReq;
        this.commandIdentifiers = List.of(commandIdentifiers);
    }

    public void invoke(CommandSender sender, String[] args) {
        if (this.getPermissions().isEmpty() || this.hasPermission(sender) && checkArgs(sender, args))
            this.getInvocation().invoke(sender, args);
    }

    public List<String> tabComplete(CommandSender sender, List<String> args) {
        if (this.tabCompleter != null) {
            return tabCompleter.tabComplete(sender, args);
        }
        return List.of();
    }

    private boolean checkArgs(CommandSender sender, String[] args) {
        if (this.commandRequirements.maxArgs() != Integer.MAX_VALUE && this.commandRequirements.maxArgs() < args.length) {
            LSConfig.sendMessage(sender, "tooManyArgs");
            return false;

        } else if (this.commandRequirements.minArgs() != Integer.MIN_VALUE && this.commandRequirements.minArgs() > args.length) {
            LSConfig.sendMessage(sender, "tooLowArgs");
            return false;
        }
        return true;
    }

    public boolean hasIdentifier(String inputIdentifier) {
        return this.commandIdentifiers.contains(inputIdentifier);
    }


}
