package siozy.dev.lunaspring.API.util.utilities.tasks;

import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;
import siozy.dev.lunaspring.API.util.service.managers.TaskManager;

@Getter
public abstract class LunaTask extends BukkitRunnable implements LunaRunnable {
    private final long ticks;
    private volatile boolean active;

    public LunaTask(long ticks) {
        this.ticks = ticks;
    }

    public LunaTask() {
        this(0);
    }

    @Override
    public void run() {
        if (this.active) return;

        TaskManager.register(this);
        this.active = true;

        try {
            this.start();
        } catch (Exception e) {
            e.printStackTrace();
            this.cancel();
        } finally {
            this.cancel();
        }
    }

    public void cancel() {
        LunaRunnable.super.cancel();
        this.active = false;
    }
}
