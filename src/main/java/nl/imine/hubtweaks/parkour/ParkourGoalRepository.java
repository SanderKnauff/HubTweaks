package nl.imine.hubtweaks.parkour;

import nl.imine.hubtweaks.db.DatabaseManager;
import nl.imine.hubtweaks.db.Repository;
import nl.imine.hubtweaks.parkour.model.ParkourGoal;
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

public class ParkourGoalRepository implements Repository<Location, ParkourGoal> {
    private final Logger logger;

    private static final String COLUMN_LEVEL = "level";
    private static final String COLUMN_WORLD = "world";
    private static final String COLUMN_X = "x";
    private static final String COLUMN_Y = "y";
    private static final String COLUMN_Z = "z";

    private final DatabaseManager databaseManager;
    private final ParkourLevelRepository parkourLevelRepository;

    private Map<Location, ParkourGoal> cache;

    public ParkourGoalRepository(Logger logger, DatabaseManager databaseManager, ParkourLevelRepository parkourLevelRepository) {
        this.logger = logger;
        this.databaseManager = databaseManager;
        this.parkourLevelRepository = parkourLevelRepository;
        this.cache = new HashMap<>();
    }

    @Override
    public void loadAll() {
        this.cache = new HashMap<>();
        try(final Connection connection = databaseManager.getConnection()) {
            final var result = connection.prepareStatement("""
                SELECT level, world, x, y, z
                FROM parkour_goal;
                """
            ).executeQuery();
            while (result.next()) {
                World world = Bukkit.getWorld(result.getString(COLUMN_WORLD));
                if (world != null) {
                    final var level = parkourLevelRepository.findOne(result.getShort(COLUMN_LEVEL)).orElse(null);
                    if (level != null) {
                        final Location location = new Location(world, result.getDouble(COLUMN_X), result.getDouble(COLUMN_Y), result.getDouble(COLUMN_Z));
                        this.cache.put(location, new ParkourGoal(level, location));
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed initializing ParkourGoal cache.", e);
        }
    }

    @Override
    public Collection<ParkourGoal> getAll() {
        return cache.values();
    }

    @Override
    public Optional<ParkourGoal> findOne(Location location) {
        return Optional.ofNullable(cache.get(location));
    }

    @Override
    public void addOne(ParkourGoal goal) {
        try(final Connection connection = databaseManager.getConnection()) {
            var statement = connection.prepareStatement("""
                INSERT INTO parkour_goal (level, world, x, y, z)
                VALUES (?, ?, ?, ?, ?)
                ON DUPLICATE KEY
                UPDATE level=VALUES(level);
                """
            );

            statement.setShort(1, goal.level().level());
            statement.setString(2, goal.target().getWorld().getName());
            statement.setInt(3, goal.target().getBlockX());
            statement.setInt(4, goal.target().getBlockY());
            statement.setInt(5, goal.target().getBlockZ());
            statement.executeUpdate();
            cache.put(goal.target(), goal);
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Failed inserting ParkourGoal.", e);
        }
    }

    @Override
    public void addAll(Collection<ParkourGoal> all) {
        try (final Connection connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);
            var statement = connection.prepareStatement("""
                INSERT INTO parkour_goal (level, world, x, y, z)
                VALUES (?, ?, ?, ?, ?)
                ON DUPLICATE KEY
                UPDATE level=VALUES(level);
                """
            );

            for (ParkourGoal goal : all) {
                cache.put(goal.target(), goal);
                statement.setShort(1, goal.level().level());
                statement.setString(2, goal.target().getWorld().getName());
                statement.setInt(3, goal.target().getBlockX());
                statement.setInt(4, goal.target().getBlockY());
                statement.setInt(5, goal.target().getBlockZ());
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed inserting ParkourGoal.", e);
        }
    }

    @Override
    public void deleteAll(Collection<ParkourGoal> all) {
        try (final Connection connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);

            final PreparedStatement statement = connection.prepareStatement("""
                DELETE FROM parkour_goal
                WHERE world = ? AND x = ? AND y = ? AND z = ?;
                """
            );

            for (ParkourGoal goal : new ArrayList<>(all)) {
                statement.setString(1, goal.target().getWorld().getName());
                statement.setInt(2, goal.target().getBlockX());
                statement.setInt(3, goal.target().getBlockY());
                statement.setInt(4, goal.target().getBlockZ());
                statement.addBatch();
                this.cache.remove(goal.target());
            }

            statement.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed deleting ParkourGoal.", e);
        }
    }

    @Override
    public void delete(ParkourGoal goal) {
        try (final Connection connection = databaseManager.getConnection()) {
            final PreparedStatement statement = connection.prepareStatement("""
                DELETE FROM parkour_goal
                WHERE world = ? AND x = ? AND y = ? AND z = ?;
                """
            );
            statement.setString(1, goal.target().getWorld().getName());
            statement.setInt(2, goal.target().getBlockX());
            statement.setInt(3, goal.target().getBlockY());
            statement.setInt(4, goal.target().getBlockZ());

            statement.executeUpdate();
            this.cache.clear();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed deleting ParkourGoal.", e);
        }
    }
}
