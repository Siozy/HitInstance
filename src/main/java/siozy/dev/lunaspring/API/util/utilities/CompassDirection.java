package siozy.dev.lunaspring.API.util.utilities;

import lombok.RequiredArgsConstructor;
import org.bukkit.block.BlockFace;

@RequiredArgsConstructor
public enum CompassDirection {
    NORTH(BlockFace.NORTH, BlockFace.NORTH),
    NORTH_EAST(BlockFace.NORTH_EAST, BlockFace.EAST),
    EAST(BlockFace.EAST, BlockFace.EAST),
    SOUTH_EAST(BlockFace.SOUTH_EAST, BlockFace.EAST),
    SOUTH(BlockFace.SOUTH, BlockFace.SOUTH),
    SOUTH_WEST(BlockFace.SOUTH_WEST, BlockFace.WEST),
    WEST(BlockFace.WEST, BlockFace.WEST),
    NORTH_WEST(BlockFace.NORTH_WEST, BlockFace.WEST);

    public final BlockFace extendedFace;
    public final BlockFace face;

    public static CompassDirection of(double angle) {
        angle = (angle + 360) % 360;

        if (angle >= 337.5 || angle < 22.5) return CompassDirection.EAST;
        if (angle >= 22.5 && angle < 67.5) return CompassDirection.NORTH_EAST;
        if (angle >= 67.5 && angle < 112.5) return CompassDirection.NORTH;
        if (angle >= 112.5 && angle < 157.5) return CompassDirection.NORTH_WEST;
        if (angle >= 157.5 && angle < 202.5) return CompassDirection.WEST;
        if (angle >= 202.5 && angle < 247.5) return CompassDirection.SOUTH_WEST;
        if (angle >= 247.5 && angle < 292.5) return CompassDirection.SOUTH;
        return CompassDirection.SOUTH_EAST;
    }
}