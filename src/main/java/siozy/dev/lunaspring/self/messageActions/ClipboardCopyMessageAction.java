package siozy.dev.lunaspring.self.messageActions;

import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.command.CommandSender;
import siozy.dev.lunaspring.API.util.utilities.ComponentUtils;
import siozy.dev.lunaspring.API.messageActions.DefaultMessageAction;
import siozy.dev.lunaspring.API.messageActions.MessageAction;

@MessageAction("CLIPBOARDCOPY")
public class ClipboardCopyMessageAction extends DefaultMessageAction {
    @Override
    public void execute(CommandSender target, String line) {
        target.spigot().sendMessage(ComponentUtils.createClickableText(line, ClickEvent.Action.COPY_TO_CLIPBOARD));
    }
}
