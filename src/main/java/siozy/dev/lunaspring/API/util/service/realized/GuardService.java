package siozy.dev.lunaspring.API.util.service.realized;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import siozy.dev.lunaspring.API.util.exceptions.WGFlagGetException;
import siozy.dev.lunaspring.API.util.service.LunaService;
import siozy.dev.lunaspring.API.util.service.PluginService;
import siozy.dev.lunaspring.API.util.service.managers.WorldEditManager;
import siozy.dev.lunaspring.API.util.service.managers.worldguard.LFlag;
import siozy.dev.lunaspring.API.util.service.managers.worldguard.LState;
import siozy.dev.lunaspring.API.util.service.managers.worldguard.LunaFlags;
import siozy.dev.lunaspring.API.util.utilities.Utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public final class GuardService extends PluginService {
    private final LunaFlags lunaFlags;
    public GuardService() {
        super("WorldGuard");
        this.lunaFlags = isEnabled() ? new LunaFlags() : null;
    }

    public @Nullable LunaFlags flags() {
        return this.lunaFlags;
    }

    public RegionContainer getRegionContainer() {
        this.checkService();
        return WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

    public RegionManager getRegionManager(World world) {
        this.checkService();
        return this.getRegionContainer().get(WorldEditManager.adapt(world));
    }

    public ProtectedCuboidRegion createRegion(String name, Location minLoc, Location maxLoc) {
        this.checkService();
        World world = minLoc.getWorld();
        if (world != maxLoc.getWorld()) return null;

        BlockVector3 minVector = BlockVector3.at(minLoc.getBlockX(), minLoc.getBlockY(), minLoc.getBlockZ());
        BlockVector3 maxVector = BlockVector3.at(maxLoc.getBlockX(), maxLoc.getBlockY(), maxLoc.getBlockZ());
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(name, minVector, maxVector);

        this.getRegionManager(world).addRegion(region);
        return region;
    }

    public void removeRegion(String name) {
        this.checkService();
        this.getRegionManager(this.getWorld(name)).removeRegion(name);
    }

    @SuppressWarnings("all")
    public ProtectedRegion getRegion(String regionName) {
        this.checkService();
        return this.getRegionContainer().getLoaded().stream()
                .filter(regionManager -> regionManager.hasRegion(regionName))
                .map(regionManager -> regionManager.getRegion(regionName))
                .findFirst()
                .orElse(null);
    }

    public void addMember(String regionName, Player player) {
        this.checkService();
        ProtectedRegion region = this.getRegion(regionName);
        if (region == null) return;

        DefaultDomain domain = region.getMembers();
        domain.addPlayer(player.getName());
        region.setMembers(domain);
    }
    public void addMember(@NotNull ProtectedRegion region, Player player) {
        this.checkService();
        DefaultDomain domain = region.getMembers();
        domain.addPlayer(player.getName());
        region.setMembers(domain);
    }

    public void addOwner(String regionName, Player player) {
        this.checkService();
        ProtectedRegion region = this.getRegion(regionName);
        if (region == null) return;

        DefaultDomain domain = region.getOwners();
        domain.addPlayer(player.getName());
        region.setOwners(domain);
    }
    public void addOwner(@NotNull ProtectedRegion region, Player player) {
        this.checkService();
        DefaultDomain domain = region.getOwners();
        domain.addPlayer(player.getName());
        region.setOwners(domain);
    }

    public void removeMember(String regionName, Player player) {
        this.checkService();
        ProtectedRegion region = this.getRegion(regionName);
        if (region == null) return;

        DefaultDomain domain = region.getMembers();
        domain.removePlayer(player.getName());
        region.setMembers(domain);
    }

    public void removeOwner(String regionName, Player player) {
        this.checkService();
        ProtectedRegion region = this.getRegion(regionName);
        if (region == null) return;

        DefaultDomain domain = region.getOwners();
        domain.removePlayer(player.getName());
        region.setOwners(domain);
    }
    public void removeMember(@NotNull ProtectedRegion region, Player player) {
        this.checkService();
        DefaultDomain domain = region.getMembers();
        domain.removePlayer(player.getName());
        region.setMembers(domain);
    }

    public void removeOwner(@NotNull ProtectedRegion region, Player player) {
        this.checkService();
        DefaultDomain domain = region.getOwners();
        domain.removePlayer(player.getName());
        region.setOwners(domain);
    }

    @SuppressWarnings("deprecation")
    public boolean isOwner(String regionName, Player player) {
        this.checkService();
        ProtectedRegion region = this.getRegion(regionName);
        return region != null && region.isOwner(player.getName());
    }

    @SuppressWarnings("deprecation")
    public boolean isMember(String regionName, Player player) {
        this.checkService();
        ProtectedRegion region = this.getRegion(regionName);
        return region != null && region.isMember(player.getName());
    }

    @SuppressWarnings("deprecation")
    public boolean isOwner(@NotNull ProtectedRegion region, Player player) {
        this.checkService();
        return region.isOwner(player.getName());
    }

    @SuppressWarnings("deprecation")
    public boolean isMember(@NotNull ProtectedRegion region, Player player) {
        this.checkService();
        return region.isMember(player.getName());
    }

    public World getWorld(String regionName) {
        this.checkService();
        for (World world : Bukkit.getWorlds()) {
            RegionManager manager = this.getRegionManager(world);
            if (manager != null && manager.hasRegion(regionName)) return world;
        }
        return null;
    }

    public BlockVector3 getVectorPoint(String regionName, boolean isMinPoint) {
        this.checkService();
        ProtectedRegion region = this.getRegion(regionName);
        if (region == null) return null;

        return isMinPoint ? region.getMinimumPoint() : region.getMaximumPoint();
    }
    public BlockVector3 getVectorPoint(@NotNull ProtectedRegion region, boolean isMinPoint) {
        return isMinPoint ? region.getMinimumPoint() : region.getMaximumPoint();
    }

    public Location getPoint(String regionName, boolean isMinPoint) {
        this.checkService();
        BlockVector3 vector3 = this.getVectorPoint(regionName, isMinPoint);
        World world = this.getWorld(regionName);
        return world == null || vector3 == null ? null : new Location(world, vector3.getBlockX(), vector3.getBlockY(), vector3.getBlockZ());
    }
    public Location getPoint(@NotNull ProtectedRegion region, boolean isMinPoint) {
        this.checkService();
        BlockVector3 vector3 = this.getVectorPoint(region, isMinPoint);
        World world = this.getWorld(region.getId());
        return world == null || vector3 == null ? null : new Location(world, vector3.getBlockX(), vector3.getBlockY(), vector3.getBlockZ());
    }

    public boolean containsBlock(String regionName, int x, int y, int z) {
        this.checkService();
        ProtectedRegion region = this.getRegion(regionName);
        return region != null && region.contains(x, y, z);
    }
    public boolean containsBlock(@NotNull ProtectedRegion region, int x, int y, int z) {
        this.checkService();
        return region.contains(x, y, z);
    }

    public Set<UUID> getMembers(String regionName) {
        this.checkService();
        ProtectedRegion region = this.getRegion(regionName);
        return region == null ? null : region.getMembers().getUniqueIds();
    }

    public Set<UUID> getOwners(String regionName) {
        this.checkService();
        ProtectedRegion region = this.getRegion(regionName);
        return region == null ? null : region.getOwners().getUniqueIds();
    }

    public Set<UUID> getMembers(@NotNull ProtectedRegion region) {
        this.checkService();
        return region.getMembers().getUniqueIds();
    }

    public Set<UUID> getOwners(@NotNull ProtectedRegion region) {
        this.checkService();
        return region.getOwners().getUniqueIds();
    }

    public void setFlag(String regionName, StateFlag flag, StateFlag.State stateFlag) {
        this.checkService();
        ProtectedRegion region = this.getRegion(regionName);
        if (region != null)
            region.setFlag(flag, stateFlag);
    }
    public void setFlag(@NotNull ProtectedRegion region, StateFlag flag, StateFlag.State stateFlag) {
        this.checkService();
        region.setFlag(flag, stateFlag);
    }

    public void setFlag(String regionName, LFlag lFlag, StateFlag.State state) {
        this.checkService();
        this.setFlag(regionName, getWGFlag(lFlag), state);
    }

    public void setFlag(String regionName, LFlag lFlag, LState lState) {
        this.checkService();
        this.setFlag(regionName, getWGFlag(lFlag), getWGState(lState));
    }

    public void setFlag(@NotNull ProtectedRegion region, LFlag lFlag, StateFlag.State state) {
        this.checkService();
        this.setFlag(region, getWGFlag(lFlag), state);
    }

    public void setFlag(@NotNull ProtectedRegion region, LFlag lFlag, LState lState) {
        this.checkService();
        this.setFlag(region, getWGFlag(lFlag), getWGState(lState));
    }

    public Map<Flag<?>, Object> getFlags(String regionName) {
        this.checkService();
        ProtectedRegion region = this.getRegion(regionName);
        if (region == null) return null;

        return region.getFlags();
    }
    public Map<Flag<?>, Object> getFlags(@NotNull ProtectedRegion region) {
        this.checkService();
        return region.getFlags();
    }

    public Map<StateFlag, StateFlag.State> getStateFlags(String regionName) {
        this.checkService();
        Map<Flag<?>, Object> flags = this.getFlags(regionName);
        if (flags == null) return null;

        return flags.entrySet()
                .stream()
                .filter(en -> en.getKey() instanceof StateFlag && en.getValue() instanceof StateFlag.State)
                .collect(Collectors.toMap(en -> (StateFlag) en.getKey(), en -> (StateFlag.State) en.getValue()));
    }

    public Map<StateFlag, StateFlag.State> getStateFlags(@NotNull ProtectedRegion region) {
        this.checkService();
        Map<Flag<?>, Object> flags = this.getFlags(region);
        if (flags == null) return null;

        return flags.entrySet()
                .stream()
                .filter(en -> en.getKey() instanceof StateFlag && en.getValue() instanceof StateFlag.State)
                .collect(Collectors.toMap(en -> (StateFlag) en.getKey(), en -> (StateFlag.State) en.getValue()));
    }

    public Map<LFlag, LState> getLStateFlags(String regionName) {
        this.checkService();
        Map<StateFlag, StateFlag.State> flags = this.getStateFlags(regionName);
        if (flags == null) return null;

        Set<String> lFlags = Stream.of(LFlag.values()).map(Enum::name).collect(Collectors.toSet());
        return flags.entrySet()
                .stream()
                .filter(en -> lFlags.contains(en.getKey().getName()))
                .collect(Collectors.toMap(en -> LFlag.valueOf(en.getKey().getName()), en -> LState.valueOf(en.getValue().name())));
    }

    public Set<LFlag> getLStateFlags(String regionName, LState filteringState) {
        this.checkService();
        Map<LFlag, LState> flags = this.getLStateFlags(regionName);
        if (flags == null) return null;

        return flags.entrySet()
                .stream()
                .filter(en -> en.getValue() == filteringState)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
    public Map<LFlag, LState> getLStateFlags(@NotNull ProtectedRegion region) {
        this.checkService();
        Map<StateFlag, StateFlag.State> flags = this.getStateFlags(region);
        if (flags == null) return null;

        Set<String> lFlags = Stream.of(LFlag.values()).map(Enum::name).collect(Collectors.toSet());
        return flags.entrySet()
                .stream()
                .filter(en -> lFlags.contains(en.getKey().getName()))
                .collect(Collectors.toMap(en -> LFlag.valueOf(en.getKey().getName()), en -> LState.valueOf(en.getValue().name())));
    }

    public Set<LFlag> getLStateFlags(@NotNull ProtectedRegion region, LState filteringState) {
        this.checkService();
        Map<LFlag, LState> flags = this.getLStateFlags(region);
        if (flags == null) return null;

        return flags.entrySet()
                .stream()
                .filter(en -> en.getValue() == filteringState)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public long getCount(Player player) {
        this.checkService();
        long amo = 0;
        for (RegionManager regionManager : this.getRegionContainer().getLoaded()) {
            amo += regionManager.getRegions().values()
                    .stream()
                    .filter(rg -> this.isOwner(rg.getId(), player))
                    .count();
        }
        return amo;
    }

    public Set<ProtectedRegion> getRegions(Location location) {
        this.checkService();
        RegionManager manager = this.getRegionManager(location.getWorld());
        BlockVector3 position = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        return manager.getApplicableRegions(position).getRegions();
    }

    public List<String> getRegionsIDs(Location location) {
        this.checkService();
        RegionManager manager = this.getRegionManager(location.getWorld());
        BlockVector3 position = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        return manager.getApplicableRegionsIDs(position);
    }

    public boolean hasRegionsInside(Location location, int cuboidSize) {
        this.checkService();
        RegionManager manager = this.getRegionManager(location.getWorld());
        BlockVector3 max = BlockVector3.at(
                location.getX() + cuboidSize, location.getY() + cuboidSize, location.getZ() + cuboidSize);
        BlockVector3 min = BlockVector3.at(
                location.getX() - cuboidSize, location.getY() - cuboidSize, location.getZ() - cuboidSize);

        ProtectedRegion rg = new ProtectedCuboidRegion(Utils.getRKey((byte) 24), min, max);
        ApplicableRegionSet set = manager.getApplicableRegions(rg);

        return !set.getRegions().isEmpty();
    }

    public StateFlag.State getWGState(LState lState) {
        this.checkService();
        return lState == LState.ALLOW ? StateFlag.State.ALLOW : StateFlag.State.DENY;
    }

    public @NotNull StateFlag getWGFlag(LFlag flag) {
        this.checkService();
        return this.getWGFlag(flag.name());
    }

    public @Nullable StateFlag getWGFlag(LunaFlags.IState state) {
        return lunaFlags != null ? lunaFlags.flag(state) : null;
    }

    public @NotNull StateFlag getWGFlag(String id) throws WGFlagGetException {
        this.checkService();

        id = id.toUpperCase();
        if (lunaFlags != null) {
            StateFlag flag = lunaFlags.flag(id);
            if (flag != null) return flag;
        }

        try {
            Field field = Flags.class.getField(id);
            field.setAccessible(true);
            return (StateFlag) field.get(null);
        }
        catch (NoSuchFieldException e) {
            throw new WGFlagGetException("Не найдено поле " + id);
        }
        catch (IllegalAccessException e) {
            throw new WGFlagGetException("Отсутствует разрешение к полю " + id);
        }
        catch (ClassCastException e) {
            throw new WGFlagGetException("Невозможно преобразовать в StateFlag");
        }
    }

    public boolean checkState(@NotNull ApplicableRegionSet regions, @NotNull LocalPlayer player, @NotNull StateFlag stateFlag) {
        return regions.testState(player, stateFlag);
    }

    public boolean checkState(@NotNull Location location, @NotNull LocalPlayer player, @NotNull StateFlag stateFlag) {
        ApplicableRegionSet regions = getRegionSet(location);
        return this.checkState(regions, player, stateFlag);
    }

    public LocalPlayer wrap(Player player) {
        return WorldGuardPlugin.inst().wrapPlayer(player);
    }

    public LocalPlayer wrap(OfflinePlayer player) {
        return WorldGuardPlugin.inst().wrapOfflinePlayer(player);
    }

    public ApplicableRegionSet getRegionSet(Location location) {
        return getRegionManager(location.getWorld()).getApplicableRegions(BukkitAdapter.asBlockVector(location));
    }
}
