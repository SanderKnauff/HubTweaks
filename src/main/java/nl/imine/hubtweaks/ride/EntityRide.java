package nl.imine.hubtweaks.ride;

import nl.imine.hubtweaks.parkour.ParkourPlayerRepository;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import nl.imine.hubtweaks.oitc.PvP;
import org.bukkit.event.player.PlayerKickEvent;

public record EntityRide(PvP pvp, ParkourPlayerRepository parkourPlayerRepository) implements Listener {

	@EventHandler
	public void onPlayerLeft(final PlayerQuitEvent event) {
		Player pl = event.getPlayer();
		pl.eject();
		if (pl.isInsideVehicle()) {
			pl.leaveVehicle();
		}
	}

	@EventHandler
	public void onPlayerKick(final PlayerKickEvent event) {
		Player pl = event.getPlayer();
		pl.eject();
		if (pl.isInsideVehicle()) {
			pl.leaveVehicle();
		}
	}

	@EventHandler
	public void onPlayerSneak(final PlayerToggleSneakEvent event) {
		final Player pl = event.getPlayer();
		if (event.isSneaking()) {
			pl.eject();
		}
	}

	@EventHandler
	public void onPlayerHit(final EntityDamageByEntityEvent event) {
		if (!event.getDamager().getPassengers().isEmpty()) {
			event.getDamager().eject();
		}
	}

	@EventHandler
	public void onPlayerInteract(final PlayerInteractEntityEvent event) {
		final Player player = event.getPlayer();
		final Entity target = event.getRightClicked();
		if (target instanceof Villager || target.getLocation().getY() > 64) {
			return;
		}
		if (target instanceof LivingEntity && !pvp.isPlayerInArena(player) && player.getVehicle() == null) {
			for (Entity passenger : target.getPassengers()) {
				if (passenger instanceof Player) {
					passenger.sendMessage("You got kicked off " + target.getName());
				}
				passenger.teleport(passenger.getLocation().add(0D, 0.1D, 0D));
			}
			target.eject();
			if (player.getVehicle() == null) {
				Location l = player.getLocation();
				player.teleport(new Location(player.getWorld(), l.getX(), l.getY(), l.getZ(), target.getLocation().getYaw(), target.getLocation().getPitch()));
				target.addPassenger(player);
				parkourPlayerRepository.findOne(player.getUniqueId()).ifPresent(p -> p.setCheated(true));
			}
		}
	}
}
