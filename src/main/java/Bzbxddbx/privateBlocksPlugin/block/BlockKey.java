package Bzbxddbx.privateBlocksPlugin.block;

import org.bukkit.Location;

import java.util.Objects;
import java.util.UUID;

public record BlockKey(UUID worldId, int x, int y, int z) {

    public BlockKey {
        Objects.requireNonNull(worldId, "worldId");
    }

    public static BlockKey from(Location location) {
        return new BlockKey(
                location.getWorld().getUID(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );
    }
}
