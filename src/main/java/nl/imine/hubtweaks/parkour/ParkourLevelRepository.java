package nl.imine.hubtweaks.parkour;

import nl.imine.hubtweaks.db.DatabaseManager;
import nl.imine.hubtweaks.db.Repository;
import nl.imine.hubtweaks.parkour.model.ParkourLevel;
import org.bukkit.DyeColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParkourLevelRepository implements Repository<Short, ParkourLevel> {
    private final Logger logger;

    private static final String COLUMN_LEVEL = "level";
    private static final String COLUMN_BONUS = "bonus";
    private static final String COLUMN_REWARD = "reward";

    private final DatabaseManager databaseManager;

    private Map<Short, ParkourLevel> cache;

    public ParkourLevelRepository(Logger logger, DatabaseManager databaseManager) {
        this.logger = logger;
        this.databaseManager = databaseManager;
        this.cache = new HashMap<>();
    }

    @Override
    public void loadAll() {
        cache = new HashMap<>();
        try(Connection connection = databaseManager.getConnection()) {
            var sql = """
                SELECT *
                FROM parkour_level;
                """;
            final ResultSet resultSet = connection.prepareStatement(sql).executeQuery();
            while (resultSet.next()) {
                final short level = resultSet.getShort(COLUMN_LEVEL);
                cache.put(level, new ParkourLevel(level, resultSet.getBoolean(COLUMN_BONUS), DyeColor.valueOf(resultSet.getString(COLUMN_REWARD))));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed initializing ParkourLevel cache.", e);
        }
    }

    @Override
    public Collection<ParkourLevel> getAll() {
        return cache.values();
    }

    @Override
    public Optional<ParkourLevel> findOne(Short id) {
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public void addOne(ParkourLevel item) {
        try(Connection connection = databaseManager.getConnection()) {
            cache.put(item.level(), item);
            var statement = connection.prepareStatement("""
                INSERT INTO parkour_level (level, bonus, reward)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY
                UPDATE level=VALUES(level), bonus=VALUES(bonus), reward=VALUES(reward);
                """
            );

            statement.setShort(1, item.level());
            statement.setBoolean(2, item.bonusLevel());
            statement.setString(3, item.reward().name());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Failed inserting ParkourLevel.", e);
        }
    }

    @Override
    public void addAll(Collection<ParkourLevel> all) {
        try (final Connection connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);
            var statement = connection.prepareStatement("""
                INSERT INTO parkour_level (level, bonus, reward)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY
                UPDATE level=VALUES(level), bonus=VALUES(bonus), reward=VALUES(reward);
                """
            );

            for (ParkourLevel parkourLevel : all) {
                cache.put(parkourLevel.level(), parkourLevel);
                statement.setShort(1, parkourLevel.level());
                statement.setBoolean(2, parkourLevel.bonusLevel());
                statement.setString(3, parkourLevel.reward().name());
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed inserting ParkourLevels.", e);
        }
    }

    @Override
    public void deleteAll(Collection<ParkourLevel> all) {
        try (final Connection connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);

            final PreparedStatement statement = connection.prepareStatement("""
                DELETE FROM parkour_level
                WHERE level = ?;
                """
            );

            for (ParkourLevel level : new ArrayList<>(all)) {
                statement.setShort(1, level.level());
                statement.addBatch();
                this.cache.remove(level.level());
            }

            statement.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed deleting ParkourLevels.", e);
        }
    }

    @Override
    public void delete(ParkourLevel level) {
        try (final Connection connection = databaseManager.getConnection()) {
            final PreparedStatement statement = connection.prepareStatement("""
                DELETE FROM parkour_level
                WHERE level = ?;
                """
            );
            statement.setShort(1, level.level());

            statement.executeUpdate();
            this.cache.clear();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed deleting ParkourLevels.", e);
        }
    }

    public Optional<ParkourLevel> getHighestLevel() {
        return getAll().stream()
            .filter(Predicate.not(ParkourLevel::bonusLevel))
            .max(Comparator.comparingInt(ParkourLevel::level));
    }
}
