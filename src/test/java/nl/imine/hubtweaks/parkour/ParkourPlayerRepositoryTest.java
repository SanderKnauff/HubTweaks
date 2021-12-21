package nl.imine.hubtweaks.parkour;

import nl.imine.hubtweaks.db.DatabaseManager;
import nl.imine.hubtweaks.parkour.model.ParkourLevel;
import nl.imine.hubtweaks.parkour.model.ParkourPlayer;
import nl.imine.hubtweaks.parkour.model.ParkourTiming;
import org.bukkit.DyeColor;
import org.flywaydb.core.Flyway;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static nl.imine.hubtweaks.parkour.TestContainer.MARIA_DB;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class ParkourPlayerRepositoryTest {

    private static final ParkourLevel PARKOUR_LEVEL_ONE = new ParkourLevel((short) 1, false, DyeColor.WHITE);
    private static final ParkourLevel PARKOUR_LEVEL_TWO = new ParkourLevel((short) 2, false, DyeColor.LIGHT_BLUE);
    private static final ParkourLevel PARKOUR_LEVEL_THREE = new ParkourLevel((short) 3, false, DyeColor.LIME);

    private Logger spyLogger;

    private ParkourLevelRepository parkourLevelRepository;

    private ParkourPlayerRepository subject;

    @BeforeEach
    void setUp() {
        Flyway.configure()
            .dataSource(MARIA_DB.getJdbcUrl(), MARIA_DB.getUsername(), MARIA_DB.getPassword())
            .locations("db/migration")
            .load()
            .migrate();

        spyLogger = spy(Logger.getLogger(this.getClass().getSimpleName()));

        final DatabaseManager databaseManager = new DatabaseManager(MARIA_DB.getJdbcUrl(), MARIA_DB.getUsername(), MARIA_DB.getPassword());
        parkourLevelRepository = new ParkourLevelRepository(spyLogger, databaseManager);
        subject = new ParkourPlayerRepository(spyLogger, databaseManager, parkourLevelRepository);

        parkourLevelRepository.addAll(List.of(PARKOUR_LEVEL_ONE, PARKOUR_LEVEL_TWO, PARKOUR_LEVEL_THREE));
    }

    @Test
    void test_addOne() {
        var id = UUID.randomUUID();
        final ParkourTiming timingOne = new ParkourTiming(Instant.parse("2021-11-22T00:00:00.000z"), PARKOUR_LEVEL_ONE, 10L);
        final ParkourTiming timingTwo = new ParkourTiming(Instant.parse("2021-11-22T00:01:00.000z"), PARKOUR_LEVEL_TWO, 20L);
        Map<ParkourLevel, ParkourTiming> timings = Map.of(
            timingOne.segment(), timingOne,
            timingTwo.segment(), timingTwo
        );
        subject.addOne(new ParkourPlayer(id, PARKOUR_LEVEL_TWO, timings));
        subject.loadAll();

        final ParkourPlayer one = subject.findOne(id).orElse(null);
        assertThat(one.getUuid(), equalTo(id));
        assertThat(one.getHighestLevel(), equalTo(Optional.of(PARKOUR_LEVEL_TWO)));
        assertThat(one.getTimings(), hasEntry(PARKOUR_LEVEL_ONE, new ParkourTiming(Instant.parse("2021-11-22T00:00:00.000z"), PARKOUR_LEVEL_ONE, 10L)));
        assertThat(one.getTimings(), hasEntry(PARKOUR_LEVEL_TWO, new ParkourTiming(Instant.parse("2021-11-22T00:01:00.000z"), PARKOUR_LEVEL_TWO, 20L)));
    }

    @Test
    void test_addAll() {
        var idOne = UUID.randomUUID();
        var idTwo = UUID.randomUUID();
        Map<ParkourLevel, ParkourTiming> timingsOne = Map.of(
            PARKOUR_LEVEL_ONE, new ParkourTiming(Instant.parse("2021-11-22T00:00:00.000z"), PARKOUR_LEVEL_ONE, 10L)
        );
        Map<ParkourLevel, ParkourTiming> timingsTwo = Map.of(
            PARKOUR_LEVEL_ONE, new ParkourTiming(Instant.parse("2021-11-22T00:02:00.000z"), PARKOUR_LEVEL_ONE, 30L),
            PARKOUR_LEVEL_TWO, new ParkourTiming(Instant.parse("2021-11-22T00:03:00.000z"), PARKOUR_LEVEL_TWO, 40L)
        );
        final ParkourPlayer player1 = new ParkourPlayer(idOne, PARKOUR_LEVEL_ONE, timingsOne);
        final ParkourPlayer player2 = new ParkourPlayer(idTwo, PARKOUR_LEVEL_TWO, timingsTwo);
        subject.addAll(List.of(
            player1,
            player2
        ));
        subject.loadAll();

        assertThat(subject.getAll(), containsInAnyOrder(player1, player2));
    }

    @Test
    void test_delete() {
        var idOne = UUID.randomUUID();
        var idTwo = UUID.randomUUID();
        Map<ParkourLevel, ParkourTiming> timingsOne = Map.of(
            PARKOUR_LEVEL_ONE, new ParkourTiming(Instant.parse("2021-11-22T00:00:00.000z"), PARKOUR_LEVEL_ONE, 10L)
        );
        Map<ParkourLevel, ParkourTiming> timingsTwo = Map.of(
            PARKOUR_LEVEL_ONE, new ParkourTiming(Instant.parse("2021-11-22T00:02:00.000z"), PARKOUR_LEVEL_ONE, 30L),
            PARKOUR_LEVEL_TWO, new ParkourTiming(Instant.parse("2021-11-22T00:03:00.000z"), PARKOUR_LEVEL_TWO, 40L)
        );
        final ParkourPlayer player1 = new ParkourPlayer(idOne, PARKOUR_LEVEL_ONE, timingsOne);
        final ParkourPlayer player2 = new ParkourPlayer(idTwo, PARKOUR_LEVEL_TWO, timingsTwo);
        subject.addAll(List.of(
            player1,
            player2
        ));
        subject.loadAll();

        subject.findOne(player1.getUuid()).ifPresent(subject::delete);
        subject.loadAll();

        assertThat(subject.getAll(), contains(player2));
    }

    @Test
    void test_deleteAll() {
        var idOne = UUID.randomUUID();
        var idTwo = UUID.randomUUID();
        var idThree = UUID.randomUUID();
        final ParkourPlayer player1 = new ParkourPlayer(idOne, PARKOUR_LEVEL_ONE, Map.of());
        final ParkourPlayer player2 = new ParkourPlayer(idTwo, PARKOUR_LEVEL_TWO, Map.of());
        final ParkourPlayer player3 = new ParkourPlayer(idThree, PARKOUR_LEVEL_THREE, Map.of());
        subject.addAll(List.of(
            player1,
            player2,
            player3
        ));
        subject.loadAll();

        subject.deleteAll(List.of(
            player1,
            player2
        ));
        subject.loadAll();

        assertThat(subject.getAll(), contains(
            player3
        ));
    }

    @AfterEach
    void tearDown() {
        subject.loadAll();
        subject.deleteAll(new ArrayList<>(subject.getAll()));
        parkourLevelRepository.deleteAll(List.of(PARKOUR_LEVEL_ONE, PARKOUR_LEVEL_TWO, PARKOUR_LEVEL_THREE));
        verifyNoMoreInteractions(spyLogger);
    }
}
