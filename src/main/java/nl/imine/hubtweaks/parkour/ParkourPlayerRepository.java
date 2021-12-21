package nl.imine.hubtweaks.parkour;

import nl.imine.hubtweaks.db.DatabaseManager;
import nl.imine.hubtweaks.db.Repository;
import nl.imine.hubtweaks.parkour.model.ParkourGoal;
import nl.imine.hubtweaks.parkour.model.ParkourLevel;
import nl.imine.hubtweaks.parkour.model.ParkourPlayer;
import nl.imine.hubtweaks.parkour.model.ParkourTiming;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParkourPlayerRepository implements Repository<UUID, ParkourPlayer> {
    private final Logger logger;

    private static final String COLUMN_ID = "player_id";
    private static final String COLUMN_HIGHEST_LEVEL = "highest_level";

    private static final String COLUMN_SEGMENT = "segment";
    private static final String COLUMN_OBTAINED = "obtained";
    private static final String COLUMN_ELAPSED_TIME = "elapsed_time";

    private final DatabaseManager databaseManager;
    private final ParkourLevelRepository parkourLevelRepository;
    private Map<UUID, ParkourPlayer> cache;

    public ParkourPlayerRepository(Logger logger, DatabaseManager databaseManager, ParkourLevelRepository parkourLevelRepository) {
        this.logger = logger;
        this.databaseManager = databaseManager;
        this.parkourLevelRepository = parkourLevelRepository;
        this.cache = new HashMap<>();
    }

    @Override
    public void loadAll() {
        cache = new HashMap<>();
        try (Connection connection = databaseManager.getConnection()) {
            var sql = """
                SELECT player_id, highest_level 
                FROM parkour_player;
                """;
            final ResultSet resultSet = connection.prepareStatement(sql).executeQuery();
            while (resultSet.next()) {
                final UUID id = UUID.fromString(resultSet.getString(COLUMN_ID));
                final ParkourLevel level = parkourLevelRepository.findOne(resultSet.getShort(COLUMN_HIGHEST_LEVEL)).orElse(null);
                final Map<ParkourLevel, ParkourTiming> timings = getTimings(connection, id);
                cache.put(id, new ParkourPlayer(id, level, timings));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed initializing ParkourPlayer cache.", e);
        }
    }

    private Map<ParkourLevel, ParkourTiming> getTimings(Connection connection, UUID id) throws SQLException {
        final var selectTimings = connection.prepareStatement("""
            SELECT segment, obtained, elapsed_time 
            FROM parkour_timing
            WHERE player_id = ?;
            """
        );
        Map<ParkourLevel, ParkourTiming> timings = new HashMap<>();
        selectTimings.setString(1, id.toString());
        var resultSet = selectTimings.executeQuery();
        while (resultSet.next()) {
            final ParkourLevel level = parkourLevelRepository.findOne(resultSet.getShort(COLUMN_SEGMENT)).orElse(null);
            if (level != null) {
                timings.put(level, new ParkourTiming(
                    resultSet.getTimestamp(COLUMN_OBTAINED).toInstant(),
                    level,
                    resultSet.getLong(COLUMN_ELAPSED_TIME)
                ));
            }
        }
        return timings;
    }

    @Override
    public Collection<ParkourPlayer> getAll() {
        return this.cache.values();
    }

    @Override
    public Optional<ParkourPlayer> findOne(UUID id) {
        return Optional.ofNullable(this.cache.get(id));
    }

    @Override
    public void addOne(ParkourPlayer player) {
        this.cache.put(player.getUuid(), player);
        try (Connection connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);
            var insertPlayer = connection.prepareStatement("""
                INSERT INTO parkour_player(player_id, highest_level)
                VALUES (?, ?)
                ON DUPLICATE KEY
                UPDATE player_id=VALUES(player_id), highest_level=VALUES(highest_level);
                """
            );
            insertPlayer.setString(1, player.getUuid().toString());
            insertPlayer.setShort(2, player.getHighestLevel().map(l -> l.level()).orElse((short) 0));
            insertPlayer.executeUpdate();

            final var deleteTimings = connection.prepareStatement("""
                    DELETE FROM parkour_timing
                    WHERE player_id = ?;
                """
            );
            deleteTimings.setString(1, player.getUuid().toString());
            deleteTimings.executeUpdate();

            var insertTimings = connection.prepareStatement("""
                INSERT INTO parkour_timing(player_id, segment, obtained, elapsed_time)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY
                UPDATE player_id=VALUES(player_id), segment=VALUES(segment), obtained=VALUES(obtained), elapsed_time=VALUES(elapsed_time);
                """
            );
            for (ParkourTiming timing : player.getTimings().values()) {
                insertTimings.setString(1, player.getUuid().toString());
                insertTimings.setShort(2, timing.segment().level());
                insertTimings.setTimestamp(3, Timestamp.from(timing.obtained()));
                insertTimings.setLong(4, timing.elapsedTime());
                insertTimings.addBatch();
            }

            insertTimings.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed initializing ParkourPlayer cache.", e);
        }
    }


    @Override
    public void delete(ParkourPlayer item) {
        try (Connection connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);
            final var deleteTimings = connection.prepareStatement("""
                DELETE FROM parkour_timing
                WHERE player_id = ?;
                """
            );
            deleteTimings.setString(1, item.getUuid().toString());
            deleteTimings.executeUpdate();

            final var deletePlayers = connection.prepareStatement("""
                DELETE FROM parkour_player
                WHERE player_id = ?;
                """
            );
            deletePlayers.setString(1, item.getUuid().toString());
            deletePlayers.executeUpdate();

            connection.commit();
            connection.setAutoCommit(true);
            this.cache.remove(item.getUuid());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed initializing ParkourPlayer cache.", e);
        }
    }


}
