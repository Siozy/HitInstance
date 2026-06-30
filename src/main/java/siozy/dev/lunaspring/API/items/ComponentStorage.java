package siozy.dev.lunaspring.API.items;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import siozy.dev.lunaspring.API.util.utilities.Utils;
import siozy.dev.lunaspring.API.util.utilities.reflection.AnnotationScanner;
import siozy.dev.lunaspring.API.util.utilities.reflection.ClassEntry;
import siozy.dev.lunaspring.LunaPlugin;

import java.util.*;
import java.util.stream.Stream;

@UtilityClass
public class ComponentStorage {
    @Getter private final Set<ItemComponent> realizedComponents = new HashSet<>();

    public void register(ItemComponent itemComponent) {
        realizedComponents.add(itemComponent);
    }

    public void register(Collection<ItemComponent> collection) {
        collection.forEach(ComponentStorage::register);
    }

    public void unregister(ItemComponent itemComponent) {
        realizedComponents.remove(itemComponent);
    }

    public void unregister(Collection<ItemComponent> collection) {
        collection.forEach(ComponentStorage::unregister);
    }

    public <C extends ItemComponent> Stream<C> getComponents(ItemStack itemStack, Class<C> componentClass) {
        return getComponents(componentClass).filter(i -> i.itemIsComponent(itemStack));
    }

    public Stream<ItemComponent> getComponents(ItemStack itemStack) {
        return realizedComponents
                .stream()
                .filter(i -> i.itemIsComponent(itemStack));
    }

    public ItemComponent getComponent(String id) {
        return Utils.find(realizedComponents, c -> c.getId().equalsIgnoreCase(id)).orElse(null);
    }

    public <C extends ItemComponent> C getComponent(ItemStack itemStack, Class<C> componentClass) {
        return getComponents(itemStack, componentClass).findFirst().orElse(null);
    }

    public <C extends ItemComponent> Stream<C> getComponents(Class<C> componentClass) {
        return realizedComponents
                .stream()
                .filter(c -> componentClass.isAssignableFrom(c.getClass()))
                .map(componentClass::cast);
    }

    @SneakyThrows
    public void loadComponents(LunaPlugin plugin, String... allowedPackages) {
        Set<ClassEntry<RealizedComponent>> entries = AnnotationScanner.findAnnotatedClasses(plugin, RealizedComponent.class, allowedPackages);
        for (ClassEntry<RealizedComponent> entry : entries) {
            if (!ItemComponent.class.isAssignableFrom(entry.getClazz())) continue;

            ItemComponent itemComponent = (ItemComponent) entry.getClazz().getDeclaredConstructor().newInstance();
            register(itemComponent);
        }
    }

    public Stream<ItemStack> scanInventory(PlayerInventory inventory, ItemComponent itemComponent) {
        Stream<ItemStack> stream;
        if (itemComponent instanceof SlotFilteringComponent sfc) {
            stream = sfc.getEnabledSlots() == null || sfc.getEnabledSlots().length == 0 ?
                    Arrays.stream(inventory.getContents()) :
                    Arrays.stream(sfc.getEnabledSlots()).filter(Objects::nonNull).map(inventory::getItem);
        } else {
            stream = Arrays.stream(inventory.getContents());
        }

        return stream.filter(i -> i != null && !i.getType().isAir() && itemComponent.itemIsComponent(i));
    }

    public <T extends ItemComponent> Stream<T> getRealizedComponents(Class<T> componentClass) {
        return realizedComponents
                .stream()
                .filter(c -> componentClass.isAssignableFrom(c.getClass()))
                .map(componentClass::cast);
    }
}
