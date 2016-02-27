package nl.imine.hubtweaks.ride;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
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

import nl.imine.hubtweaks.HubTweaks;
import nl.imine.hubtweaks.Statistic;
import nl.imine.hubtweaks.parkour.Parkour;
import nl.imine.hubtweaks.pvp.PvP;

public class EntityRide implements Listener {

    private final Map<Entity, Integer> timeOut = new HashMap<>();
    private static EntityRide instance;

    public EntityRide() {
        Bukkit.getPluginManager().registerEvents(this, HubTweaks.getInstance());
    }

    public static void init() {
        instance = new EntityRide();
    }

    @EventHandler
    public void onPlayerLeft(final PlayerQuitEvent pqe) {
        Player pl = pqe.getPlayer();
        if (pl.getVehicle() != null) {
            pl.eject();
            pl.getVehicle().eject();
        }
    }

    @EventHandler
    public void onPlayerSneak(final PlayerToggleSneakEvent ptse) {
        final Player pl = ptse.getPlayer();
        if (ptse.isSneaking()) {
            addToTimeout(pl, 100L);
            pl.eject();
        }
    }

    public static void removeFromTimeout(final Entity e) {
        instance.timeOut.remove(e);
    }

    public static void addToTimeout(final Entity e, long delay) {
        if (instance.timeOut.containsKey(e) && instance.timeOut.get(e) > -1) {
            Bukkit.getScheduler().cancelTask(instance.timeOut.get(e));
            instance.timeOut.remove(e);
        }
        int sch = -1;
        if (delay > 0) {
            sch = Bukkit.getScheduler().scheduleSyncDelayedTask(HubTweaks.getInstance(), new Runnable() {
                public void run() {
                    instance.timeOut.remove(e);
                }
            }, delay);
        }
        instance.timeOut.put(e, sch);
    }

    @EventHandler
    public void onPlayerHit(final EntityDamageByEntityEvent edbee) {
        if (edbee.getDamager().getPassenger() != null) {
            edbee.getDamager().eject();
        }
        addToTimeout(edbee.getDamager(), 100L);
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEntityEvent piee) {
        final Player pl = piee.getPlayer();
        final Entity e = piee.getRightClicked();
        if (timeOut.containsKey(e) || e instanceof Villager || e.getLocation().getY() > 64) {
            return;
        }
        if (e instanceof LivingEntity && pl.hasPermission("iMine.hub.ride") && !PvP.isPlayerInArena(pl) && pl.getVehicle() == null
                && (!(e instanceof Player) || pl.hasPermission("iMine.hub.ride.player"))) {
            Entity oldPassenger = e.getPassenger();
            if (oldPassenger != null) {
                if (oldPassenger instanceof Player) {
                    ((Player) oldPassenger).sendMessage("You get kicked off the "
                            + e.getType().toString().toLowerCase().replace("_", " ").replace("craft", ""));
                }
                oldPassenger.teleport(oldPassenger.getLocation().add(0D, 0.1D, 0D));
            }
            addToTimeout(e, 100L);
            e.eject();
            if (pl.getVehicle() == null) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(HubTweaks.getInstance(), new Runnable() {
                    public void run() {
                        Location l = pl.getLocation();
                        pl.teleport(new Location(pl.getWorld(), l.getX(), l.getY(), l.getZ(), e.getLocation().getYaw(),
                                e.getLocation().getPitch()));
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
