package siozy.dev.lunaspring.API.util.modules;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;
import siozy.dev.lunaspring.API.util.service.managers.VanishManager;
import siozy.dev.lunaspring.API.util.service.managers.VaultManager;
import siozy.dev.lunaspring.LunaPlugin;
import siozy.dev.lunaspring.LunaSpring;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Supplier;

import static org.bukkit.Bukkit.getServer;

@UtilityClass
public class Modules {
    public final Set<Class<?>> IGNORED_CLASSES = Set.of(
            Serializable.class, LunaModule.class, Cloneable.class);

    public <E extends LunaModule> boolean register(E realizedModule) {
        Class<?> clazz = null;
        for (Class<?> anInterface : realizedModule.getClass().getInterfaces()) {
            if (IGNORED_CLASSES.contains(anInterface)) continue;
            clazz = anInterface;
            break;
        }

        if (clazz == null) return false;

        safeRegister(clazz.cast(realizedModule), clazz, realizedModule.getOwnPlugin());
        return true;
    }

    public <T> void safeRegister(Object realizedModule, Class<T> targetClass, LunaPlugin plugin) {
        Bukkit.getServer().getServicesManager().register(
                targetClass,
                targetClass.cast(realizedModule),
                plugin,
                ServicePriority.Highest
        );
    }

    public <E> E provide(Class<E> targetClass, @NotNull Supplier<E> ifProviderIsNullConsumer) {
        RegisteredServiceProvider<E> registeredServiceProvider = getServer().getServicesManager().getRegistration(targetClass);
        if (registeredServiceProvider == null) {
            return ifProviderIsNullConsumer.get();
        }
        else {
            return registeredServiceProvider.getProvider();
        }
    }

    public <E> E provide(Class<E> targetClass) {
        return provide(targetClass, () -> null);
    }

    public void initializeServices(LunaSpring lunaSpring) {
        Bukkit.getScheduler().runTask(lunaSpring, () -> {
            VaultManager.initialize();
            VanishManager.initialize();
        });
    }
}
