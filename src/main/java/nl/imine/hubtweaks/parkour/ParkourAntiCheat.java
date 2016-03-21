package nl.imine.hubtweaks.parkour;

import nl.imine.api.event.FlightDetectEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.Material;

public class ParkourAntiCheat implements Listener {

	@EventHandler
	public void onFlyDetect(FlightDetectEvent evt) {
		ParkourPlayer player = ParkourManager.getParkourInstance().getParkourPlayer(evt.getPlayer());
		player.setCheated(true);
		player.setLastLevel(ParkourManager.getParkourInstance().getLevels().stream().filter(l -> !l.isBonusLevel())
				.sorted((ParkourLevel p1, ParkourLevel p2) -> p2.getLevel() - p1.getLevel()).findFirst().get());
		System.out.println(Material.FROSTED_ICE);
	}

	@EventHandler
	public void onPlayerGlide(EntityToggleGlideEvent evt) {
		if (evt.getEntity() instanceof Player) {
			ParkourPlayer player = ParkourManager.getParkourInstance().getParkourPlayer((Player) evt.getEntity());
			player.setCheated(true);
			player.setLastLevel(ParkourManager.getParkourInstance().getLevels().stream().filter(l -> !l.isBonusLevel())
					.sorted((ParkourLevel p1, ParkourLevel p2) -> p2.getLevel() - p1.getLevel()).findFirst().get());
		}
	}

	@EventHandler
	public void onGameModeChange(PlayerGameModeChangeEvent evt) {
		if (!evt.getNewGameMode().equals(GameMode.ADVENTURE)) {
			ParkourPlayer player = ParkourManager.getParkourInstance().getParkourPlayer(evt.getPlayer());
			player.setCheated(true);
			player.setLastLevel(ParkourManager.getParkourInstance().getLevels().stream().filter(l -> !l.isBonusLevel())
					.sorted((ParkourLevel p1, ParkourLevel p2) -> p2.getLevel() - p1.getLevel()).findFirst().get());
		}
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent evt) {
		if (evt.getTo().getBlockY() > 40) {
			ParkourPlayer player = ParkourManager.getParkourInstance().getParkourPlayer(evt.getPlayer());
			player.setCheated(true);
			player.setLastLevel(ParkourManager.getParkourInstance().getLevels().stream().filter(l -> !l.isBonusLevel())
					.sorted((ParkourLevel p1, ParkourLevel p2) -> p2.getLevel() - p1.getLevel()).findFirst().get());
		}
	}

	@EventHandler
	public void onPlayerToggleFlight(PlayerToggleFlightEvent evt) {
		ParkourPlayer player = ParkourManager.getParkourInstance().getParkourPlayer(evt.getPlayer());
		player.setCheated(true);
		player.setLastLevel(ParkourManager.getParkourInstance().getLevels().stream().filter(l -> !l.isBonusLevel())
				.sorted((ParkourLevel p1, ParkourLevel p2) -> p2.getLevel() - p1.getLevel()).findFirst().get());
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent evt) {
		ParkourManager.getParkourInstance().getParkourPlayer(evt.getEntity()).resetPlayer();
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent evt) {
		if (!evt.getPlayer().getActivePotionEffects().isEmpty()) {
			ParkourPlayer player = ParkourManager.getParkourInstance().getParkourPlayer(evt.getPlayer());
			player.setCheated(true);
			player.setLastLevel(ParkourManager.getParkourInstance().getLevels().stream().filter(l -> !l.isBonusLevel())
					.sorted((ParkourLevel p1, ParkourLevel p2) -> p2.getLevel() - p1.getLevel()).findFirst().get());
		}
	}
}
