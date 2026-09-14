package Bzbxddbx.privateBlocksPlugin;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

public final class PrivateBlocksBootstrapper implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
    }

    @Override
    public JavaPlugin createPlugin(PluginProviderContext context) {
        return new PrivateBlocksPlugin(dataDirectory(context));
    }

    private static Path dataDirectory(PluginProviderContext context) {
        return context.getDataDirectory();
    }
}