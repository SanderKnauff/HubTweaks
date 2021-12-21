package nl.imine.hubtweaks.parkour;

import nl.imine.hubtweaks.db.DatabaseManager;
import nl.imine.hubtweaks.parkour.model.ParkourLevel;
import org.bukkit.DyeColor;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static nl.imine.hubtweaks.parkour.TestContainer.MARIA_DB;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class ParkourLevelRepositoryTest {

    private Logger spyLogger;

    private ParkourLevelRepository subject;

    @BeforeEach
    void setUp() {
        Flyway.configure()
            .dataSource(MARIA_DB.getJdbcUrl(), MARIA_DB.getUsername(), MARIA_DB.getPassword())
            .locations("db/migration")
            .load()
            .migrate();

        spyLogger = spy(Logger.getLogger(this.getClass().getSimpleName()));

        subject = new ParkourLevelRepository(spyLogger, new DatabaseManager(MARIA_DB.getJdbcUrl(), MARIA_DB.getUsername(), MARIA_DB.getPassword()));
    }

    @Test
    void test_addOne() {
        subject.addOne(new ParkourLevel((short) 3, true, DyeColor.BLUE));
        subject.loadAll();

        assertThat(subject.findOne((short) 3), equalTo(Optional.of(new ParkourLevel((short) 3, true, DyeColor.BLUE))));
    }

    @Test
    void test_addAll() {
        subject.addAll(List.of(
            new ParkourLevel((short) 1, true, DyeColor.BLACK),
            new ParkourLevel((short) 2, true, DyeColor.RED)
        ));
        subject.loadAll();

        final Collection<ParkourLevel> all = subject.getAll();
        assertThat(all, contains(
            new ParkourLevel((short) 1, true, DyeColor.BLACK),
            new ParkourLevel((short) 2, true, DyeColor.RED)
        ));
    }

    @Test
    void test_delete() {
        subject.addAll(List.of(
            new ParkourLevel((short) 1, true, DyeColor.BLACK),
            new ParkourLevel((short) 2, true, DyeColor.RED)
        ));
        subject.loadAll();

        subject.findOne((short) 1).ifPresent(subject::delete);
        subject.loadAll();

        assertThat(subject.getAll(), contains(
            new ParkourLevel((short) 2, true, DyeColor.RED)
        ));
    }

    @Test
    void test_deleteAll() {
        subject.addAll(List.of(
            new ParkourLevel((short) 1, true, DyeColor.BLACK),
            new ParkourLevel((short) 2, true, DyeColor.RED),
            new ParkourLevel((short) 3, true, DyeColor.BLUE)
        ));
        subject.loadAll();

        subject.deleteAll(List.of(
            new ParkourLevel((short) 1, true, DyeColor.BLACK),
            new ParkourLevel((short) 3, true, DyeColor.BLUE)
        ));
        subject.loadAll();

        assertThat(subject.getAll(), contains(
            new ParkourLevel((short) 2, true, DyeColor.RED)
        ));
    }

    @AfterEach
    void tearDown() {
        subject.deleteAll(subject.getAll());
        verifyNoMoreInteractions(spyLogger);
    }
}
