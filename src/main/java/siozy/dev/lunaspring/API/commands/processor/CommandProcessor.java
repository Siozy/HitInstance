package siozy.dev.lunaspring.API.commands.processor;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import siozy.dev.lunaspring.API.commands.LunaCompleter;
import siozy.dev.lunaspring.API.util.utilities.Utils;
import siozy.dev.lunaspring.self.configuration.LSConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public final class CommandProcessor implements TabExecutor {
    private final List<SubCommand> subCommands;
    private final List<String> commandIdentifiers;
    @Accessors(fluent = true)
    private final String appliedCommand;
    private NoArgCommand noArgCommand;

    @SneakyThrows
    public CommandProcessor(@NotNull String appliedCommand) {
        this.subCommands = new ArrayList<>();
        this.commandIdentifiers = new ArrayList<>();
        this.appliedCommand = appliedCommand;
    }

    @Override
    @SneakyThrows
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0) {
            for (SubCommand subCommand : this.subCommands) {
                if (!subCommand.hasIdentifier(args[0])) continue;
                subCommand.invoke(sender, args);
                return true;
            }
        }

        if (this.noArgCommand != null)
            this.noArgCommand.invoke(sender, args);
        else
            LSConfig.sendMessage(sender, "wrongArguments");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if (args.length == 1) {
            List<SubCommand> subCommands = this.subCommands.stream()
                    .filter(sc ->
                            sc.getCommandIdentifiers().stream().anyMatch(identifier ->
                                            identifier.startsWith(args[0])) && sc.hasPermissionNoMessage(sender)).toList();
            if (!subCommands.isEmpty()) {
                List<String> allCommandIdentifiers = Utils.tabCompleterFiltering(subCommands.stream().flatMap(sc -> sc.getCommandIdentifiers().stream()).collect(Collectors.toList()), args[0]);
                List<String> tabCompleteIgnore = subCommands.stream().flatMap(cmd -> cmd.getCommandRequirements().tabCompleteIgnore().stream()).toList();
                return allCommandIdentifiers.stream().filter(id -> !tabCompleteIgnore.contains(id)).collect(Collectors.toList());
            }
        }
        else if (args.length >= 2) {
            SubCommand subCommand = this.subCommands.stream().filter(s -> s.hasIdentifier(args[0]) && s.hasPermissionNoMessage(sender)).findFirst().orElse(null);
            if (subCommand != null) {
                List<String> arguments = List.of(args).subList(1, args.length);
                return subCommand.tabComplete(sender, arguments);
            }
        }

        if (this.noArgCommand != null && this.noArgCommand.getInvocation() instanceof LunaCompleter completer) {
            return completer.tabComplete(sender, List.of(args).subList(1, args.length));
        }

        return null;
    }

    public void registerSubCommand(SubCommand subCommand) {
        this.subCommands.add(subCommand);
        this.commandIdentifiers.addAll(subCommand.getCommandIdentifiers());
    }

    public void registerZeroArgCommand(NoArgCommand command) {
        this.noArgCommand = command;
    }

    public boolean isEmpty() {
        return this.subCommands.isEmpty() && this.noArgCommand == null;
    }
}
