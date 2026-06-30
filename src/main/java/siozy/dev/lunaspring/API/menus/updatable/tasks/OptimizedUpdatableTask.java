package siozy.dev.lunaspring.API.menus.updatable.tasks;

import siozy.dev.lunaspring.API.menus.updatable.UpdatableIMenu;
import siozy.dev.lunaspring.API.menus.updatable.UpdatableItem;

import java.util.Collection;

public class OptimizedUpdatableTask extends UpdatableTask {
    private Collection<UpdatableItem> updatableItems;

    public OptimizedUpdatableTask(UpdatableIMenu updatableIMenu, int tickDelay) {
        super(updatableIMenu, tickDelay);
    }

    public OptimizedUpdatableTask(UpdatableIMenu updatableIMenu, int tickDelay, boolean reInsert) {
        super(updatableIMenu, tickDelay, reInsert);
    }

    @Override
    protected Collection<UpdatableItem> getItems() {
        this.loadItems();
        return this.updatableItems;
    }

    protected void loadItems() {
        if (this.updatableItems == null) {
            this.updatableItems = super.getItems();
        }
    }

    @Override
    public void start() {
        this.loadItems();
        super.start();
    }
}
