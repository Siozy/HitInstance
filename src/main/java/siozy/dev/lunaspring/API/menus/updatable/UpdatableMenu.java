package siozy.dev.lunaspring.API.menus.updatable;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import siozy.dev.lunaspring.API.menus.AMenu;
import siozy.dev.lunaspring.API.menus.updatable.tasks.OptimizedUpdatableTask;
import siozy.dev.lunaspring.API.menus.updatable.tasks.UpdatableTask;

@Getter
public class UpdatableMenu extends AMenu implements UpdatableIMenu {
    private final UpdatableTask runnable;
    public UpdatableMenu(@NotNull Player player, Settings settings) {
        super(player);
        this.runnable = this.loadTimer(settings);
    }

    public UpdatableMenu(@NotNull Player player, String title, @Range(from = 9L, to = 54) byte size, ConfigurationSection decorSection, Settings settings) {
        super(player, title, size, decorSection);
        this.runnable = this.loadTimer(settings);
    }

    public UpdatableMenu(@NotNull Player player, ConfigurationSection menuSection, Settings settings) {
        super(player, menuSection);
        this.runnable = this.loadTimer(settings);
    }

    public UpdatableMenu(@NotNull Player player, String title, @Range(from = 9L, to = 54) byte size, Settings settings) {
        super(player, title, size);
        this.runnable = this.loadTimer(settings);
    }

    private UpdatableTask loadTimer(Settings settings) {
        return settings.optimized ?
                new OptimizedUpdatableTask(this, settings.tickDelay, settings.reinsert) :
                new UpdatableTask(this, settings.tickDelay, settings.reinsert);
    }

    public record Settings(boolean optimized, int tickDelay, boolean reinsert) {};
}
