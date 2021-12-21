package nl.imine.hubtweaks.parkour;

import nl.imine.hubtweaks.parkour.model.ParkourGoal;
import nl.imine.hubtweaks.parkour.model.ParkourLevel;
import nl.imine.hubtweaks.parkour.model.ParkourPlayer;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkourManagerTest {

    private static final ParkourLevel LEVEL_EQUIPMENT = new ParkourLevel((short) -1, false, DyeColor.BLACK);
    private static final ParkourLevel LEVEL_BASE = new ParkourLevel((short) 0, false, DyeColor.WHITE);
    private static final ParkourLevel LEVEL_1 = new ParkourLevel((short) 1, false, DyeColor.RED);
    private static final ParkourLevel LEVEL_2 = new ParkourLevel((short) 2, false, DyeColor.BLUE);
    private static final ParkourLevel LEVEL_3 = new ParkourLevel((short) 3, false, DyeColor.YELLOW);

    private World mockWorld;

    @Mock
    private Logger logger;
    @Mock
    private Player mockPlayer;
    @Mock
    private ParkourPlayer spyParkourPlayer;
    @Mock
    private ParkourLevelRepository mockLevelRepository;
    @Mock
    private ParkourPlayerRepository mockPlayerRepository;
    @Mock
    private ParkourGoalRepository mockGoalRepository;

    @InjectMocks
    private ParkourManager subject;

    @BeforeEach
    void setUp() {
        MockBukkit.open();

        initItemFactory();

        mockWorld = mock(World.class);

        initLevels();
        initGoals();
        initPlayer();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.close();
    }

    @Test
    void onPressurePlateInteract_NextPlate_ShouldIncreaseLevel() {
        final var mockBlock = createBlock(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, mockWorld, 5, 5,5);
        subject.onPressurePlateInteract(new PlayerInteractEvent(
            mockPlayer, Action.PHYSICAL, null, mockBlock, BlockFace.UP, null
        ));

        verify(spyParkourPlayer).setHighestLevel(LEVEL_3);
    }

    @Test
    void onPressurePlateInteract_SamePlateAsCurrent_ShouldDoNothing() {
        final var mockBlock = createBlock(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, mockWorld, 4, 4,4);
        subject.onPressurePlateInteract(new PlayerInteractEvent(
            mockPlayer, Action.PHYSICAL, null, mockBlock, BlockFace.UP, null
        ));

        verify(spyParkourPlayer, never()).setHighestLevel(any(ParkourLevel.class));
    }

    @Test
    void onPressurePlateInteract_Equipment_ShouldGiveEquipment_NoElytraWhenNotFinished() {
        final var mockBlock = createBlock(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, mockWorld, 1, 1,1);
        subject.onPressurePlateInteract(new PlayerInteractEvent(
            mockPlayer, Action.PHYSICAL, null, mockBlock, BlockFace.UP, null
        ));

        verify(mockPlayer.getInventory()).setBoots(any());
        verify(mockPlayer.getInventory(), never()).setChestplate(any());
    }

    @Test
    void onPressurePlateInteract_Equipment_ShouldGiveEquipment_GiveElytraWhenFinished() {
        spyParkourPlayer.setHighestLevel(LEVEL_3);

        final var mockBlock = createBlock(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, mockWorld, 1, 1,1);
        subject.onPressurePlateInteract(new PlayerInteractEvent(
            mockPlayer, Action.PHYSICAL, null, mockBlock, BlockFace.UP, null
        ));

        verify(mockPlayer.getInventory()).setBoots(any());
        verify(mockPlayer.getInventory()).setChestplate(any());
    }

    @Test
    void onPressurePlateInteract_Base_ShouldStartTiming() {
        final var mockBlock = createBlock(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, mockWorld, 2, 2,2);
        subject.onPressurePlateInteract(new PlayerInteractEvent(
            mockPlayer, Action.PHYSICAL, null, mockBlock, BlockFace.UP, null
        ));

    }

    private Block createBlock(Material material, World world, int x, int y, int z) {
        final var block = mock(Block.class);

        lenient().when(block.getType()).thenReturn(material);
        when(block.getLocation()).thenReturn(new Location(world, x, y, z));

        return block;
    }

    private void initItemFactory() {
        ItemFactory mockItemFactory = mock(ItemFactory.class);
        MockBukkit.getMock().when(Bukkit::getItemFactory).thenReturn(mockItemFactory);
    }

    private void initLevels() {
        final List<ParkourLevel> levels = List.of(LEVEL_EQUIPMENT, LEVEL_BASE, LEVEL_1, LEVEL_2, LEVEL_3);
        lenient().when(mockLevelRepository.getAll()).thenReturn(levels);
        for (ParkourLevel level : levels) {
            lenient().when(mockLevelRepository.findOne(level.level())).thenReturn(Optional.of(level));
        }
    }

    private void initPlayer() {
        UUID playerId = UUID.randomUUID();
        spyParkourPlayer = spy(new ParkourPlayer(playerId, LEVEL_2, new HashMap<>()));
        when(mockPlayer.getUniqueId()).thenReturn(playerId);
        lenient().when(mockPlayer.getInventory()).thenReturn(mock(PlayerInventory.class));
        when(mockPlayerRepository.findOne(playerId)).thenReturn(Optional.of(spyParkourPlayer));
        MockBukkit.getMock().when(() -> Bukkit.getPlayer(playerId)).thenReturn(mockPlayer);
    }

    private void initGoals() {
        List.of(
            new ParkourGoal(LEVEL_EQUIPMENT, new Location(mockWorld, 1, 1, 1)),
            new ParkourGoal(LEVEL_BASE, new Location(mockWorld, 2, 2, 2)),
            new ParkourGoal(LEVEL_1, new Location(mockWorld, 3, 3, 3)),
            new ParkourGoal(LEVEL_2, new Location(mockWorld, 4, 4, 4)),
            new ParkourGoal(LEVEL_3, new Location(mockWorld, 5, 5, 5))
        ).forEach(goal -> {
            lenient().when(mockGoalRepository.findOne(goal.target())).thenReturn(Optional.of(goal));
        });
    }
}
