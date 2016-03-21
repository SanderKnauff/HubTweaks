package nl.imine.hubtweaks.pvp;

import nl.imine.api.util.LocationUtil;
import nl.imine.hubtweaks.HubTweaks;
import nl.imine.hubtweaks.Statistic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PvPListener implements Listener {

	public static void init() {
		HubTweaks.getInstance().getServer().getPluginManager().registerEvents(new PvPListener(),
			HubTweaks.getInstance());
	}

	@EventHandler
	public void onPlayerJoinArena(PvPJoinEvent evt) {
		Player player = evt.getPlayer();
		if (!PvP.getSpawnList().isEmpty()) {
			if (player.getVehicle() != null) {
				player.leaveVehicle();
			}
			if (player.getPassenger() != null) {
				player.getPassenger().leaveVehicle();
			}
			PvP.addPlayerToArena(player);
		} else {
			player.sendMessage(ChatColor.DARK_RED + "ERROR: Warp aborted due to no avalible spawns.");
		}
	}

	@EventHandler
	public void onPvPDamage(EntityDamageByEntityEvent evt) {
		if (evt.getEntity() instanceof Player) {
			Player player = (Player) evt.getEntity();
			if (PvP.isPlayerInArena(player)) {
				if (evt.getDamager() instanceof Player) {
					if (PvP.isPlayerInArena((Player) evt.getDamager())) {
						if (evt.getDamage() > 0) {
							for (int i = 0; i < 3; i++) {
								Location loc = new Location(evt.getEntity().getLocation().getWorld(),
										evt.getEntity().getLocation().getX(),
										evt.getEntity().getLocation().getY() + (i * 0.3D),
										evt.getEntity().getLocation().getZ());
								HubTweaks.getInstance().getServer()
										.getWorld(evt.getDamager().getLocation().getWorld().getName())
										.playEffect(loc, Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
							}
							evt.setCancelled(false);
						}
					}
				}
				if (evt.getEntity() instanceof Player && evt.getDamager().getType().equals(EntityType.ARROW)) {
					Arrow arrow = (Arrow) evt.getDamager();
					if (arrow.getShooter() instanceof Player) {
						Player attacker = (Player) arrow.getShooter();
						if (PvP.isPlayerInArena(player) && PvP.isPlayerInArena(attacker) && attacker != player) {
							player.damage(player.getHealth(), attacker);
							player.setHealth(0D);
							arrow.remove();
						}
					}
					evt.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent evt) {
		if (evt.getEntity().getKiller() instanceof Player && evt.getEntity() instanceof Player) {
			Player player = (Player) evt.getEntity();
			Player killer = (Player) evt.getEntity().getKiller();
			if (PvP.isPlayerInArena(player) && PvP.isPlayerInArena(killer)
					&& killer.getInventory().getItemInMainHand().getType() != null) {
				PvP.removePlayerFromArena(player);
				if (killer.getInventory().all(Material.ARROW).isEmpty()) {
					killer.getInventory().addItem(new ItemStack(Material.ARROW, 1));
				}
				player.sendMessage(ChatColor.WHITE + "You have been killed by: '" + ChatColor.GRAY + killer.getName()
						+ ChatColor.WHITE + "'");
				killer.sendMessage(
					ChatColor.WHITE + "You killed: '" + ChatColor.GRAY + player.getName() + ChatColor.WHITE + "'");
			}
			Statistic.addToKill(killer);
		}
		evt.getDrops().clear();
		evt.setDroppedExp(0);
		evt.setDeathMessage(null);
	}

	@EventHandler
	public void onProjectileHit(final ProjectileHitEvent evt) {
		if (evt.getEntity().getShooter() instanceof Player) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(HubTweaks.getInstance(), () -> {
				evt.getEntity().remove();
			} , 100L);
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent evt) {
		if (LocationUtil.isInBox(PvP.getCorners()[0], PvP.getCorners()[1], evt.getFrom())
				&& !LocationUtil.isInBox(PvP.getCorners()[0], PvP.getCorners()[1], evt.getTo())) {
			evt.getPlayer().setHealth(0D);
		} else if (LocationUtil.isInBox(PvP.getCorners()[0], PvP.getCorners()[1], evt.getTo())
				&& !LocationUtil.isInBox(PvP.getCorners()[0], PvP.getCorners()[1], evt.getFrom())) {
			PvP.addPlayerToArena(evt.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerItemDrop(PlayerDropItemEvent evt) {
		PvP.isPlayerInArena(evt.getPlayer());
	}

	@EventHandler
	public void onPlayerItemPickup(PlayerPickupItemEvent evt) {
		if (PvP.isPlayerInArena(evt.getPlayer())) {
			evt.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerInventoryChange(InventoryClickEvent evt) {
		PvP.isPlayerInArena((Player) evt.getWhoClicked());
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent evt) {
		if (PvP.isPlayerInArena(evt.getPlayer())) {
			PvP.removePlayerFromArena(evt.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent evt) {
		if (PvP.isPlayerInArena(evt.getPlayer())) {
			PvP.removePlayerFromArena(evt.getPlayer());
		}
	}
}
