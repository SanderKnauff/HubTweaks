package nl.imine.hubtweaks.kotl;

import nl.imine.hubtweaks.HubTweaksPlugin;
import nl.imine.hubtweaks.util.ColorUtil;
import nl.imine.hubtweaks.util.LocationUtil;
import nl.imine.hubtweaks.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public record KotlListener(Kotl kotl) implements Listener {

	public static void init(Kotl kotl) {
		HubTweaksPlugin.getInstance().getServer().getPluginManager().registerEvents(new KotlListener(kotl),
			HubTweaksPlugin.getInstance());
	}

	@EventHandler
	public void onSwitchItem(PlayerSwapHandItemsEvent evt) {
		if (evt.getPlayer().getGameMode() == GameMode.ADVENTURE
			&& LocationUtil.isInBox(evt.getPlayer().getLocation(), Kotl.BOX[0], Kotl.BOX[1])) {
			evt.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if ((event.getItemDrop().getItemStack() != null) && (event.getItemDrop().getItemStack().getType() != null)
			&& ((event.getItemDrop().getItemStack().getType().equals(Material.GOLDEN_HELMET))
				|| (event.getItemDrop().getItemStack().getType().equals(Material.GOLDEN_CARROT)))) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerPickup(EntityPickupItemEvent event) {
		ItemStack is = event.getItem().getItemStack();
		if (is.getType().equals(Material.GOLDEN_HELMET) || is.getType().equals(Material.GOLDEN_CARROT)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.getDrops().removeIf(is ->
			is.getType().equals(Material.GOLDEN_HELMET) || is.getType().equals(Material.GOLDEN_CARROT)
		);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent evt) {
		Player player = evt.getPlayer();
		if (evt.getAction().equals(Action.PHYSICAL) && evt.getClickedBlock().getLocation().equals(kotl.getPlateLoc())) {
			if (kotl.getKing() == null || !kotl.getKing().isOnline()) {
				kotl.setKing(player);
				if (!kotl.getKing().equals(kotl.getOldKing())) {
					Bukkit.getOnlinePlayers().forEach(pl -> PlayerUtil.sendActionMessage(pl,
						ColorUtil.replaceColors("&6&l%s is the new queen!".formatted(player.getDisplayName()))));
				}
			}
			evt.setCancelled(false);
		}
	}

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		if (!event.getPlayer().equals(kotl.getKing())) {
			return;
		}

		if (event.getTo() != null && event.getTo().distanceSquared(kotl.getPlateLoc()) > 2) {
			kotl.setKing(null);
			kotl.removeEntropiaWand(event.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		if (kotl.getKing() != null) {
			if (kotl.getKing().equals(player)) {
				if (event.getTo().distanceSquared(kotl.getPlateLoc()) > 2) {
					kotl.setKing(null);
					kotl.removeEntropiaWand(event.getPlayer());
				}
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if ((kotl.getKing() != null) && (event.getPlayer().equals(kotl.getKing()))) {
			kotl.setKing(null);
			kotl.removeEntropiaWand(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent evt) {
		Entity e = evt.getEntity();
		if (LocationUtil.isInBox(e.getLocation(), Kotl.BOX[0], Kotl.BOX[1])) {
			if (e.getVehicle() != null) {
				e.getVehicle().eject();
			}
			evt.setDamage(1D);
			evt.setCancelled(false);
		}

		if (((evt.getDamager() instanceof Player)) && ((e instanceof Player))) {
			Player damager = (Player) evt.getDamager();
			if ((damager.getInventory().getItemInMainHand().getType() != null)
				&& (damager.getInventory().getItemInMainHand().getType().equals(Material.GOLDEN_CARROT))) {
				if (Kotl.getInstance().getKing() != null) {
					if (Kotl.getInstance().getKing().equals(damager)
						&& LocationUtil.isInBox(e.getLocation(), Kotl.BOX[0], Kotl.BOX[1])) {
						LocationUtil.firework(e.getLocation(),
							FireworkEffect.builder().withColor(Color.RED).withColor(Color.BLUE).withColor(Color.GREEN)
								.withColor(Color.YELLOW).with(FireworkEffect.Type.BALL_LARGE).build(),
							5L);
					} else {
						Kotl.getInstance().removeEntropiaWand(damager);
						damager.setHealth(0);
						damager.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_HUGE, damager.getLocation(), 1);
					}
				} else {
					Kotl.getInstance().removeEntropiaWand(damager);
					damager.setHealth(0);
					damager.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_HUGE, damager.getLocation(), 1);
				}
			}
		}
	}
}
