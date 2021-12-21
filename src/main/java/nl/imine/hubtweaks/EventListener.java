package nl.imine.hubtweaks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import static java.util.function.Predicate.not;

public class EventListener implements Listener, Runnable {

	public static void init(HubTweaksPlugin plugin) {
		EventListener el = new EventListener();
		plugin.getServer().getPluginManager().registerEvents(el, HubTweaksPlugin.getInstance());
		Bukkit.getScheduler().scheduleSyncRepeatingTask(HubTweaksPlugin.getInstance(), el, 1L, 1L);
	}

	public void run() {
		Bukkit.getWorlds().stream()
			.flatMap(world -> world.getEntities().stream())
			.filter(LivingEntity.class::isInstance)
			.filter(not(Boat.class::isInstance))
			.filter(entity -> entity.getLocation().getY() <= 0)
			.forEach(entity -> {
				entity.setFallDistance(0f);
				Location spawn = entity.getWorld().getSpawnLocation().getBlock().getLocation();
				spawn.add(0.5D, 0.1D, 0.5D);
				spawn.setDirection(entity.getLocation().getDirection());
				entity.teleport(spawn, PlayerTeleportEvent.TeleportCause.END_PORTAL);
				if (entity instanceof Player player) {
					playerRespawn(player);
				}
			});
	}

	@EventHandler
	public void onInventoryClick(final InventoryClickEvent evt) {
		if (evt.getWhoClicked().getGameMode() == GameMode.ADVENTURE) {
			evt.setCancelled(true);
		}
	}

	@EventHandler
	public void onPvP(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			return;
		}

		if (!(event.getDamager() instanceof Player player)) {
			return;
		}

		if (!player.getGameMode().equals(GameMode.ADVENTURE)) {
			return;
		}

		event.setDamage(0);
	}

	@EventHandler
	public void onPlayerDisconnect(final PlayerQuitEvent event) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(HubTweaksPlugin.getInstance(),
			() -> PlayerDataManager.removePlayerData(event.getPlayer()), 10
		);
	}

	@EventHandler
	public void onPlayerItemDrop(final PlayerDropItemEvent event) {
		if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent pje) {
		this.playerRespawn(pje.getPlayer());
	}

	@EventHandler
	public void onPlayerRespawn(final PlayerRespawnEvent pre) {
		this.playerRespawn(pre.getPlayer());
	}

	private void playerRespawn(final Player player) {
		player.setGameMode(GameMode.ADVENTURE);
		player.teleport(HubTweaksPlugin.getMainWorld().getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
		player.getInventory().setArmorContents(new ItemStack[player.getInventory().getArmorContents().length]);
		final ItemStack item = new ItemStack(Material.COMPASS, 1);
		Optional.ofNullable(item.getItemMeta()).ifPresent(metadata -> {
			List<String> list = new ArrayList<>();
			list.add(ChatColor.GOLD + "Right click to open Warp Menu");
			metadata.setLore(list);
			metadata.setDisplayName(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Teleporter");
			item.setItemMeta(metadata);
		});
		player.closeInventory();
		Bukkit.getScheduler().scheduleSyncDelayedTask(HubTweaksPlugin.getInstance(), () -> {
			player.getInventory().clear();
			player.getInventory().addItem(item);
		}, 10L);
	}
}
