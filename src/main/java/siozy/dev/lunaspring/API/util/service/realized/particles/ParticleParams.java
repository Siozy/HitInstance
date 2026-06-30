package siozy.dev.lunaspring.API.util.service.realized.particles;

import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import siozy.dev.lunaspring.API.util.utilities.Utils;

public record ParticleParams(Particle particle,
                             int amount,
                             double[] params,
                             ConfigurationSection basicSection) {

    public static ParticleParams load(ConfigurationSection section) {
        if (section == null) return null;

        Particle particle = Utils.getEnumValue(Particle.class, section.getString("type"), Particle.FLAME);
        int amount = section.getInt("amount");
        double[] values = {
                section.getDouble("offsetX"),
                section.getDouble("offsetY"),
                section.getDouble("offsetZ"),
                section.getDouble("speed")
        };

        return new ParticleParams(particle, amount, values, section);
    }
}