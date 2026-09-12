package Bzbxddbx.privateBlocksPlugin.block;

import org.bukkit.Location;

import java.util.Objects;
import java.util.UUID;

public final class Claim {

    private final UUID owner;
    private final BlockKey center;
    private final int radius;

    public Claim(UUID owner, BlockKey center, int radius) {
        this.owner = Objects.requireNonNull(owner, "owner");
        this.center = Objects.requireNonNull(center, "center");
        this.radius = Math.max(0, radius);
    }

    public UUID getOwner() {
        return owner;
    }

    public BlockKey getCenter() {
        return center;
    }

    public int getRadius() {
        return radius;
    }

    public boolean contains(Location location) {
        return contains(BlockKey.from(location));
    }

    public boolean contains(BlockKey key) {
        if (!center.worldId().equals(key.worldId())) {
            return false;
        }
        return Math.abs(center.x() - key.x()) <= radius
                && Math.abs(center.y() - key.y()) <= radius
                && Math.abs(center.z() - key.z()) <= radius;
    }

    public boolean intersects(Claim other) {
        if (!center.worldId().equals(other.center.worldId())) {
            return false;
        }
        int distance = radius + other.radius;
        return Math.abs(center.x() - other.center.x()) <= distance
                && Math.abs(center.y() - other.center.y()) <= distance
                && Math.abs(center.z() - other.center.z()) <= distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Claim claim)) {
            return false;
        }
        return owner.equals(claim.owner) && center.equals(claim.center);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, center);
    }
}
