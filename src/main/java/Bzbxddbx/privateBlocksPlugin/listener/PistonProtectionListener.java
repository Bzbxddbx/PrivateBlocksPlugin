package Bzbxddbx.privateBlocksPlugin.listener;

import Bzbxddbx.privateBlocksPlugin.block.ClaimManager;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import java.util.List;

public class PistonProtectionListener implements Listener {

    private final ClaimManager claimManager;

    public PistonProtectionListener(ClaimManager claimManager) {
        this.claimManager = claimManager;
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        Block pistonHead = event.getBlock().getRelative(event.getDirection());

        if (!claimManager.isPistonMoveAllowed(event.getBlock().getLocation(), pistonHead.getLocation())
                || isIntrusion(event.getBlocks(), event.getDirection(), event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (isIntrusion(event.getBlocks(), event.getDirection().getOppositeFace(), event.getBlock())) {
            event.setCancelled(true);
        }
    }

    private boolean isIntrusion(List<Block> movedBlocks, BlockFace moveDirection, Block piston) {
        for (Block block : movedBlocks) {
            Block destination = block.getRelative(moveDirection);
            if (!claimManager.isPistonMoveAllowed(piston.getLocation(), destination.getLocation())) {
                return true;
            }
        }
        return false;
    }
}