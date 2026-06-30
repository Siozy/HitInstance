package siozy.dev.lunaspring.self.messageActions;

import org.bukkit.command.CommandSender;
import siozy.dev.lunaspring.API.util.utilities.ComponentUtils;
import siozy.dev.lunaspring.API.messageActions.DefaultMessageAction;
import siozy.dev.lunaspring.API.messageActions.MessageAction;

@MessageAction("HOVER")
public class HoverTextMessageAction extends DefaultMessageAction {
    @Override
    public void execute(CommandSender target, String line) {
        target.spigot().sendMessage(ComponentUtils.createHoverableText(line));
    }
}
