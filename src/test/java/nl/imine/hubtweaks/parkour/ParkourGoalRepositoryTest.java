package nl.imine.hubtweaks.parkour;

import nl.imine.hubtweaks.db.DatabaseManager;
import nl.imine.hubtweaks.parkour.model.ParkourGoal;
import nl.imine.hubtweaks.parkour.model.ParkourLevel;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static nl.imine.hubtweaks.parkour.TestContainer.MARIA_DB;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class ParkourGoalRepositoryTest {

    private static final ParkourLevel PARKOUR_LEVEL_ONE = new ParkourLevel((short) 1, false, DyeColor.WHITE);
    private static final ParkourLevel PARKOUR_LEVEL_TWO = new ParkourLevel((short) 2, false, DyeColor.LIGHT_BLUE);
    private static final ParkourLevel PARKOUR_LEVEL_THREE = new ParkourLevel((short) 3, false, DyeColor.LIME);

    public static final String WORLD_NAME = "world";

    private Logger spyLogger;
    private World mockWorld;

    private ParkourLevelRepository parkourLevelRepository;
    private ParkourGoalRepository subject;

    @BeforeEach
    void setUp() {
        Flyway.configure()
            .dataSource(MARIA_DB.getJdbcUrl(), MARIA_DB.getUsername(), MARIA_DB.getPassword())
            .loggers("nl.imine.hubtweaks.parkour.FlywayLogger")
            .locations("db/migration")
            .load()
            .migrate();

        MockBukkit.open();

        spyLogger = spy(Logger.getLogger(this.getClass().getSimpleName()));

        World mockWorld = mock(World.class);
        doReturn(WORLD_NAME).when(mockWorld).getName();

        MockBukkit.getMock().when(() -> Bukkit.getWorld(WORLD_NAME)).thenReturn(mockWorld);

        final DatabaseManager databaseManager = new DatabaseManager(MARIA_DB.getJdbcUrl(), MARIA_DB.getUsername(), MARIA_DB.getPassword());
        parkourLevelRepository = new ParkourLevelRepository(spyLogger, databaseManager);
        subject = new ParkourGoalRepository(spyLogger, databaseManager, parkourLevelRepository);

        parkourLevelRepository.addAll(List.of(PARKOUR_LEVEL_ONE, PARKOUR_LEVEL_TWO, PARKOUR_LEVEL_THREE));
    }

    @AfterEach
    void tearDown() {
        subject.deleteAll(subject.getAll());
        MockBukkit.close();
        parkourLevelRepository.deleteAll(List.of(PARKOUR_LEVEL_ONE, PARKOUR_LEVEL_TWO, PARKOUR_LEVEL_THREE));
        verifyNoMoreInteractions(spyLogger);
    }

    @Test
    void test_addOne() {
        subject.addOne(new ParkourGoal(PARKOUR_LEVEL_ONE, new Location(Bukkit.getWorld(WORLD_NAME), 50, 128, 60)));
        subject.loadAll();

        assertThat(subject.findOne(new Location(Bukkit.getWorld(WORLD_NAME), 50, 128, 60)), equalTo(Optional.of(new ParkourGoal(PARKOUR_LEVEL_ONE, new Location(Bukkit.getWorld(WORLD_NAME), 50, 128, 60)))));
    }

    @Test
    void test_addAll() {
        subject.addAll(List.of(
            new ParkourGoal(PARKOUR_LEVEL_ONE, new Location(Bukkit.getWorld(WORLD_NAME), 50, 128, 60)),
            new ParkourGoal(PARKOUR_LEVEL_ONE, new Location(Bukkit.getWorld(WORLD_NAME), 50, 5, 60))
        ));
        subject.loadAll();

        final Collection<ParkourGoal> all = subject.getAll();
        assertThat(all, containsInAnyOrder(
            new ParkourGoal(PARKOUR_LEVEL_ONE, new Location(Bukkit.getWorld(WORLD_NAME), 50, 128, 60)),
            new ParkourGoal(PARKOUR_LEVEL_ONE, new Location(Bukkit.getWorld(WORLD_NAME), 50, 5, 60))
        ));
    }

    @Test
    void test_delete() {
        subject.addAll(List.of(
            new ParkourGoal(PARKOUR_LEVEL_ONE, new Location(Bukkit.getWorld(WORLD_NAME), 50, 128, 60)),
            new ParkourGoal(PARKOUR_LEVEL_ONE, new Location(Bukkit.getWorld(WORLD_NAME), 50, 5, 60))
        ));
        subject.loadAll();

        subject.findOne(new Location(Bukkit.getWorld(WORLD_NAME), 50, 128, 60)).ifPresent(subject::delete);
        subject.loadAll();

        assertThat(subject.getAll(), contains(
            new ParkourGoal(PARKOUR_LEVEL_ONE, new Location(Bukkit.getWorld(WORLD_NAME), 50, 5, 60))
        ));
    }

    @Test
    void test_deleteAll() {
        subject.addAll(List.of(
            new ParkourGoal(PARKOUR_LEVEL_ONE, new Location(Bukkit.getWorld(WORLD_NAME), 50, 128, 60)),
            new ParkourGoal(PARKOUR_LEVEL_ONE, new Location(Bukkit.getWorld(WORLD_NAME), 50, 5, 60)),
            new ParkourGoal(PARKOUR_LEVEL_ONE, new Location(Bukkit.getWorld(WORLD_NAME), 1, 2, 3))
        ));
        subject.loadAll();

        subject.deleteAll(List.of(
            new ParkourGoal(PARKOUR_LEVEL_ONE, new Location(Bukkit.getWorld(WORLD_NAME), 50, 128, 60)),
            new ParkourGoal(PARKOUR_LEVEL_ONE, new Location(Bukkit.getWorld(WORLD_NAME), 1, 2, 3))
        ));
        subject.loadAll();

        assertThat(subject.getAll(), contains(
            new ParkourGoal(PARKOUR_LEVEL_ONE, new Location(Bukkit.getWorld(WORLD_NAME), 50, 5, 60))
        ));
    }
}
