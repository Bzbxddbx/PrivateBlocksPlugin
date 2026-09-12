package Bzbxddbx.privateBlocksPlugin.block.repository;

import Bzbxddbx.privateBlocksPlugin.block.BlockKey;
import Bzbxddbx.privateBlocksPlugin.block.Claim;
import Bzbxddbx.privateBlocksPlugin.block.repository.sqlite.ClaimDao;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SqlClaimRepository implements ClaimRepository {

    private static final long FLUSH_TIMEOUT_SECONDS = 10;

    private final Map<BlockKey, Claim> claimsByCenter = new LinkedHashMap<>();
    private final ClaimDao claimDao;
    private final ExecutorService writeExecutor;
    private final Logger logger;
    private boolean closed;

    public SqlClaimRepository(File databaseFile, Logger logger) {
        this.claimDao = new ClaimDao("jdbc:sqlite:" + databaseFile.getAbsolutePath(), logger);
        this.writeExecutor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "PrivateClaims-Writer");
            thread.setDaemon(true);
            return thread;
        });
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
        writeExecutor.submit(() -> claimDao.insert(claim));
    }

    @Override
    public void delete(Claim claim) {
        claimsByCenter.remove(claim.getCenter(), claim);
        writeExecutor.submit(() -> claimDao.delete(claim.getCenter()));
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

        writeExecutor.shutdown();
        try {
            if (!writeExecutor.awaitTermination(FLUSH_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                logger.warning("Не все изменения приватов удалось записать в БД за отведенное время.");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            logger.log(Level.WARNING, "Поток выключения был прерван во время сохранения приватов.", exception);
        }
    }
}