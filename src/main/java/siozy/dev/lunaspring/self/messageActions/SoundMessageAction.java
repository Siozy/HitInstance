package siozy.dev.lunaspring.self.messageActions;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import siozy.dev.lunaspring.API.util.utilities.AnnounceUtils;
import siozy.dev.lunaspring.API.messageActions.MessageAction;
import siozy.dev.lunaspring.API.messageActions.PlayerMessageAction;

@MessageAction("SOUND")
public class SoundMessageAction extends PlayerMessageAction {
    @Override
    public void execute(Player target, String line) {
        AnnounceUtils.sound(target, line);
    }
}
