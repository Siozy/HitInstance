package siozy.dev.lunaspring.API.messageActions;

import org.bukkit.command.CommandSender;

public interface IMessageAction<E extends CommandSender> {
    String getId();
    void execute(E target, String line);
    boolean canCast(CommandSender sender);

    @SuppressWarnings("unchecked")
    default E cast(CommandSender sender) {
        return (E) sender;
    }

    static <E extends CommandSender> void safeExecute(IMessageAction<E> action, CommandSender sender, String line) {
        if (action.canCast(sender)) {
            action.execute(action.cast(sender), line);
        }
    }
}
