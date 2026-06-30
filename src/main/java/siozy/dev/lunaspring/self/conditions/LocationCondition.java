package siozy.dev.lunaspring.self.conditions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import siozy.dev.lunaspring.API.conditions.abs.ConditionId;
import siozy.dev.lunaspring.API.conditions.abs.ConditionParams;
import siozy.dev.lunaspring.API.conditions.abs.PlayerCondition;

@ConditionId("LOCATION")
@ConditionParams(
        identifiers = {"radius", "world", "x", "y", "z"},
        idClasses = {double.class, String.class, double.class, double.class, double.class})
public class LocationCondition implements PlayerCondition {
    // x;y;z;world;r

    @Override
    public boolean check(Player player, Object... objects) {
        if (objects.length < 2) return false;

        Location location;
        double radius;
        if (objects[0] instanceof Location checkLoc) {
            radius = (double) objects[1];
            location = checkLoc;
        }
        else {
            if (objects.length < 5) return false;

            World world = Bukkit.getWorld((String) objects[0]);
            if (world == null) return false;

            location = new Location(world,
                    (double) objects[1],
                    (double) objects[2],
                    (double) objects[3]);
            radius = (double) objects[4];
        }

        return player.getLocation().distance(location) <= radius;
    }

    @Override
    public Object[] generateObjects(ConfigurationSection section) {
        double radius = section.getDouble("radius");

        Location location = section.getLocation("location");
        if (location != null) {
            return new Object[]{location, radius};
        }

        return new Object[]{
                section.getString("world"),
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z"),
                radius
        };
    }
}
