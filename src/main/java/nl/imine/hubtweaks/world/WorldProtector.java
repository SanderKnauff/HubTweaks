package nl.imine.hubtweaks.world;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.PlayerInventory;

import nl.imine.hubtweaks.util.LocationUtil;
import nl.imine.hubtweaks.HubTweaksPlugin;
import nl.imine.hubtweaks.oitc.PvP;

public class WorldProtector implements Listener {

	public static void init() {
		new WorldProtector();
	}

	public WorldProtector() {
		Bukkit.getPluginManager().registerEvents(this, HubTweaksPlugin.getInstance());
		Bukkit.getScheduler().scheduleSyncRepeatingTask(HubTweaksPlugin.getInstance(), () -> {
			Bukkit.getOnlinePlayers().forEach(pl -> {
				pl.setFoodLevel(20);
				pl.setSaturation(1F);
			});
			Bukkit.getOnlinePlayers().stream()
					.filter(pl -> !LocationUtil.isInBox(pl.getLocation(), PvP.BOX[0], PvP.BOX[1]) && !pl.isDead())
					.forEach(pl -> pl.setHealth(20D));
		} , 20L, 20L);
	}

	private boolean isBlockedBlock(Block bl) {
		if (bl == null) {
			return false;
		}
		return switch (bl.getType()) {
			case OAK_BUTTON,
				STONE_BUTTON,
				OAK_PRESSURE_PLATE,
				STONE_PRESSURE_PLATE,
				LIGHT_WEIGHTED_PRESSURE_PLATE,
				HEAVY_WEIGHTED_PRESSURE_PLATE,
				JUKEBOX -> false;
			default -> true;
		};
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onClickEntity(PlayerInteractAtEntityEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onClickItemFrame(PlayerInteractEntityEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.ADVENTURE && event.getRightClicked() instanceof ItemFrame) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityHurt(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player) && event.getDamager() instanceof Player
				&& ((Player) event.getDamager()).getGameMode() == GameMode.ADVENTURE) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockChange(EntityChangeBlockEvent event) {
		event.setCancelled(isBlockedBlock(event.getBlock()));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onHangBreak(HangingBreakByEntityEvent event) {
		if (event.getRemover() instanceof Player player && player.getGameMode() == GameMode.ADVENTURE || event instanceof Projectile) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamageByEntity(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			switch (event.getCause()) {
				case FIRE, FIRE_TICK, LAVA -> event.getEntity().setFireTicks(0);
				case FALL, CONTACT, BLOCK_EXPLOSION, DROWNING, ENTITY_EXPLOSION, FALLING_BLOCK, SUFFOCATION ->
					event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamageByEntity(EntityExplodeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player player && player.getGameMode() == GameMode.ADVENTURE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.ADVENTURE && event.hasBlock()) {
			PlayerInventory pi = event.getPlayer().getInventory();
			if (pi.getItemInMainHand().getType() == Material.BOW || pi.getItemInOffHand().getType() == Material.BOW) {
				return;
			}
			event.setCancelled(isBlockedBlock(event.getClickedBlock()));
		}
	}

	@EventHandler
	public void onBlock(BlockPlaceEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlock(BlockBreakEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		event.setCancelled(true);
	}
}
