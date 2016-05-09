package nl.imine.hubtweaks.oitc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import nl.imine.api.util.LocationUtil;
import nl.imine.hubtweaks.HubTweaks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class PvP {

	public static final Location[] BOX = new Location[]{new Location(Bukkit.getWorlds().get(0), -14, 14, -449),
			new Location(Bukkit.getWorlds().get(0), 43, 70, -399)};
	private static final List<Location> SPAWN_ARENA = new ArrayList<>();
	private static File pvpConfigFile = null;
	private static FileConfiguration pvpConfig = null;

	public static void init() {
		PvPListener.init();
		loadArena();
	}

	public static boolean isPlayerInArena(Player player) {
		return LocationUtil.isInBox(player.getLocation(), BOX[0], BOX[1]);
	}

	public static void addPlayerToArena(Player player) {
		player.closeInventory();
		PvP.addGear(player);
		player.setFireTicks(0);
		player.setFallDistance(0);
		player.setVelocity(new Vector());
		player.setHealth(player.getMaxHealth());
		player.getActivePotionEffects().clear();
		player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(100, 0));
	}

	public static void removePlayerFromArena(Player player) {
		if (isPlayerInArena(player)) {
			player.getInventory().clear();
		}
	}

	public static void loadArena() {
		pvpConfigFile = new File(HubTweaks.getInstance().getDataFolder().getPath() + File.separatorChar + "PvP.yml");
		if (!pvpConfigFile.exists()) {
			try {
				pvpConfigFile.createNewFile();
			} catch (IOException e) {
				System.err.println("ERROR: PVP FILE COULD NOT BE CREATED");
				return;
			}
		} else {
			System.out.println("PVPCONFIG Exists");
		}
		pvpConfig = YamlConfiguration.loadConfiguration(pvpConfigFile);
		if (pvpConfig.getConfigurationSection("Spawns") != null) {
			for (String key : pvpConfig.getConfigurationSection("Spawns").getKeys(false)) {
				World world = HubTweaks.getInstance().getServer()
						.getWorld(pvpConfig.getString("Spawns." + key + ".world"));
				double x = pvpConfig.getDouble("Spawns." + key + ".x");
				double y = pvpConfig.getDouble("Spawns." + key + ".y");
				double z = pvpConfig.getDouble("Spawns." + key + ".z");
				SPAWN_ARENA.add(new Location(world, x, y, z));
			}
		}
	}

	public static Location[] getCorners() {
		return BOX;
	}

	public static Location getRandomSpawn() {
		Random r = new Random();
		return SPAWN_ARENA.get(r.nextInt(SPAWN_ARENA.size()));
	}

	public static List<Location> getSpawnList() {
		return SPAWN_ARENA;
	}

	public static void addSpawn(Location spawn) {
		SPAWN_ARENA.add(spawn);
		System.out.println(SPAWN_ARENA.size());
		pvpConfig.set("Spawns." + SPAWN_ARENA.size() + ".world", spawn.getWorld().getName());
		pvpConfig.set("Spawns." + SPAWN_ARENA.size() + ".x", spawn.getX());
		pvpConfig.set("Spawns." + SPAWN_ARENA.size() + ".y", spawn.getY());
		pvpConfig.set("Spawns." + SPAWN_ARENA.size() + ".z", spawn.getZ());
		try {
			pvpConfig.save(pvpConfigFile);
		} catch (IOException e) {
			System.err.println("IOException loading hubtweaks PvP spawns: " + e.getMessage());
		}
	}

	public static void addGear(Player p) {
		p.getInventory().clear();
		ItemStack sword = new ItemStack(Material.IRON_SWORD, 1);
		ItemStack bow = new ItemStack(Material.BOW, 1);
		ItemStack arrow = new ItemStack(Material.ARROW, 1);
		p.getInventory().setItem(0, sword);
		p.getInventory().setItem(1, bow);
		p.getInventory().setItem(2, arrow);
	}
}
