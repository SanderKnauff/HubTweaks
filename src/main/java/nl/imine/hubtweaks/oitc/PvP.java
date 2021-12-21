package nl.imine.hubtweaks.oitc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import nl.imine.hubtweaks.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class PvP {
	private final PvPSpawnRepository pvpSpawnRepository;
	private final Map<UUID, Integer> streak;

	public static final Location[] BOX = new Location[]{new Location(Bukkit.getWorlds().get(0), -14, 14, -449), new Location(Bukkit.getWorlds().get(0), 43, 70, -399)};

	public PvP(PvPSpawnRepository pvpSpawnRepository) {
		this.pvpSpawnRepository = pvpSpawnRepository;
		this.streak = new HashMap<>();
	}

	public void init(Plugin plugin) {
		pvpSpawnRepository.loadAll();
		Bukkit.getServer().getPluginManager().registerEvents(new PvPListener(this), plugin);
	}

	public boolean isPlayerInArena(Player player) {
		return LocationUtil.isInBox(player.getLocation(), BOX[0], BOX[1]);
	}

	public void addPlayerToArena(Player player) {
		streak.put(player.getUniqueId(), 0);
		player.closeInventory();
		this.addGear(player);
		player.setFireTicks(0);
		player.setFallDistance(0);
		player.setVelocity(new Vector());
		player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		player.getActivePotionEffects().clear();
	}

	public void removePlayerFromArena(Player player) {
		if (isPlayerInArena(player)) {
			streak.put(player.getUniqueId(), 0);
			player.getInventory().clear();
		}
	}

	public Location[] getCorners() {
		return BOX;
	}

	public Location getRandomSpawn() {
		final List<Location> all = new ArrayList<>(pvpSpawnRepository.getAll());
		return all.get(ThreadLocalRandom.current().nextInt(all.size()));
	}

	public Collection<Location> getSpawnList() {
		return pvpSpawnRepository.getAll();
	}

	public void addGear(Player p) {
		p.getInventory().clear();
		ItemStack sword = new ItemStack(Material.IRON_SWORD, 1);
		ItemStack bow = new ItemStack(Material.BOW, 1);
		ItemStack arrow = new ItemStack(Material.ARROW, 1);
		p.getInventory().setItem(0, sword);
		p.getInventory().setItem(1, bow);
		p.getInventory().setItem(2, arrow);
	}

	public Integer getStreak(UUID uniqueId) {
		return streak.get(uniqueId);
	}

	public void addKillToStreak(UUID uniqueId) {
		streak.put(uniqueId, streak.get(uniqueId) + 1);
	}
}
