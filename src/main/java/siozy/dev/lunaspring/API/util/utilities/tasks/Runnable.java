package siozy.dev.lunaspring.API.util.utilities.tasks;

import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import siozy.dev.lunaspring.API.util.service.managers.TaskManager;
import siozy.dev.lunaspring.LunaPlugin;

public class Runnable extends BukkitRunnable implements LunaRunnable {
    private final java.lang.Runnable runnable;
    private boolean isRepeatingTask = false;
    @Getter
    private volatile boolean active;

    public Runnable(java.lang.Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        if (this.isCancelled()) return;
        this.start();
    }

    @Override
    public synchronized @NotNull BukkitTask runTaskTimer(@NotNull Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        isRepeatingTask = true;
        TaskManager.register(this);
        return super.runTaskTimer(plugin, delay, period);
    }

    @Override
    public synchronized @NotNull BukkitTask runTaskTimerAsynchronously(@NotNull Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        isRepeatingTask = true;
        TaskManager.register(this);
        return super.runTaskTimerAsynchronously(plugin, delay, period);
    }

    @Override
    public final void start() {
        if (isRepeatingTask) {
            this.runnable.run();
            return;
        }

        try {
            TaskManager.register(this);
            this.runnable.run();
        } catch (Throwable e) {
            this.cancel();
            e.printStackTrace();
        } finally {
            this.cancel();
        }
    }

    @Override
    public synchronized void cancel() {
        LunaRunnable.super.cancel();
        this.active = false;
    }

    public static Runnable start(java.lang.Runnable runnable) {
        return new Runnable(runnable);
    }

    public static Runnable start(LunaPlugin plugin, java.lang.Runnable runnable) {
        var r = new Runnable(runnable);
        r.runTask(plugin);
        return r;
    }

    public static Runnable startAsync(LunaPlugin plugin, java.lang.Runnable runnable) {
        var r = new Runnable(runnable);
        r.runTaskAsynchronously(plugin);
        return r;
    }
}