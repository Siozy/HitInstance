package siozy.dev.lunaspring.API.util.service.managers;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.world.biome.BiomeType;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.World;
import siozy.dev.lunaspring.API.util.service.realized.WorldEditService;

import java.io.File;

@UtilityClass
public class WorldEditManager {
    private final WorldEditService weService;
    static {
        weService = new WorldEditService();
    }

    public EditSession getSession(World world) {
        return WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world));
    }

    public EditSession pasteSchematic(File schemFile, Location location, double offsetX, double offsetY, double offsetZ, boolean ignoreAirBlocks) {
        return weService.pasteSchematic(schemFile, location, offsetX, offsetY, offsetZ, ignoreAirBlocks);
    }

    public EditSession pasteSchematic(File schemFile, Location location, boolean ignoreAirBlocks) {
        return pasteSchematic(schemFile, location, 0, 0, 0, ignoreAirBlocks);
    }

    public EditSession pasteSchematic(File schemFile, Location location, double x, double y, double z) {
        return pasteSchematic(schemFile, location, x, y, z, false);
    }

    public BiomeType getBiome(int x, int y, int z, World world) {
        return weService.getBiome(x, y, z, world);
    }

    public boolean setBiome(int x, int y, int z, World world, BiomeType biomeType) {
        return weService.setBiome(x, y, z, world, biomeType);
    }

    public BiomeType getBiome(Location location) {
        return getBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld());
    }

    public boolean setBiome(Location location, BiomeType biomeType) {
        return setBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld(), biomeType);
    }

    public void cancel(Operation operation) {
        weService.cancel(operation);
    }

    public void undo(EditSession editSession, EditSession newEditSession) {
        weService.undo(editSession, newEditSession);
    }

    public void undo(EditSession editSession, World world) {
        undo(editSession, getSession(world));
    }

    public Player adapt(org.bukkit.entity.Player player) {
        return weService.adapt(player);
    }

    public com.sk89q.worldedit.world.World adapt(World world) {
        return weService.adapt(world);
    }

    public Entity adapt(org.bukkit.entity.Entity entity) {
        return weService.adapt(entity);
    }

    public com.sk89q.worldedit.util.Location adapt(Location location) {
        return weService.adapt(location);
    }

    public boolean isEnabled() {
        return weService.isEnabled();
    }
}
