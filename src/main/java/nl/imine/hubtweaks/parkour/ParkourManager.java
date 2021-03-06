package nl.imine.hubtweaks.parkour;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import nl.imine.api.db.DatabaseManager;
import nl.imine.api.iMineAPI;
import nl.imine.api.util.ColorUtil;
import nl.imine.api.util.ItemUtil;
import nl.imine.api.util.LocationUtil;
import nl.imine.api.util.PlayerUtil;
import nl.imine.hubtweaks.HubTweaks;
import nl.imine.hubtweaks.Statistic;
import nl.imine.hubtweaks.Util;
import static nl.imine.hubtweaks.Util.readFromFile;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ParkourManager implements Listener {

	private static Parkour parkour;

	public static final DatabaseManager DM = iMineAPI.getDatabaseManager();

	private static final File TIMINGS_LIST = new File(HubTweaks.getInstance().getDataFolder(), "Timings.json");

	public static void init() {
		List<ParkourLevel> levels = loadLevels();
		levels.add(ParkourLevel.START_LEVEL);
		levels.add(ParkourLevel.EQUIPMENT_LEVEL);
		parkour = new Parkour(levels);
		parkour.addGoals(loadGoals());
		Bukkit.getOnlinePlayers().stream().forEach(p -> loadParkourPlayer(p));
		Bukkit.getPluginManager().registerEvents(new ParkourManager(), HubTweaks.getInstance());
		Bukkit.getPluginManager().registerEvents(new ParkourAntiCheat(), HubTweaks.getInstance());
	}

	public static void loadTimings() {
		String json = readFromFile(TIMINGS_LIST);
		Gson gson = new GsonBuilder().create();
		JsonObject mainObject = gson.fromJson(json, JsonObject.class);
		mainObject.entrySet().stream().forEach(e -> {
			UUID uuid = UUID.fromString(e.getKey());
			JsonArray timings = (JsonArray) e.getValue();
			Player owner = Bukkit.getPlayer(uuid);
			if (owner != null) {
				timings.forEach(timingElement -> {
					JsonObject timing = timingElement.getAsJsonObject();
					parkour.getParkourPlayer(owner.getPlayer())
							.addPendingTime(new ParkourTiming(
									null, ParkourManager.getParkourInstance().getLevel(
										timing.get("Origin").getAsShort()),
							ParkourManager.getParkourInstance().getLevel(timing.get("Destination").getAsShort()),
							timing.get("StartTime").getAsLong()));
				});
			}
		});
	}

	public static void saveTimings() {
		TIMINGS_LIST.delete();
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.setPrettyPrinting().create();
		JsonObject playerJson = new JsonObject();
		parkour.getPlayers().forEach(p -> {
			JsonArray timings = new JsonArray();
			p.getPendingTimes().stream().filter(t -> t.getDateObtained() == null).forEach(t -> {
				JsonObject timing = new JsonObject();
				timing.add("Origin", new JsonPrimitive(t.getStartLevel().getLevel()));
				timing.add("Destination", new JsonPrimitive(t.getDestLevel().getLevel()));
				timing.add("StartTime", new JsonPrimitive(t.getTimeMiliseconds()));
				timings.add(timing);
			});
			playerJson.add(p.getUuid().toString(), timings);
		});
		String json = gson.toJson(playerJson);
		Util.saveToFile(TIMINGS_LIST, new String[]{json});
	}

	@EventHandler
	public void onPressurePlateInteract(PlayerInteractEvent evt) {
		// Check if the player interacted with a pressure plate.
		if (evt.getAction().equals(Action.PHYSICAL)) {
			// Check if the pressureplate is part of the parkour.
			if (parkour.isParkourGoal(evt.getClickedBlock().getLocation())) {
				ParkourPlayer player = parkour.getParkourPlayer(evt.getPlayer());
				ParkourGoal goal = parkour.getParkourGoal(evt.getClickedBlock().getLocation());
				if (goal.getLevel().equals(ParkourLevel.EQUIPMENT_LEVEL)) {
					if (!player.getHighestLevel().equals(ParkourLevel.START_LEVEL)) {
						ItemStack boots = ItemUtil.getBuilder(Material.LEATHER_BOOTS).build();
						LeatherArmorMeta meta = (LeatherArmorMeta) boots.getItemMeta();
						meta.setColor(player.getHighestLevel().getReward().getColor());
						boots.setItemMeta(meta);
						Bukkit.getPlayer(player.getUuid()).getInventory().setBoots(boots);
					}
					if (player.getHighestLevel().equals(parkour.getFinalLevel(true))) {
						Bukkit.getPlayer(player.getUuid()).getInventory()
								.setChestplate(new ItemStack(ItemUtil.getBuilder(Material.ELYTRA).build()));
					}
					return;
				}
				if (player.hasCheated()) {
					return;
				}

				// Timings

				// Remove old timings
				new ArrayList<>(player.getPendingTimes()).stream()
						.filter(t -> t.getStartLevel().equals(goal.getLevel()))
						.forEach(t -> player.removePendingTime(t));

				// Handle pending timings.
				new ArrayList<>(player.getPendingTimes()).stream().filter(t -> t.getDestLevel().equals(goal.getLevel()))
						.forEach(t -> {
							t.setTimeMiliseconds(System.currentTimeMillis() - t.getTimeMiliseconds());
							t.setDateObtained(Timestamp.from(Instant.now()));
							if (t.getStartLevel().getLevel() == 0
									&& t.getDestLevel().equals(parkour.getFinalLevel(false))) {
								Bukkit.getScheduler().runTaskLater(HubTweaks.getInstance(), () -> {
									player.addTiming(t);
								} , 60l);
							} else {
								player.addTiming(t);
							}
							player.removePendingTime(t);
						});

				// Create new timings.
				// Time between levels
				if (goal.getLevel().getLevel() < parkour.getFinalLevel(false).getLevel()) {
					player.addPendingTime(new ParkourTiming(null, goal.getLevel(),
							parkour.getLevel((short) (goal.getLevel().getLevel() + 1)), System.currentTimeMillis()));
				}
				// Overall Timing
				if (goal.getLevel().equals(ParkourLevel.START_LEVEL)) {
					player.setLastLevel(ParkourLevel.START_LEVEL);
					player.addPendingTime(new ParkourTiming(null, goal.getLevel(), parkour.getFinalLevel(false),
							System.currentTimeMillis()));
				}

				if (goal.getLevel().equals(parkour.getFinalLevel(false)) && !player.hasCheated()
						&& !player.getLastLevel().equals(parkour.getFinalLevel(false))) {
					Statistic.addToParkour(evt.getPlayer());
					if (!player.getLastLevel().equals(ParkourLevel.START_LEVEL)) {
						Bukkit.getOnlinePlayers().stream()
								.forEach(pl -> PlayerUtil.sendActionMessage(pl, ColorUtil.replaceColors(
									"&c&l%s &r&6 has reached the end of the parkour!", evt.getPlayer().getName())));
					}
				}

				// HARDCODED BONUSES
				if (goal.getLevel().equals(parkour.getFinalLevel(false)) && !player.hasCheated()
						&& player.getLastLevel().equals(ParkourLevel.START_LEVEL)) {
					ParkourLevel bonusLevel = parkour.getLevels().stream().filter(p -> p.getLevel() == 6).findFirst()
							.get();
					player.setHighestLevel(bonusLevel);
					for (int i = 0; i < 10; i++) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(HubTweaks.getInstance(), () -> {
							LocationUtil.firework(evt.getPlayer().getLocation(),
								FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.PURPLE)
										.withFade(Color.GREEN, Color.LIME, Color.YELLOW, Color.ORANGE, Color.RED)
										.build(),
								20L);
						} , 20 * (i + 5));
					}
					Bukkit.getOnlinePlayers().stream()
							.forEach(pl -> PlayerUtil.sendActionMessage(pl, ColorUtil.replaceColors(
								"&c&l%s &r&5has reached the end of the parkour!", evt.getPlayer().getName())));
				} else if (player.getHighestLevel().getLevel() < goal.getLevel().getLevel()) {
					player.setHighestLevel(goal.getLevel());
				}
				player.setLastLevel(goal.getLevel());

				// Give the player his boots if he has reached a level before.
				if (!player.getHighestLevel().equals(ParkourLevel.START_LEVEL)
						&& !goal.getLevel().equals(ParkourLevel.START_LEVEL)) {
					ItemStack boots = ItemUtil.getBuilder(Material.LEATHER_BOOTS).build();
					LeatherArmorMeta meta = (LeatherArmorMeta) boots.getItemMeta();
					meta.setColor(player.getHighestLevel().getReward().getColor());
					boots.setItemMeta(meta);
					Bukkit.getPlayer(player.getUuid()).getInventory().setBoots(boots);
				}
				player.save();

			}
		}
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent evt) {
		if (parkour.getParkourPlayer(evt.getPlayer()) == null) {
			loadParkourPlayer(evt.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerLogout(PlayerQuitEvent evt) {
		parkour.removePlayer(parkour.getParkourPlayer(evt.getPlayer()));
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent evt) {
		parkour.removePlayer(parkour.getParkourPlayer(evt.getPlayer()));
	}

	public ParkourLevel getLevel(short level) {
		return null;
	}

	public static void saveLevel(ParkourLevel level) {
		DM.insertQuery("INSERT INTO iMine_Statistics.parkour_level VALUES (%s,%b,%s)", level.getLevel(),
			level.isBonusLevel(), level.getReward().name());
	}

	public static void saveGoal(ParkourGoal goal) {
		DM.insertQuery("INSERT INTO iMine_Statistics.parkour_goal VALUES (%s,%s,%d,%d,%d)", goal.getLevel().getLevel(),
			goal.getTarget().getWorld().getUID(), goal.getTarget().getBlockX(), goal.getTarget().getBlockY(),
			goal.getTarget().getBlockZ());
	}

	public static List<ParkourLevel> loadLevels() {
		List<ParkourLevel> ret = new ArrayList<>();
		ResultSet rs = DM.selectQuery("SELECT * FROM iMine_Statistics.parkour_level WHERE id > 0");
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
		ResultSet rs = DM.selectQuery("SELECT * FROM iMine_Statistics.parkour_goal");
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

	public static void loadParkourPlayer(Player player) {
		ResultSet rs = DM.selectQuery("SELECT * FROM iMine_Statistics.parkour_player WHERE uuid LIKE '%s' LIMIT 1",
			player.getUniqueId().toString());
		ParkourPlayer parkourPlayer = null;
		try {
			while (rs.next()) {
				ResultSet timingSet = DM.selectQuery(
					"SELECT * FROM iMine_Statistics.parkour_timing WHERE uuid LIKE '%s'",
					player.getUniqueId().toString());
				List<ParkourTiming> timings = new ArrayList<>();
				while (timingSet.next()) {
					timings.add(new ParkourTiming(timingSet.getTimestamp("dateObtained"),
							parkour.getLevel((short) timingSet.getInt("origin")),
							parkour.getLevel((short) timingSet.getInt("destination")), timingSet.getLong("time")));
				}
				parkourPlayer = new ParkourPlayer(player.getUniqueId(), parkour.getLevel((short) rs.getInt("level")),
						timings);
			}
			if (parkourPlayer == null) {
				parkourPlayer = new ParkourPlayer(player.getUniqueId(), parkour.getLevel((short) 0), new ArrayList<>());
				DM.insertQuery("INSERT INTO iMine_Statistics.parkour_player VALUES('%s',0)", player.getUniqueId());
			}
			parkourPlayer.save();
			parkour.addPlayer(parkourPlayer);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Parkour getParkourInstance() {
		return parkour;
	}
}
