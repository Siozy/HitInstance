package siozy.dev.lunaspring.API.util.service.managers.worldguard;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import siozy.dev.lunaspring.API.util.exceptions.WGFlagGetException;
import siozy.dev.lunaspring.API.util.service.realized.GuardService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@UtilityClass
public class GuardManager {
    private final GuardService guardService;
    static {
        guardService = new GuardService();
    }

    public @Nullable LunaFlags flags() {
        return guardService.flags();
    }

    public RegionContainer getRegionContainer() {
        return guardService.getRegionContainer();
    }

    public com.sk89q.worldguard.protection.managers.RegionManager getRegionManager(World world) {
        return guardService.getRegionManager(world);
    }

    public ProtectedCuboidRegion createRegion(String name, Location minLoc, Location maxLoc) {
        return guardService.createRegion(name, minLoc, maxLoc);
    }

    public void removeRegion(String name) {
        guardService.removeRegion(name);
    }

    public ProtectedRegion getRegion(String regionName) {
        return guardService.getRegion(regionName);
    }

    public void addMember(String regionName, Player player) {
        guardService.addMember(regionName, player);
    }

    public void addOwner(String regionName, Player player) {
        guardService.addOwner(regionName, player);
    }

    public void removeMember(String regionName, Player player) {
        guardService.removeMember(regionName, player);
    }

    public void removeOwner(String regionName, Player player) {
        guardService.removeOwner(regionName, player);
    }

    public boolean isOwner(String regionName, Player player) {
        return guardService.isOwner(regionName, player);
    }

    public boolean isMember(String regionName, Player player) {
        return guardService.isMember(regionName, player);
    }

    public World getWorld(String regionName) {
        return guardService.getWorld(regionName);
    }

    public BlockVector3 getVectorPoint(String regionName, boolean isMinPoint) {
        return guardService.getVectorPoint(regionName, isMinPoint);
    }

    public Location getPoint(String regionName, boolean isMinPoint) {
        return guardService.getPoint(regionName, isMinPoint);
    }

    public boolean containsBlock(String regionName, int x, int y, int z) {
        return guardService.containsBlock(regionName, x, y, z);
    }

    public boolean containsBlock(String regionName, Location location) {
        return containsBlock(regionName, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public Set<UUID> getMembers(String regionName) {
        return guardService.getMembers(regionName);
    }

    public Set<UUID> getOwners(String regionName) {
        return guardService.getOwners(regionName);
    }

    public Map<Flag<?>, Object> getFlags(String regionName) {
        return guardService.getFlags(regionName);
    }

    public Map<StateFlag, StateFlag.State> getStateFlags(String regionName) {
        return guardService.getStateFlags(regionName);
    }

    public Map<LFlag, LState> getLStateFlags(String regionName) {
        return guardService.getLStateFlags(regionName);
    }

    public Set<LFlag> getLStateFlags(String regionName, LState filteringState) {
        return guardService.getLStateFlags(regionName, filteringState);
    }

    public void setFlag(String regionName, StateFlag flag, StateFlag.State state) {
        guardService.setFlag(regionName, flag, state);
    }

    public void setFlag(String regionName, LFlag lFlag, StateFlag.State state) {
        guardService.setFlag(regionName, lFlag, state);
    }

    public void setFlag(String regionName, LFlag lFlag, LState lState) {
        guardService.setFlag(regionName, lFlag, lState);
    }

    public void addMember(@NotNull ProtectedRegion region, Player player) {
        guardService.addMember(region, player);
    }

    public void addOwner(@NotNull ProtectedRegion region, Player player) {
        guardService.addOwner(region, player);
    }

    public void removeMember(@NotNull ProtectedRegion region, Player player) {
        guardService.removeMember(region, player);
    }

    public void removeOwner(@NotNull ProtectedRegion region, Player player) {
        guardService.removeOwner(region, player);
    }

    public boolean isOwner(@NotNull ProtectedRegion region, Player player) {
        return guardService.isOwner(region, player);
    }

    public boolean isMember(@NotNull ProtectedRegion region, Player player) {
        return guardService.isMember(region, player);
    }

    public BlockVector3 getVectorPoint(@NotNull ProtectedRegion region, boolean isMinPoint) {
        return guardService.getVectorPoint(region, isMinPoint);
    }

    public Location getPoint(@NotNull ProtectedRegion region, boolean isMinPoint) {
        return guardService.getPoint(region, isMinPoint);
    }

    public boolean containsBlock(@NotNull ProtectedRegion region, int x, int y, int z) {
        return guardService.containsBlock(region, x, y, z);
    }

    public boolean containsBlock(@NotNull ProtectedRegion region, Location location) {
        return containsBlock(region, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public Set<UUID> getMembers(@NotNull ProtectedRegion region) {
        return guardService.getMembers(region);
    }

    public Set<UUID> getOwners(@NotNull ProtectedRegion region) {
        return guardService.getOwners(region);
    }

    public Map<Flag<?>, Object> getFlags(@NotNull ProtectedRegion region) {
        return guardService.getFlags(region);
    }

    public Map<StateFlag, StateFlag.State> getStateFlags(@NotNull ProtectedRegion region) {
        return guardService.getStateFlags(region);
    }

    public Map<LFlag, LState> getLStateFlags(@NotNull ProtectedRegion region) {
        return guardService.getLStateFlags(region);
    }

    public Set<LFlag> getLStateFlags(@NotNull ProtectedRegion region, LState filteringState) {
        return guardService.getLStateFlags(region, filteringState);
    }

    public void setFlag(@NotNull ProtectedRegion region, StateFlag flag, StateFlag.State state) {
        guardService.setFlag(region, flag, state);
    }

    public void setFlag(@NotNull ProtectedRegion region, LFlag lFlag, StateFlag.State state) {
        guardService.setFlag(region, lFlag, state);
    }

    public void setFlag(@NotNull ProtectedRegion region, LFlag lFlag, LState lState) {
        guardService.setFlag(region, lFlag, lState);
    }

    public long getCount(Player player) {
        return guardService.getCount(player);
    }

    public boolean hasRegionsInside(Location location, int cuboidSize) {
        return guardService.hasRegionsInside(location, cuboidSize);
    }

    public Set<ProtectedRegion> getRegions(Location location) {
        return guardService.getRegions(location);
    }

    public List<String> getRegionsIds(Location location) {
        return guardService.getRegionsIDs(location);
    }

    public StateFlag.State getWGState(LState lState) {
        return guardService.getWGState(lState);
    }

    public @NotNull StateFlag getWGFlag(LFlag flag) {
        return guardService.getWGFlag(flag);
    }

    public @NotNull StateFlag getWGFlag(String id) throws WGFlagGetException {
        return guardService.getWGFlag(id);
    }

    public @Nullable StateFlag getWGFlag(LunaFlags.IState state) {
        return guardService.getWGFlag(state);
    }

    public boolean checkState(@NotNull ApplicableRegionSet regions, @NotNull LocalPlayer player, @NotNull StateFlag stateFlag) {
        return guardService.checkState(regions, player, stateFlag);
    }

    public boolean checkState(@NotNull Location location, @NotNull LocalPlayer player, @NotNull StateFlag stateFlag) {
        return guardService.checkState(location, player, stateFlag);
    }

    public LocalPlayer wrap(@NotNull Player player) {
        return guardService.wrap(player);
    }

    public LocalPlayer wrap(@NotNull OfflinePlayer player) {
        return guardService.wrap(player);
    }

    public ApplicableRegionSet getRegionSet(@NotNull Location location) {
        return guardService.getRegionSet(location);
    }

    public boolean isEnabled() {
        return guardService.isEnabled();
    }
}