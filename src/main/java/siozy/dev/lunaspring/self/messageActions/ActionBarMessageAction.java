package siozy.dev.lunaspring.self.messageActions;

import org.bukkit.entity.Player;
import siozy.dev.lunaspring.API.util.service.managers.ColorManager;
import siozy.dev.lunaspring.API.messageActions.MessageAction;
import siozy.dev.lunaspring.API.messageActions.PlayerMessageAction;

@MessageAction("ACTION_BAR")
public class ActionBarMessageAction extends PlayerMessageAction {
    @Override
    public void execute(Player target, String line) {
        target.sendActionBar(ColorManager.color(line));
    }
}
