package siozy.dev.lunaspring.API.items;

import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

@Component
public interface SlotFilteringComponent extends ItemComponent {
    @Nullable EquipmentSlot[] getEnabledSlots();
}
