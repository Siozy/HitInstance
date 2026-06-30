package siozy.dev.lunaspring.self.messageActions;

import org.bukkit.command.CommandSender;
import siozy.dev.lunaspring.API.messageActions.DefaultMessageAction;
import siozy.dev.lunaspring.API.messageActions.MessageAction;
import siozy.dev.lunaspring.API.util.utilities.LunaMath;
import siozy.dev.lunaspring.API.util.utilities.Utils;
import siozy.dev.lunaspring.API.util.utilities.tasks.Runnable;
import siozy.dev.lunaspring.LunaSpring;

@MessageAction("DELAY")
public class DelayMessageAction extends DefaultMessageAction {
    @Override
    public void execute(CommandSender target, String line) {
        String[] split = line.split(" ");
        if (split.length <= 1) return;

        int delay = LunaMath.toInt(split[0], 10);
        Runnable.start(() -> {
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < split.length; i++) builder.append(split[i]).append(" ");
            Utils.processCommandsWithActions(target, builder.toString());
        }).runTaskLaterAsynchronously(LunaSpring.getInstance(), delay);
    }
}
