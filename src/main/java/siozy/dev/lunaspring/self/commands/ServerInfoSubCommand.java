package siozy.dev.lunaspring.self.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import siozy.dev.lunaspring.API.commands.Invocation;
import siozy.dev.lunaspring.API.commands.annotations.Permissions;
import siozy.dev.lunaspring.API.commands.annotations.SubCommand;
import siozy.dev.lunaspring.API.util.service.managers.ColorManager;
import siozy.dev.lunaspring.self.configuration.LSConfig;

import java.util.Arrays;
import java.util.List;

@SubCommand(commandIdentifiers = {"server-info"}, appliedCommand = "lunaspring")
@Permissions("#.server-info")
public class ServerInfoSubCommand implements Invocation {

    @Override
    public void invoke(CommandSender sender, String[] args) {
        List<String> list = LSConfig.getStringList("server-info");

        int tps = (int) (Arrays.stream(Bukkit.getServer().getTPS()).sum() / Bukkit.getServer().getTPS().length);
        list.forEach(l -> {
            String line = l
                    .replace("[online]", String.valueOf(Bukkit.getOnlinePlayers().size()))
                    .replace("[max-players]", String.valueOf(Bukkit.getMaxPlayers()))
                    .replace("[bukkit-version]", Bukkit.getBukkitVersion())
                    .replace("[mc-version]", Bukkit.getMinecraftVersion())
                    .replace("[port]", String.valueOf(Bukkit.getPort()))
                    .replace("[average_tps]", String.valueOf(tps));

            sender.sendMessage(ColorManager.color(line));
        });
    }
}
