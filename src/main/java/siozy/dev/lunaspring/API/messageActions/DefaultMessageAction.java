package siozy.dev.lunaspring.API.messageActions;

import org.bukkit.command.CommandSender;

public abstract class DefaultMessageAction extends AbsMessageAction<CommandSender> {
    @Override
    public boolean canCast(CommandSender sender) {
        return true;
    }

    @Override
    public CommandSender cast(CommandSender sender) {
        return sender;
    }
}
