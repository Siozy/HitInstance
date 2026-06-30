package siozy.dev.lunaspring.API.events.vanish;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

@Setter @Getter
public class VanishEnableEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled = false;
    private Predicate<CommandSender> checkViewPredicate;
    public VanishEnableEvent(@NotNull Player who, @Nullable Predicate<CommandSender> checkViewPredicate) {
        super(who);
        this.checkViewPredicate = checkViewPredicate;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
