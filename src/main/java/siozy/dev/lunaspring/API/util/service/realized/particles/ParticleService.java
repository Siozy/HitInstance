package siozy.dev.lunaspring.API.util.service.realized.particles;

import org.bukkit.Location;
import siozy.dev.lunaspring.API.util.service.LunaService;
import siozy.dev.lunaspring.API.util.utilities.Utils;

import java.util.Collection;

public class ParticleService implements LunaService {
    public void spawnParticles(Location location, ParticleParams particles) {
        if (particles == null) return;

        double[] values = particles.params();
        location.getWorld().spawnParticle(particles.particle(),
                location,
                particles.amount(),
                values[0], values[1], values[2],
                values[3]);
    }

    public void spawnParticles(Collection<Location> particleLocations, ParticleParams particles) {
        if (particles == null) return;
        particleLocations.forEach((l) -> spawnParticles(l, particles));
    }

    public void spawnCircle(Location location, int points, double radius, ParticleParams particles) {
        this.spawnParticles(Utils.generateCircleLocations(location, points, radius), particles);
    }
}
