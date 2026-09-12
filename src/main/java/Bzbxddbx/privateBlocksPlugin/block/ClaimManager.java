package Bzbxddbx.privateBlocksPlugin.block;

import Bzbxddbx.privateBlocksPlugin.block.repository.ClaimRepository;
import org.bukkit.Location;

import java.util.Optional;
import java.util.UUID;

public class ClaimManager {

    private final ClaimRepository repository;

    public ClaimManager(ClaimRepository repository) {
        this.repository = repository;
    }

    public ClaimCreateResult createClaim(Location center, UUID ownerId, int radius) {
        Claim claim = new Claim(ownerId, BlockKey.from(center), radius);

        if (repository.intersects(claim)) {
            return ClaimCreateResult.OVERLAPS;
        }

        repository.save(claim);
        return ClaimCreateResult.SUCCESS;
    }

    public boolean removeClaim(Location center) {
        Optional<Claim> claim = repository.findByCenter(BlockKey.from(center));
        if (claim.isEmpty()) {
            return false;
        }
        repository.delete(claim.get());
        return true;
    }

    public boolean isProtected(Location location) {
        return repository.findContaining(BlockKey.from(location)).isPresent();
    }

    public Optional<UUID> getOwner(Location location) {
        return repository.findContaining(BlockKey.from(location)).map(Claim::getOwner);
    }

    public Optional<Claim> findClaim(Location location) {
        return repository.findContaining(BlockKey.from(location));
    }
}
