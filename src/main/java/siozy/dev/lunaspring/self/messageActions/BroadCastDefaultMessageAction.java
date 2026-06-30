package siozy.dev.lunaspring.self.messageActions;

import org.bukkit.command.CommandSender;
import siozy.dev.lunaspring.API.util.service.managers.ColorManager;
import siozy.dev.lunaspring.API.util.utilities.AnnounceUtils;
import siozy.dev.lunaspring.API.messageActions.DefaultMessageAction;
import siozy.dev.lunaspring.API.messageActions.MessageAction;

@MessageAction("BROADCAST")
public class BroadCastDefaultMessageAction extends DefaultMessageAction {
    @Override
    public void execute(CommandSender target, String line) {
        AnnounceUtils.broadcast(ColorManager.color(line));
    }
}
