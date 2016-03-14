package nl.imine.hubtweaks.parkour;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import nl.imine.api.Credentials;
import nl.imine.api.db.DatabaseManager;
import nl.imine.api.util.ItemUtil;
import nl.imine.hubtweaks.HubTweaks;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ParkourManager implements Listener {

	private static Parkour parkour;

	public static final DatabaseManager DM = new DatabaseManager(Credentials.getUrl(), Credentials.getUsername(),
			Credentials.getPassword(), "iMine_Statistics");

	public static void init() {
		List<ParkourLevel> levels = loadLevels();
		if (levels.isEmpty()) {
			ParkourLevel level = new ParkourLevel((short) 0, true, DyeColor.BLACK);
			levels.add(level);
			saveLevel(level);
		}
		parkour = new Parkour(levels);
		Bukkit.getPluginManager().registerEvents(new ParkourManager(), HubTweaks.getInstance());
	}

	@EventHandler
	public void onPressurePlateInteract(PlayerInteractEvent evt) {
		// Check if the player interacted with a pressure plate.
		if (evt.getAction().equals(Action.PHYSICAL)) {
			// Check if the pressureplate is part of the parkour.
			if (parkour.isParkourGoal(evt.getClickedBlock().getLocation())) {
				ParkourPlayer player = parkour.getParkourPlayer(evt.getPlayer());
				ParkourGoal goal = parkour.getParkourGoal(evt.getClickedBlock().getLocation());

				// Increase player highest level if he reached a new checkpoint.
				if (player.getHighestLevel().getLevel() < goal.getLevel().getLevel()) {
					player.setHighestLevel(goal.getLevel());
				}

				// Give the player his boots if he has reached a level before.
				if (player.getHighestLevel().getLevel() != 0) {
					ItemStack boots = ItemUtil.getBuilder(Material.LEATHER_BOOTS).build();
					LeatherArmorMeta meta = (LeatherArmorMeta) boots.getItemMeta();
					meta.setColor(player.getHighestLevel().getReward().getColor());
					boots.setItemMeta(meta);
					Bukkit.getPlayer(player.getUuid()).getInventory().setBoots(boots);
				}

				// Handle pending timings.
				new ArrayList<>(player.getPendingTimes()).stream().filter(t -> t.getDestLevel().equals(goal.getLevel()))
						.forEach(t -> {
							t.setTimeMiliseconds(System.currentTimeMillis() - t.getTimeMiliseconds());
							t.setDateObtained(Timestamp.from(Instant.now()));
							player.addTiming(t);
							player.removePendingTime(t);
						});

				// Create new timings.
				ParkourLevel lastLevel = parkour.getLevels().stream().filter(l -> !l.isBonusLevel())
						.sorted((ParkourLevel p1, ParkourLevel p2) -> p1.getLevel() - p2.getLevel()).findFirst().get();
				// Time between levels
				if (goal.getLevel().getLevel() < lastLevel.getLevel()) {
					player.addPendingTime(new ParkourTiming(null, goal.getLevel(),
							parkour.getLevel((short) (goal.getLevel().getLevel() + 1)), System.currentTimeMillis()));
				}
				// Overall Timing
				if (goal.getLevel().getLevel() == 0) {
					player.addPendingTime(
						new ParkourTiming(null, goal.getLevel(), lastLevel, System.currentTimeMillis()));
				}
			}
		}
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent evt) {
		if (parkour.getParkourPlayer(evt.getPlayer()) == null) {
			ResultSet rs = DM.selectQuery("SELECT * FROM parkour_player WHERE uuid LIKE '%s' LIMIT 1",
				evt.getPlayer().getUniqueId().toString());
			ParkourPlayer parkourPlayer = null;
			try {
				while (rs.next()) {
					ResultSet timingSet = DM.selectQuery("SELECT * FROM parkour_timing WHERE uuid LIKE '%s'",
						evt.getPlayer().getUniqueId().toString());
					List<ParkourTiming> timings = new ArrayList<>();
					while (timingSet.next()) {
						timings.add(new ParkourTiming(timingSet.getTimestamp("dateObtained"),
								parkour.getLevel((short) timingSet.getInt("origin")),
								parkour.getLevel((short) timingSet.getInt("destination")), timingSet.getLong("time")));
					}
					parkourPlayer = new ParkourPlayer(evt.getPlayer().getUniqueId(),
							parkour.getLevel((short) rs.getInt("level")), timings);
				}
				if (parkourPlayer == null) {
					parkourPlayer = new ParkourPlayer(evt.getPlayer().getUniqueId(), parkour.getLevel((short) 0),
							new ArrayList<>());
					DM.insertQuery("INSERT INTO parkour_player VALUES(%s,0)", evt.getPlayer().getUniqueId());
				}
				parkourPlayer.save();
				parkour.addPlayer(parkourPlayer);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public ParkourLevel getLevel(short level) {
		return null;
	}

	private static void saveLevel(ParkourLevel level) {
		DM.insertQuery("INSERT INTO parkour_level VALUES (%s,%b,%s)", level.getLevel(), level.isBonusLevel(),
			level.getReward().name());
	}

	private static void saveGoal(ParkourGoal goal) {
		DM.insertQuery("INSERT INTO parkour_goal VALUES (%s,%s,%d,%d,%d)", goal.getLevel().getLevel(),
			goal.getTarget().getWorld().getUID(), goal.getTarget().getBlockX(), goal.getTarget().getBlockY(),
			goal.getTarget().getBlockZ());
	}

	public static List<ParkourLevel> loadLevels() {
		List<ParkourLevel> ret = new ArrayList<>();
		ResultSet rs = DM.selectQuery("SELECT * FROM parkour_level");
		try {
			while (rs.next()) {
				DyeColor reward = DyeColor.valueOf(rs.getString("reward"));
				ret.add(new ParkourLevel(rs.getShort("id"), rs.getBoolean("bonus"), reward));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static List<ParkourGoal> loadGoals() {
		List<ParkourGoal> ret = new ArrayList<>();
		ResultSet rs = DM.selectQuery("SELECT * FROM parkour_goal");
		try {
			while (rs.next()) {
				ParkourLevel level = parkour.getLevel((short) rs.getInt("id"));
				Location location = new Location(Bukkit.getWorld(UUID.fromString(rs.getString("worlduuid"))),
						rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
				ret.add(new ParkourGoal(level, location));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}
}
