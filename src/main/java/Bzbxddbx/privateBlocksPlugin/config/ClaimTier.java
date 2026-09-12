package Bzbxddbx.privateBlocksPlugin.config;

import org.bukkit.Material;

import java.util.Objects;

public record ClaimTier(Material material, int radius) {

    public ClaimTier {
        Objects.requireNonNull(material, "material");
        if (radius < 0) {
            throw new IllegalArgumentException("radius must be non-negative");
        }
    }

    public int getSize() {
        return radius * 2 + 1;
    }
}