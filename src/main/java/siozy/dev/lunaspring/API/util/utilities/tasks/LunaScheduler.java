package siozy.dev.lunaspring.API.util.utilities.tasks;

import lombok.Getter;
import org.bukkit.Bukkit;
import siozy.dev.lunaspring.LunaPlugin;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class LunaScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    @Getter private final Collection<String> timeStrings;
    public LunaScheduler(Collection<String> timeStrings) {
        this.timeStrings = timeStrings;
    }

    public void start(LunaPlugin plugin) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (String timeStr : this.timeStrings) {
                scheduleTask(timeStr);
            }
        });
    }

    private void scheduleTask(String timeStr) {
        LocalTime targetTime = LocalTime.parse(timeStr);
        LocalTime now = LocalTime.now();

        long initialDelay;
        if (now.isBefore(targetTime)) {
            initialDelay = now.until(targetTime, ChronoUnit.MILLIS);
        }
        else {
            initialDelay = now.until(targetTime, ChronoUnit.MILLIS) + TimeUnit.DAYS.toMillis(1);
        }

        this.scheduler.scheduleAtFixedRate(
                this::executeTask,
                initialDelay,
                TimeUnit.DAYS.toMillis(1),
                TimeUnit.MILLISECONDS
        );
    }

    protected abstract void executeTask();
}
