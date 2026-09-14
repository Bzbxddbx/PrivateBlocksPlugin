package Bzbxddbx.privateBlocksPlugin;

import Bzbxddbx.privateBlocksPlugin.block.ClaimBlockRestorer;
import Bzbxddbx.privateBlocksPlugin.block.ClaimManager;
import Bzbxddbx.privateBlocksPlugin.block.repository.SqlClaimRepository;
import Bzbxddbx.privateBlocksPlugin.command.PrivateBlocksCommand;
import Bzbxddbx.privateBlocksPlugin.config.PluginSettings;
import Bzbxddbx.privateBlocksPlugin.listener.BlockProtectedListener;
import Bzbxddbx.privateBlocksPlugin.listener.PistonProtectionListener;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.List;

public final class PrivateBlocksPlugin extends JavaPlugin {

    private final Path dataDirectory;

    private SqlClaimRepository claimRepository;

    public PrivateBlocksPlugin(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        PluginSettings settings = new PluginSettings(getConfig(), this);

        claimRepository = new SqlClaimRepository(dataDirectory.resolve("claims.db"), getLogger());
        claimRepository.loadFromDisk();

        ClaimManager claimManager = new ClaimManager(claimRepository);

        if (settings.isRestoreMissingBlocksEnabled()) {
            new ClaimBlockRestorer(claimManager, settings, this).restoreMissingBlocks();
        }

        BlockProtectedListener protectionListener = new BlockProtectedListener(claimManager, settings);

        getServer().getPluginManager().registerEvents(protectionListener, this);

        if (settings.isPistonProtectionEnabled()) {
            getServer().getPluginManager().registerEvents(new PistonProtectionListener(claimManager), this);
        }

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->
                event.registrar().register(
                        PrivateBlocksCommand.create().build(),
                        "Показывает информацию о радиусах приват-блоков",
                        List.of()
                ));
    }

    @Override
    public void onDisable() {
        if (claimRepository != null) {
            claimRepository.close();
            claimRepository = null;
        }
    }
}