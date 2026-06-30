package siozy.dev.lunaspring.API.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Интерфейс, описывающий логику автозаполенения команды<br><br>
 * <b>НЕ сохраняет индексацию аргументов!</b><br>
 * То есть args.get(0) == первый аргумент после аргумента подкоманды
 */
public interface LunaCompleter {
    List<String> tabComplete(CommandSender sender, List<String> args);
}