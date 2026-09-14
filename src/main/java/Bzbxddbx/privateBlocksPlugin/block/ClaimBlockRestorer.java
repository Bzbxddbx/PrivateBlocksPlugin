package Bzbxddbx.privateBlocksPlugin.block;

import Bzbxddbx.privateBlocksPlugin.config.ClaimTier;
import Bzbxddbx.privateBlocksPlugin.config.PluginSettings;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.logging.Level;

public class ClaimBlockRestorer {

    private final ClaimManager claimManager;
    private final JavaPlugin plugin;
    private final Map<Integer, Material> materialByRadius;

    public ClaimBlockRestorer(ClaimManager claimManager, PluginSettings settings, JavaPlugin plugin) {
        this.claimManager = claimManager;
        this.plugin = plugin;
        this.materialByRadius = buildMaterialByRadius(settings);
    }

    public void restoreMissingBlocks() {
        Server server = plugin.getServer();
        for (Claim claim : claimManager.findAll()) {
            Material material = materialByRadius.get(claim.getRadius());
            if (material == null) {
                plugin.getLogger().warning("Не найден материал для привата с радиусом " + claim.getRadius()
                        + " в " + claim.getCenter() + ", пропущено восстановление.");
                continue;
            }

            World world = server.getWorld(claim.getCenter().worldId());
            if (world == null) {
                plugin.getLogger().warning("Мир привата " + claim.getCenter().worldId() + " не загружен, пропущено восстановление.");
                continue;
            }

            int x = claim.getCenter().x();
            int y = claim.getCenter().y();
            int z = claim.getCenter().z();

            world.getChunkAtAsync(x >> 4, z >> 4, true)
                    .whenComplete((chunk, throwable) -> restoreChunk(throwable, world, x, y, z, material, claim));
        }
    }

    private void restoreChunk(Throwable throwable, World world, int x, int y, int z, Material material, Claim claim) {
        if (throwable != null) {
            plugin.getLogger().log(Level.SEVERE,
                    "Не удалось загрузить чанк привата " + claim.getCenter() + " для восстановления.",
                    throwable instanceof CompletionException ? throwable.getCause() : throwable);
            return;
        }

        plugin.getServer().getGlobalRegionScheduler().execute(plugin, () -> {
            Block block = new Location(world, x, y, z).getBlock();
            if (block.getType() == material) {
                return;
            }

            block.setType(material);
            plugin.getLogger().log(Level.INFO, "Восстановлен приват-блок на центре {0}", claim.getCenter());
        });
    }

    private static Map<Integer, Material> buildMaterialByRadius(PluginSettings settings) {
        Map<Integer, Material> result = new HashMap<>();
        for (ClaimTier tier : settings.tiers()) {
            result.putIfAbsent(tier.radius(), tier.material());
        }
        return result;
    }
}