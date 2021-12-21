package nl.imine.hubtweaks.oitc;

import nl.imine.hubtweaks.db.DatabaseManager;
import nl.imine.hubtweaks.db.Repository;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PvPSpawnRepository implements Repository<Location, Location> {
    private final Logger logger;

    private static final String COLUMN_WORLD = "world";
    private static final String COLUMN_X = "x";
    private static final String COLUMN_Y = "y";
    private static final String COLUMN_Z = "z";

    private final DatabaseManager databaseManager;

    private Map<Location, Location> cache;

    public PvPSpawnRepository(Logger logger, DatabaseManager databaseManager) {
        this.logger = logger;
        this.databaseManager = databaseManager;
        this.cache = new HashMap<>();
    }

    @Override
    public void loadAll() {
        this.cache = new HashMap<>();
        try(final Connection connection = databaseManager.getConnection()) {
            final var result = connection.prepareStatement("""
                SELECT world, x, y, z
                FROM pvp_spawn;
                """
            ).executeQuery();
            while (result.next()) {
                World world = Bukkit.getWorld(result.getString(COLUMN_WORLD));
                if (world != null) {
                    final Location location = new Location(world, result.getDouble(COLUMN_X), result.getDouble(COLUMN_Y), result.getDouble(COLUMN_Z));
                    this.cache.put(location, location);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed initializing PvPSpawn cache.", e);
        }
    }

    @Override
    public Collection<Location> getAll() {
        return cache.values();
    }

    @Override
    public Optional<Location> findOne(Location id) {
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public void addOne(Location spawn) {
        try(final Connection connection = databaseManager.getConnection()) {
            var statement = connection.prepareStatement("""
                INSERT INTO pvp_spawn (world, x, y, z)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY
                UPDATE world=VALUES(world), x=VALUES(x), y=VALUES(y), z=VALUES(z);
                """
            );

            statement.setString(1, spawn.getWorld().getName());
            statement.setInt(2, spawn.getBlockX());
            statement.setInt(3, spawn.getBlockY());
            statement.setInt(4, spawn.getBlockZ());
            statement.executeUpdate();
            cache.put(spawn, spawn);
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Failed inserting PvPSpawn.", e);
        }
    }

    @Override
    public void addAll(Collection<Location> all) {
        try (final Connection connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);
            var statement = connection.prepareStatement("""
                INSERT INTO pvp_spawn (world, x, y, z)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY
                UPDATE world=VALUES(world), x=VALUES(x), y=VALUES(y), z=VALUES(z);
                """
            );

            for (Location spawn : all) {
                cache.put(spawn, spawn);
                statement.setString(1, spawn.getWorld().getName());
                statement.setInt(2, spawn.getBlockX());
                statement.setInt(3, spawn.getBlockY());
                statement.setInt(4, spawn.getBlockZ());
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed inserting PvPSpawn.", e);
        }
    }

    @Override
    public void deleteAll(Collection<Location> all) {
        try (final Connection connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);

            final PreparedStatement statement = connection.prepareStatement("""
                DELETE FROM pvp_spawn
                WHERE world = ? AND x = ? AND y = ? AND z = ?;
                """
            );

            for (Location spawn : new ArrayList<>(all)) {
                statement.setString(1, spawn.getWorld().getName());
                statement.setDouble(2, spawn.getX());
                statement.setDouble(3, spawn.getY());
                statement.setDouble(4, spawn.getZ());
                statement.addBatch();
                this.cache.remove(spawn);
            }

            statement.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed deleting PvPSpawn.", e);
        }
    }

    @Override
    public void delete(Location spawn) {
        try (final Connection connection = databaseManager.getConnection()) {
            final PreparedStatement statement = connection.prepareStatement("""
                DELETE FROM pvp_spawn
                WHERE world = ? AND x = ? AND y = ? AND z = ?;
                """
            );
            statement.setString(1, spawn.getWorld().getName());
            statement.setDouble(2, spawn.getX());
            statement.setDouble(3, spawn.getY());
            statement.setDouble(4, spawn.getZ());

            statement.executeUpdate();
            this.cache.clear();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed deleting PvPSpawn.", e);
        }
    }
}
