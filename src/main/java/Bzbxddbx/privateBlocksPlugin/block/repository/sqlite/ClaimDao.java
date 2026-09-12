package Bzbxddbx.privateBlocksPlugin.block.repository.sqlite;

import Bzbxddbx.privateBlocksPlugin.block.BlockKey;
import Bzbxddbx.privateBlocksPlugin.block.Claim;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClaimDao {

    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS claims ("
                    + "world_id TEXT    NOT NULL, "
                    + "center_x INTEGER NOT NULL, "
                    + "center_y INTEGER NOT NULL, "
                    + "center_z INTEGER NOT NULL, "
                    + "owner    TEXT    NOT NULL, "
                    + "radius   INTEGER NOT NULL, "
                    + "PRIMARY KEY (world_id, center_x, center_y, center_z))";

    private static final String INSERT =
            "INSERT OR REPLACE INTO claims (world_id, center_x, center_y, center_z, owner, radius) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String DELETE =
            "DELETE FROM claims WHERE world_id = ? AND center_x = ? AND center_y = ? AND center_z = ?";

    private static final String LOAD_ALL =
            "SELECT world_id, center_x, center_y, center_z, owner, radius FROM claims";

    private final String url;
    private final Logger logger;

    public ClaimDao(String url, Logger logger) {
        this.url = url;
        this.logger = logger;
    }

    public void createTableIfAbsent() {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            statement.execute(CREATE_TABLE);
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "Не удалось создать таблицу claims", exception);
        }
    }

    public List<Claim> loadAll() {
        List<Claim> claims = new ArrayList<>();
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(LOAD_ALL)) {
            while (resultSet.next()) {
                claims.add(mapToClaim(resultSet));
            }
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "Не удалось загрузить приваты из БД", exception);
        }
        return claims;
    }

    public void insert(Claim claim) {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setString(1, claim.getCenter().worldId().toString());
            statement.setInt(2, claim.getCenter().x());
            statement.setInt(3, claim.getCenter().y());
            statement.setInt(4, claim.getCenter().z());
            statement.setString(5, claim.getOwner().toString());
            statement.setInt(6, claim.getRadius());
            statement.executeUpdate();
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "Не удалось сохранить приват в БД", exception);
        }
    }

    public void delete(BlockKey center) {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(DELETE)) {
            statement.setString(1, center.worldId().toString());
            statement.setInt(2, center.x());
            statement.setInt(3, center.y());
            statement.setInt(4, center.z());
            statement.executeUpdate();
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "Не удалось удалить приват из БД", exception);
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url);
    }

    private static Claim mapToClaim(ResultSet resultSet) throws SQLException {
        UUID worldId = UUID.fromString(resultSet.getString("world_id"));
        int x = resultSet.getInt("center_x");
        int y = resultSet.getInt("center_y");
        int z = resultSet.getInt("center_z");
        UUID owner = UUID.fromString(resultSet.getString("owner"));
        int radius = resultSet.getInt("radius");

        return new Claim(owner, new BlockKey(worldId, x, y, z), radius);
    }
}