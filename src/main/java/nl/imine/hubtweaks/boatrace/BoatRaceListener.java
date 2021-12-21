package nl.imine.hubtweaks.boatrace;

import org.bukkit.Location;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

public class BoatRaceListener implements Listener {

    @EventHandler
    public void onVehicleLeave(final VehicleExitEvent event) {
        if (!(event.getExited() instanceof Boat)) {
            return;
        }

        event.getExited().remove();
    }

    @EventHandler
    public void onButtonPress(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }

        final var loc = event.getClickedBlock().getLocation();
        if (loc.getBlockX() == 39 && loc.getBlockY() == 4 && loc.getBlockZ() == -526 && loc.getWorld() != null) {
            var boat = loc.getWorld().spawnEntity(new Location(loc.getWorld(), 42, 4, -529.5), EntityType.BOAT);
            boat.addPassenger(event.getPlayer());
        }
    }

}
