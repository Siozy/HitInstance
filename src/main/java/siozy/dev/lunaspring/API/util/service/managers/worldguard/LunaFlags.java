package siozy.dev.lunaspring.API.util.service.managers.worldguard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import siozy.dev.lunaspring.LunaSpring;
import siozy.dev.lunaspring.self.configuration.LSConfig;

import java.util.HashMap;
import java.util.Map;

@Getter
public class LunaFlags {
    @Accessors(fluent = true) private final Map<IState, StateFlag> states;
    public LunaFlags() {
        this.states = new HashMap<>();
    }

    public FlagRegistry getRegistry() {
        return WorldGuard.getInstance().getFlagRegistry();
    }

    public void register(IState state, StateFlag stateFlag) {
        this.states.put(state, stateFlag);
    }

    public @Nullable StateFlag register(FlagRegistry registry, IState state) {
        try {
            StateFlag stateFlag = new StateFlag(state.getId(), state.defaultValue());

            registry.register(stateFlag);
            this.register(state, stateFlag);

            return stateFlag;
        }
        catch (FlagConflictException e) {
            Flag<?> existing = registry.get(state.getId());
            if (existing instanceof StateFlag stateFlag) {
                this.register(state, stateFlag);
                return stateFlag;
            }

            LunaSpring.getInstance().warning(String.format("Флаг %s конфликтует с другим типом.", state.getId()));
            return null;
        }
    }

    public @Nullable StateFlag register(IState state) {
        return this.register(getRegistry(), state);
    }

    public @Nullable StateFlag flag(String id) {
        return this.states.entrySet()
                .stream()
                .filter(e -> e.getKey().getId().equalsIgnoreCase(id))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    public @Nullable StateFlag flag(IState state) {
        return this.states.get(state);
    }

    public interface IState {
        String getId();
        boolean defaultValue();
    }

    @AllArgsConstructor @Getter
    public enum State implements IState {
        ELYTRA_GLIDE_FLAG("lsp-elytra-glide", true),
        ELYTRA_GLIDE_HARD_FLAG("lsp-elytra-glide-hard", true),
        ELYTRA_BOOST_FLAG("lsp-elytra-boost", true),
        ENABLE_FLY_FLAG("lsp-enable-fly", true),
        ENABLE_SPRINT_FLAG("lsp-enable-sprint", true),
        ENABLE_SNEAK_FLAG("lsp-enable-sneak", true),
        SHEAR_FLAG("lsp-shear-entities", true),
        BUCKET_FILL_FLAG("lsp-bukket-fill", true),
        BUCKET_EMPTY_FLAG("lsp-bukket-empty", true),
        JUMP_FLAG("lsp-jump", true),
        ITEM_DAMAGE_FLAG("lsp-damage-items", true),
        CRAFT_FLAG("lsp-enable-crafts", true),
        GET_DAMAGE_FLAG("lsp-receive-damage", true),
        USE_PORTALS_FLAG("lsp-use-portals", true),
        OPEN_INVENTORY_FLAG("lsp-open-inventory", true),
        CLICK_INVENTORY_FLAG("lsp-click-inventory", true),
        PICKUP_EXP_FLAG("lsp-pickup-exp", true),
        REGENERATE_FOOD_FLAG("lsp-regenerate-food-level", true),
        DISABLE_EXHAUST_FOOD_FLAG("lsp-disable-exhaust-food-level", false);

        private final String id;
        @Accessors(fluent = true) private final boolean defaultValue;

        public boolean check(Player player) {
            try {
                Location loc = player.getLocation();
                LunaFlags flags = GuardManager.flags();
                if (flags == null) return defaultValue;

                StateFlag stateFlag = flags.flag(State.this);
                if (stateFlag == null) return defaultValue;

                boolean b = GuardManager.checkState(loc, GuardManager.wrap(player), stateFlag);
                if (b != defaultValue) LSConfig.sendMessage(player, "flags." + id);
                return b;
            } catch (Throwable t) {
                return defaultValue;
            }
        }
    }
}
