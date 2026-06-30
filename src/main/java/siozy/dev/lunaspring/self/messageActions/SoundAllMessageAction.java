package siozy.dev.lunaspring.self.messageActions;

import org.bukkit.command.CommandSender;
import siozy.dev.lunaspring.API.util.utilities.AnnounceUtils;
import siozy.dev.lunaspring.API.messageActions.DefaultMessageAction;
import siozy.dev.lunaspring.API.messageActions.MessageAction;

@MessageAction("SOUND_ALL")
public class SoundAllMessageAction extends DefaultMessageAction {
    @Override
    public void execute(CommandSender target, String line) {
        AnnounceUtils.soundAll(line);
    }
}
