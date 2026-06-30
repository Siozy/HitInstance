package siozy.dev.lunaspring.API.util.service.managers;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import siozy.dev.lunaspring.API.util.utilities.tasks.LunaRunnable;
import siozy.dev.lunaspring.API.util.utilities.tasks.LunaTask;
import siozy.dev.lunaspring.API.util.utilities.Utils;
import siozy.dev.lunaspring.API.util.utilities.lists.LunaList;
import siozy.dev.lunaspring.API.util.utilities.lists.LunaLists;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

@UtilityClass
public class TaskManager {
    @Getter private final List<Integer> tasksId = Lists.newArrayList();
    @Getter private final LunaList<LunaRunnable> tasks = LunaLists.newList();

    public synchronized void register(LunaRunnable task) {
        tasks.add(task);
    }

    public synchronized void register(int id) {
        tasksId.add(id);
    }

    public synchronized void unregister(LunaRunnable task) {
        tasks.remove(task);
    }

    public synchronized void unregister(Integer id) {
        tasksId.remove(id);
    }

    public synchronized boolean check(LunaRunnable task) {
        return tasks.contains(task);
    }

    public synchronized void stopAll() {
        tasksId.forEach(Utils::cancelTask);
        tasksId.clear();

        new ArrayList<>(tasks).forEach(LunaRunnable::cancel);
        tasks.clear();
    }

    public synchronized <T extends LunaRunnable> void stopAll(Class<T> clazz, Predicate<T> predicate) {
        List<T> runnables = getAllInList(clazz, predicate);
        runnables.forEach(LunaRunnable::cancel);
        tasks.removeAll(runnables);
    }

    @Deprecated
    public synchronized <T extends LunaRunnable> Stream<T> getAll(Class<T> clazz, Predicate<T> predicate) {
        return getAllInList(clazz, predicate).stream();
    }

    public <T extends LunaRunnable> List<T> getAllInList(Class<T> clazz, Predicate<T> predicate) {
        return tasks.s()
                .filter(t -> t != null && clazz.isAssignableFrom(t.getClass()))
                .map(clazz::cast)
                .filter(t -> predicate == null || predicate.test(t))
                .toList();
    }

    public <T extends LunaRunnable> Optional<T> get(Class<T> clazz, Predicate<T> predicate) {
        return getAll(clazz, predicate).findFirst();
    }
}