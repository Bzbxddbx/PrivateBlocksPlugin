package Bzbxddbx.privateBlocksPlugin.block.repository;

import Bzbxddbx.privateBlocksPlugin.block.BlockKey;
import Bzbxddbx.privateBlocksPlugin.block.Claim;
import Bzbxddbx.privateBlocksPlugin.block.repository.sqlite.ClaimDao;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SqlClaimRepository implements ClaimRepository {

    private static final long FLUSH_TIMEOUT_SECONDS = 10;

    private final Map<BlockKey, Claim> claimsByCenter = new LinkedHashMap<>();
    private final ClaimDao claimDao;
    private final Logger logger;
    private volatile CompletableFuture<Void> writeTail;
    private boolean closed;

    public SqlClaimRepository(Path databaseFile, Logger logger) {
        this.claimDao = new ClaimDao("jdbc:sqlite:" + databaseFile.toAbsolutePath(), logger);
        this.writeTail = CompletableFuture.completedFuture(null);
        this.logger = logger;
    }

    public void loadFromDisk() {
        claimDao.createTableIfAbsent();
        for (Claim claim : claimDao.loadAll()) {
            claimsByCenter.put(claim.getCenter(), claim);
        }
    }

    @Override
    public void save(Claim claim) {
        claimsByCenter.put(claim.getCenter(), claim);
        enqueue(() -> claimDao.insert(claim));
    }

    @Override
    public void delete(Claim claim) {
        claimsByCenter.remove(claim.getCenter(), claim);
        enqueue(() -> claimDao.delete(claim.getCenter()));
    }

    private void enqueue(Runnable task) {
        CompletableFuture<Void> chain = writeTail.thenRunAsync(task);
        writeTail = chain.exceptionally(exception -> {
            logger.log(Level.SEVERE, "Запись в БД прервана.", exception);
            return null;
        });
    }

    @Override
    public Optional<Claim> findByCenter(BlockKey center) {
        return Optional.ofNullable(claimsByCenter.get(center));
    }

    @Override
    public Optional<Claim> findContaining(BlockKey key) {
        return claimsByCenter.values().stream()
                .filter(claim -> claim.contains(key))
                .findFirst();
    }

    @Override
    public boolean intersects(Claim claim) {
        return claimsByCenter.values().stream().anyMatch(claim::intersects);
    }

    @Override
    public Collection<Claim> findAll() {
        return Collections.unmodifiableCollection(claimsByCenter.values());
    }

    public void close() {
        if (closed) {
            return;
        }
        closed = true;

        try {
            writeTail.get(FLUSH_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            logger.log(Level.WARNING, "Поток выключения был прерван во время сохранения приватов.", exception);
        } catch (ExecutionException | TimeoutException exception) {
            logger.log(Level.WARNING, "Не все изменения приватов удалось записать в БД за отведенное время.", exception);
        }
    }
}