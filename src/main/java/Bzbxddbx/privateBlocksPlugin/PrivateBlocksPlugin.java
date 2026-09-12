package Bzbxddbx.privateBlocksPlugin;

import Bzbxddbx.privateBlocksPlugin.block.ClaimManager;
import Bzbxddbx.privateBlocksPlugin.block.repository.SqlClaimRepository;
import Bzbxddbx.privateBlocksPlugin.command.PrivateBlocksCommand;
import Bzbxddbx.privateBlocksPlugin.config.PluginSettings;
import Bzbxddbx.privateBlocksPlugin.listener.BlockProtectedListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class PrivateBlocksPlugin extends JavaPlugin {

    private SqlClaimRepository claimRepository;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        PluginSettings settings = new PluginSettings(getConfig(), this);

        claimRepository = new SqlClaimRepository(new File(getDataFolder(), "claims.db"), getLogger());
        claimRepository.loadFromDisk();

        ClaimManager claimManager = new ClaimManager(claimRepository);

        BlockProtectedListener protectionListener = new BlockProtectedListener(claimManager, settings);

        getServer().getPluginManager().registerEvents(protectionListener, this);

        getCommand("pbhelp").setExecutor(new PrivateBlocksCommand());
    }

    @Override
    public void onDisable() {
        if (claimRepository != null) {
            claimRepository.close();
            claimRepository = null;
        }
    }
}