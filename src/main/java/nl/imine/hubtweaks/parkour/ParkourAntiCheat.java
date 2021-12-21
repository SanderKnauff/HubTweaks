package nl.imine.hubtweaks.parkour;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class ParkourAntiCheat implements Listener {

    private final ParkourPlayerRepository parkourPlayerRepository;

    public ParkourAntiCheat(ParkourPlayerRepository parkourPlayerRepository) {
        this.parkourPlayerRepository = parkourPlayerRepository;
    }

    @EventHandler
	public void onPlayerGlide(EntityToggleGlideEvent evt) {
		if (evt.getEntity() instanceof Player player) {
            parkourPlayerRepository.findOne(player.getUniqueId()).ifPresent(p -> p.setCheated(true));
		}
	}

	@EventHandler
	public void onGameModeChange(PlayerGameModeChangeEvent evt) {
		if (!evt.getNewGameMode().equals(GameMode.ADVENTURE)) {
            parkourPlayerRepository.findOne(evt.getPlayer().getUniqueId()).ifPresent(p -> p.setCheated(true));

		}
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent evt) {
		if (evt.getTo() != null && evt.getTo().getBlockY() > 40) {
            parkourPlayerRepository.findOne(evt.getPlayer().getUniqueId()).ifPresent(p -> p.setCheated(true));
		}
	}

	@EventHandler
	public void onPlayerToggleFlight(PlayerToggleFlightEvent evt) {
        parkourPlayerRepository.findOne(evt.getPlayer().getUniqueId()).ifPresent(p -> p.setCheated(true));
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent evt) {
		if (!evt.getPlayer().getActivePotionEffects().isEmpty()) {
            parkourPlayerRepository.findOne(evt.getPlayer().getUniqueId()).ifPresent(p -> p.setCheated(true));
		}
	}
}
