package Bzbxddbx.privateBlocksPlugin.block.repository;

import Bzbxddbx.privateBlocksPlugin.block.BlockKey;
import Bzbxddbx.privateBlocksPlugin.block.Claim;

import java.util.Collection;
import java.util.Optional;

public interface ClaimRepository {

    void save(Claim claim);

    void delete(Claim claim);

    Optional<Claim> findByCenter(BlockKey center);

    Optional<Claim> findContaining(BlockKey key);

    boolean intersects(Claim claim);

    Collection<Claim> findAll();
}
