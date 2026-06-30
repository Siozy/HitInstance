package siozy.dev.lunaspring.self.messageActions;

import org.bukkit.entity.Player;
import siozy.dev.lunaspring.API.util.service.managers.ColorManager;
import siozy.dev.lunaspring.API.util.utilities.AnnounceUtils;
import siozy.dev.lunaspring.API.messageActions.MessageAction;
import siozy.dev.lunaspring.API.messageActions.PlayerMessageAction;

@MessageAction("TITLE")
public class TitleMessageAction extends PlayerMessageAction {
    @Override
    public void execute(Player target, String line) {
        AnnounceUtils.title(target, ColorManager.color(line));
    }
}
