package siozy.dev.lunaspring.API.commands;

import org.bukkit.command.CommandSender;

/**
 * Интерфейс, описывающий логику исполнения команды
 * Обязателен для любой команды, созданной по аннотации
 * @ZeroArgCommand/@SubCommand
 */

@FunctionalInterface
public interface Invocation {
    void invoke(CommandSender sender, String[] args);
}
