package siozy.dev.lunaspring.API.menus.updatable.tasks;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import siozy.dev.lunaspring.API.menus.updatable.UpdatableIMenu;
import siozy.dev.lunaspring.API.menus.updatable.UpdatableItem;
import siozy.dev.lunaspring.API.util.utilities.tasks.LunaTask;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter @Accessors(fluent = true)
public class UpdatableTask extends LunaTask {
    private final UpdatableIMenu updatableIMenu;
    private boolean reInsert = true;
    public UpdatableTask(UpdatableIMenu updatableIMenu, int tickDelay) {
        super(tickDelay);
        this.updatableIMenu = updatableIMenu;
    }

    public UpdatableTask(UpdatableIMenu updatableIMenu, int tickDelay, boolean reInsert) {
        this(updatableIMenu, tickDelay);
        this.reInsert = reInsert;
    }

    @Override @SneakyThrows
    @SuppressWarnings("all")
    public void start() {
        long timer = this.getTicks() * 50L;
        while (true) {
            if (!this.isActive()) return;

            this.tick();
            Thread.sleep(timer);
        }
    }

    protected void tick() {
        for (UpdatableItem item : getItems()) {
            item.tick(this.updatableIMenu);
            if (this.reInsert) item.insert(this.updatableIMenu);
        }
    }

    protected Collection<UpdatableItem> getItems() {
        return this.updatableIMenu.getItemList()
                .stream()
                .filter(i -> i instanceof UpdatableItem)
                .map(i -> (UpdatableItem) i)
                .collect(Collectors.toList());
    }

    public UpdatableTask reinsert(boolean newState) {
        this.reInsert = newState;
        return this;
    }
}
