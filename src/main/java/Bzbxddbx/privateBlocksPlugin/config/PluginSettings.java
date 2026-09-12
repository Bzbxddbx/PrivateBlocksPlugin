package Bzbxddbx.privateBlocksPlugin.config;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PluginSettings {

    private static final Map<String, Integer> DEFAULT_SIZES = Map.ofEntries(
            Map.entry("EMERALD_BLOCK", 15),
            Map.entry("DIAMOND_BLOCK", 9),
            Map.entry("NETHERITE_BLOCK", 21),
            Map.entry("GOLD_BLOCK", 7)
    );

    private final List<ClaimTier> tiers;
    private final boolean restoreMissingBlocks;
    private final Plugin plugin;

    public PluginSettings(FileConfiguration config, Plugin plugin) {
        this.plugin = plugin;
        this.restoreMissingBlocks = config.getBoolean("restore-missing-blocks", true);
        this.tiers = parseTiers(config);
    }

    public boolean isRestoreMissingBlocksEnabled() {
        return restoreMissingBlocks;
    }

    public Optional<ClaimTier> tierFor(Material material) {
        return tiers.stream()
                .filter(tier -> tier.material() == material)
                .findFirst();
    }

    public Collection<ClaimTier> tiers() {
        return List.copyOf(tiers);
    }

    private List<ClaimTier> parseTiers(FileConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection("claims");
        if (section == null) {
            plugin.getLogger().warning("Отсутствует секция 'claims' в config.yml, используются значения по умолчанию.");
            return buildDefaults();
        }

        List<ClaimTier> parsed = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            parseTier(key, section.getInt(key)).ifPresent(parsed::add);
        }

        return parsed.isEmpty() ? buildDefaults() : List.copyOf(parsed);
    }

    private Optional<ClaimTier> parseTier(String materialName, int size) {
        Material material = Material.matchMaterial(materialName);
        if (material == null) {
            plugin.getLogger().warning("Неизвестный материал в config.yml: " + materialName);
            return Optional.empty();
        }

        if (size < 1 || size % 2 == 0) {
            plugin.getLogger().warning("Размер привата для " + materialName
                    + " должен быть нечетным и >= 1, пропущен.");
            return Optional.empty();
        }

        return Optional.of(new ClaimTier(material, (size - 1) / 2));
    }

    private static List<ClaimTier> buildDefaults() {
        return DEFAULT_SIZES.entrySet().stream()
                .map(entry -> new ClaimTier(Material.valueOf(entry.getKey()), (entry.getValue() - 1) / 2))
                .toList();
    }
}