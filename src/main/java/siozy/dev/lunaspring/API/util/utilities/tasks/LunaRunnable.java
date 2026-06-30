package siozy.dev.lunaspring.API.util.utilities.tasks;

import siozy.dev.lunaspring.API.util.service.managers.TaskManager;
import siozy.dev.lunaspring.API.util.utilities.Utils;

public interface LunaRunnable {
    int getTaskId();
    boolean isActive();
    void start();
    default void cancel() {
        if (!this.isActive()) return;
        TaskManager.unregister(this);
        try {
            int id = this.getTaskId();
            Utils.cancelTask(id);
        } catch (IllegalStateException ignored) {}
    }
}
