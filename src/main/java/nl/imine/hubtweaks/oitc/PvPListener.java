package nl.imine.hubtweaks.oitc;

import nl.imine.hubtweaks.util.LocationUtil;
import nl.imine.hubtweaks.HubTweaksPlugin;
import nl.imine.hubtweaks.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public record PvPListener(PvP pvp) implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent evt) {
		if (evt.isCancelled()) {
			return;
		}

		if (pvp.getSpawnList().contains(evt.getTo())) {
			pvp.addPlayerToArena(evt.getPlayer());
		} else {
			if (LocationUtil.isInBox(evt.getFrom(), pvp.getCorners()[0], pvp.getCorners()[1])
					&& !LocationUtil.isInBox(evt.getTo(), pvp.getCorners()[0], pvp.getCorners()[1])) {
				evt.getPlayer().setHealth(0D);
			}
		}
	}

	@EventHandler
	public void onPvPDamage(EntityDamageByEntityEvent evt) {
		if (!(evt.getEntity() instanceof Player player)) {
			return;
		}

		if (!pvp.isPlayerInArena(player)) {
			return;
		}

		if (evt.getDamager() instanceof Player) {
			handleMeleeDamage(evt);
			return;
		}

		if (evt.getEntity() instanceof Player && evt.getDamager() instanceof Arrow arrow) {
			handleArrowDamage(evt, player, arrow);
		}
	}

	private void handleArrowDamage(EntityDamageByEntityEvent evt, Player player, Arrow arrow) {
		evt.setCancelled(true);
		if (!(arrow.getShooter() instanceof Player attacker)) {
			return;
		}

		if (!pvp.isPlayerInArena(player) || !pvp.isPlayerInArena(attacker) || attacker == player) {
			return;
		}

		player.damage(player.getHealth(), attacker);
		arrow.remove();
	}

	private void handleMeleeDamage(EntityDamageByEntityEvent evt) {
		if (pvp.isPlayerInArena((Player) evt.getDamager())) {
			if (evt.getDamage() > 0) {
				for (int i = 0; i < 3; i++) {
					Location loc = new Location(evt.getEntity().getLocation().getWorld(),
							evt.getEntity().getLocation().getX(),
						evt.getEntity().getLocation().getY() + (i * 0.3D),
							evt.getEntity().getLocation().getZ());
					HubTweaksPlugin.getInstance().getServer()
							.getWorld(evt.getDamager().getLocation().getWorld().getName())
							.playEffect(loc, Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
				}
				evt.setCancelled(false);
			}
		}
	}

	@EventHandler
	public void handleProjectileItemFrameHit(EntityDamageByEntityEvent evt) {
		if (!evt.getEntity().getType().equals(EntityType.ITEM_FRAME)) {
			return;
		}
		if (!(evt.getDamager() instanceof Projectile)) {
			return;
		}
		evt.setCancelled(true);
	}

	@EventHandler
	public void onHangingBreak(HangingBreakByEntityEvent evt) {
		if (evt.getRemover() instanceof Player player) {
			if (pvp.isPlayerInArena(player)) {
				evt.setCancelled(true);
			}
		} else if (evt.getRemover() instanceof Arrow) {
			evt.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent evt) {
		evt.getDrops().clear();
		evt.setDroppedExp(0);
		evt.setDeathMessage(null);

		Player player = evt.getEntity();
		Player killer = evt.getEntity().getKiller();
		if (killer == null) {
			return;
		}


		if (pvp.isPlayerInArena(player) && pvp.isPlayerInArena(killer) && !killer.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
			pvp.removePlayerFromArena(player);
			if (killer.getInventory().all(Material.ARROW).isEmpty()) {
				killer.getInventory().addItem(new ItemStack(Material.ARROW, 1));
			}

			pvp.addKillToStreak(killer.getUniqueId());
			var players = Bukkit.getOnlinePlayers();
			final Integer currentStreak = pvp.getStreak(killer.getUniqueId());
			switch(currentStreak) {
				case 5 -> players.forEach(p -> p.sendTitle(" ", "%s%s %sis on a %sKILLING SPREE".formatted(ChatColor.RED, killer.getName().toUpperCase(), ChatColor.RESET, ChatColor.GREEN), 20, 60, 20));
				case 7 -> players.forEach(p -> p.sendTitle(" ", "%s%s %sis on a %sRAMPAGE".formatted(ChatColor.RED, killer.getName().toUpperCase(), ChatColor.RESET, ChatColor.BLUE), 20, 60, 20));
				case 9 -> players.forEach(p -> p.sendTitle(" ", "%s%s %sis %sDOMINATING".formatted(ChatColor.RED, killer.getName().toUpperCase(), ChatColor.RESET, ChatColor.LIGHT_PURPLE), 20, 60, 20));
				case 11 -> players.forEach(p -> p.sendTitle(" ", "%s%s %sis %sUNSTOPPABLE".formatted(ChatColor.RED, killer.getName().toUpperCase(), ChatColor.RESET, ChatColor.RED), 20, 60, 20));
				case 15 -> players.forEach(p -> p.sendTitle(" ", "%s%s %sis %sGODLIKE".formatted(ChatColor.RED, killer.getName().toUpperCase(), ChatColor.RESET, ChatColor.GOLD), 20, 60, 20));
			}

			PlayerUtil.sendActionMessage(player, "%sYou have been killed by: '%s%s%s'".formatted(ChatColor.GRAY, ChatColor.RED, killer.getName(), ChatColor.GRAY));
			PlayerUtil.sendActionMessage(killer, "%sYou killed: '%s%s%s' (Streak: %s%d%s)".formatted(ChatColor.GRAY, ChatColor.RED, player.getName(), ChatColor.GRAY, ChatColor.RED, currentStreak, ChatColor.GRAY));
		}
	}

	@EventHandler
	public void onProjectileHit(final ProjectileHitEvent evt) {
		if (evt.getEntity().getShooter() instanceof Player) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(HubTweaksPlugin.getInstance(), () -> {
				evt.getEntity().remove();
			} , 100L);
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent evt) {
		if (evt.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
			if (LocationUtil.isInBox(evt.getFrom(), pvp.getCorners()[0], pvp.getCorners()[1])
					&& !LocationUtil.isInBox(evt.getTo(), pvp.getCorners()[0], pvp.getCorners()[1])) {
				evt.getPlayer().setHealth(0D);
			} else if (LocationUtil.isInBox(evt.getTo(), pvp.getCorners()[0], pvp.getCorners()[1])
					&& !LocationUtil.isInBox(evt.getFrom(), pvp.getCorners()[0], pvp.getCorners()[1])) {
				pvp.addPlayerToArena(evt.getPlayer());
			}
		}
	}

	@EventHandler
	public void onPlayerItemDrop(PlayerDropItemEvent evt) {
		pvp.isPlayerInArena(evt.getPlayer());
	}

	@EventHandler
	public void onPlayerItemPickup(EntityPickupItemEvent evt) {
		if (evt.getEntity() instanceof Player player) {
			if (pvp.isPlayerInArena(player)) {
				evt.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerInventoryChange(InventoryClickEvent evt) {
		pvp.isPlayerInArena((Player) evt.getWhoClicked());
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent evt) {
		if (pvp.isPlayerInArena(evt.getPlayer())) {
			pvp.removePlayerFromArena(evt.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent evt) {
		if (pvp.isPlayerInArena(evt.getPlayer())) {
			pvp.removePlayerFromArena(evt.getPlayer());
		}
	}
}
