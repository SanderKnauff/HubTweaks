package nl.imine.hubtweaks.ride;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import nl.imine.hubtweaks.HubTweaks;
import nl.imine.hubtweaks.Statistic;
import nl.imine.hubtweaks.parkour.Parkour;

public class EntityRide implements Listener {

    private final Map<Entity, Integer> timeOut = new HashMap<>();

    public EntityRide() {
        Bukkit.getPluginManager().registerEvents(this, HubTweaks.getInstance());
    }

    public static void init() {
        new EntityRide();
    }

    @EventHandler
    public void onPlayerLeft(final PlayerQuitEvent pqe) {
        Player pl = pqe.getPlayer();
        if(pl.getVehicle() != null){
            pl.eject();
            pl.getVehicle().eject();
        }
    }
    
    @EventHandler
    public void onPlayerSneak(final PlayerToggleSneakEvent ptse) {
        final Player pl = ptse.getPlayer();
        if (ptse.isSneaking()) {
            addToTimeout(pl);
            pl.eject();
        }
    }

    public void addToTimeout(final Entity e) {
        if (timeOut.containsKey(e)) {
            Bukkit.getScheduler().cancelTask(timeOut.get(e));
        }
        int sch = Bukkit.getScheduler().scheduleSyncDelayedTask(HubTweaks.getInstance(), new Runnable() {
            public void run() {
                timeOut.remove(e);
            }
        }, 100L);
        timeOut.put(e, sch);
    }

    @EventHandler
    public void onPlayerInterct(final PlayerInteractEntityEvent piee) {
        final Player pl = piee.getPlayer();
        final Entity e = piee.getRightClicked();
        if (timeOut.containsKey(e) || e instanceof Villager || e.getLocation().getY() > 64) {
            return;
        }
        if (e instanceof LivingEntity && pl.hasPermission("iMine.hub.ride")
                && (!(e instanceof Player) || pl.hasPermission("iMine.hub.ride.player"))) {
            Entity oldPassenger = e.getPassenger();
            if (oldPassenger != null) {
                if (oldPassenger instanceof Player) {
                    ((Player) oldPassenger).sendMessage("You get kicked off the "
                            + e.getType().toString().toLowerCase().replace("_", " ").replace("craft", ""));
                }
                oldPassenger.teleport(oldPassenger.getLocation().add(0D, -0.5D, 0D));
            }
            addToTimeout(e);
            e.eject();
            if (pl.getVehicle() == null) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(HubTweaks.getInstance(), new Runnable() {
                    public void run() {
                        e.setPassenger(pl);
                        Parkour.getInstance().getPlayer(pl).setTouchedPlate(true);
                    }
                }, 2L);
            }
            Statistic.addToRide(pl);
            if (e instanceof Player) {
                Statistic.addToRidden(((Player) e));
            }
        }
    }

}
