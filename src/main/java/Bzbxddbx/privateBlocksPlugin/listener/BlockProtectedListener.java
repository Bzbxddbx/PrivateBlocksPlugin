package Bzbxddbx.privateBlocksPlugin.listener;

import Bzbxddbx.privateBlocksPlugin.block.BlockKey;
import Bzbxddbx.privateBlocksPlugin.block.Claim;
import Bzbxddbx.privateBlocksPlugin.block.ClaimCreateResult;
import Bzbxddbx.privateBlocksPlugin.block.ClaimManager;
import Bzbxddbx.privateBlocksPlugin.config.ClaimTier;
import Bzbxddbx.privateBlocksPlugin.config.PluginSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Optional;

public class BlockProtectedListener implements Listener {

    private final ClaimManager claimManager;
    private final PluginSettings settings;

    public BlockProtectedListener(ClaimManager claimManager, PluginSettings settings) {
        this.claimManager = claimManager;
        this.settings = settings;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Location location = block.getLocation();

        if (claimManager.isProtected(location)) {
            Player player = event.getPlayer();
            Claim claim = claimManager.findClaim(location).orElseThrow();

            if (!player.getUniqueId().equals(claim.getOwner())) {
                event.setCancelled(true);
                player.sendMessage(Component.text("Эта территория принадлежит другому игроку!", NamedTextColor.RED));
                return;
            }
        }

        Optional<ClaimTier> tier = settings.tierFor(block.getType());
        if (tier.isEmpty()) {
            return;
        }

        Player player = event.getPlayer();
        ClaimCreateResult result = claimManager.createClaim(location, player.getUniqueId(), tier.get().radius());

        if (result == ClaimCreateResult.OVERLAPS) {
            event.setCancelled(true);
            player.sendMessage(Component.text("Здесь пересекается территория привата!", NamedTextColor.RED));
            return;
        }

        player.sendMessage(Component.text("Вы установили приват-блок "
                + tier.get().getSize() + "x" + tier.get().getSize() + "x" + tier.get().getSize() + "!", NamedTextColor.GREEN));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (!claimManager.isProtected(block.getLocation())) {
            return;
        }

        Player player = event.getPlayer();
        Claim claim = claimManager.findClaim(block.getLocation()).orElseThrow();

        if (!player.getUniqueId().equals(claim.getOwner())) {
            event.setCancelled(true);
            player.sendMessage(Component.text("Эта территория принадлежит другому игроку!", NamedTextColor.RED));
            return;
        }

        if (BlockKey.from(block.getLocation()).equals(claim.getCenter())) {
            claimManager.removeClaim(block.getLocation());
            player.sendMessage(Component.text("Вы убрали свой приват-блок.", NamedTextColor.YELLOW));
        }
    }
}
