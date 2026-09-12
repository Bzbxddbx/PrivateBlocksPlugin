package Bzbxddbx.privateBlocksPlugin.block;

import Bzbxddbx.privateBlocksPlugin.config.ClaimTier;
import Bzbxddbx.privateBlocksPlugin.config.PluginSettings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClaimBlockRestorer {

    private final ClaimManager claimManager;
    private final Logger logger;
    private final Map<Integer, Material> materialByRadius;

    public ClaimBlockRestorer(ClaimManager claimManager, PluginSettings settings, Logger logger) {
        this.claimManager = claimManager;
        this.logger = logger;
        this.materialByRadius = buildMaterialByRadius(settings);
    }

    public void restoreMissingBlocks() {
        for (Claim claim : claimManager.findAll()) {
            Material material = materialByRadius.get(claim.getRadius());
            if (material == null) {
                logger.warning("Не найден материал для привата с радиусом " + claim.getRadius()
                        + " в " + claim.getCenter() + ", пропущено восстановление.");
                continue;
            }

            World world = Bukkit.getWorld(claim.getCenter().worldId());
            if (world == null) {
                logger.warning("Мир привата " + claim.getCenter().worldId() + " не загружен, пропущено восстановление.");
                continue;
            }

            Block block = new Location(
                    world,
                    claim.getCenter().x(),
                    claim.getCenter().y(),
                    claim.getCenter().z()
            ).getBlock();

            if (block.getType() == material) {
                continue;
            }

            block.setType(material);
            logger.log(Level.INFO, "Восстановлен приват-блок на центре {0}", claim.getCenter());
        }
    }

    private static Map<Integer, Material> buildMaterialByRadius(PluginSettings settings) {
        Map<Integer, Material> result = new HashMap<>();
        for (ClaimTier tier : settings.tiers()) {
            result.putIfAbsent(tier.radius(), tier.material());
        }
        return result;
    }
}