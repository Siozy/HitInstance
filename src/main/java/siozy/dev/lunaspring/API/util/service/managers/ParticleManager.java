package siozy.dev.lunaspring.API.util.service.managers;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import siozy.dev.lunaspring.API.util.service.realized.particles.ParticleParams;
import siozy.dev.lunaspring.API.util.service.realized.particles.ParticleService;

import java.util.Collection;

@UtilityClass
public class ParticleManager {
    private final ParticleService service;
    static {
        service = new ParticleService();
    }

    public void spawnParticles(Location location, ParticleParams particles) {
        service.spawnParticles(location, particles);
    }

    public void spawnParticles(Collection<Location> particleLocations, ParticleParams particles) {
        service.spawnParticles(particleLocations, particles);
    }

    public void spawnCircle(Location location, int points, double radius, ParticleParams particles) {
        service.spawnCircle(location, points, radius, particles);
    }
}
