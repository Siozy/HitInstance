package siozy.dev.lunaspring.self.configuration;

import lombok.Getter;
import org.bukkit.command.CommandSender;

@Getter
public enum Message {
    TOO_MANY_ARGS("tooManyArgs"),
    TOO_LOW_ARGS("tooLowArgs"),
    NO_PERMISSION("noPermission"),
    INVALID_SENDER("invalidSender"),
    WRONG_ARGUMENTS("wrongArguments"),
    UNDEFINED_COMMAND("undefinedCommand"),
    NO_DEPENDENCY("noDependency");

    private final String messageId;
    Message(String messageId) {
        this.messageId = messageId;
    }

    public void send(CommandSender sender, String... replacements) {
        LSConfig.sendMessage(sender, this.messageId, replacements);
    }
}