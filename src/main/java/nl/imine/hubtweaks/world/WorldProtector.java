package nl.imine.hubtweaks.world;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import nl.imine.api.util.LocationUtil;
import nl.imine.hubtweaks.HubTweaks;
import nl.imine.hubtweaks.kotl.Kotl;
import nl.imine.hubtweaks.pvp.PvP;

public class WorldProtector implements Listener {

    public static void init() {
        new WorldProtector();
    }

    public WorldProtector() {
        Bukkit.getPluginManager().registerEvents(this, HubTweaks.getInstance());
        Bukkit.getScheduler().scheduleSyncRepeatingTask(HubTweaks.getInstance(), () -> {
            Bukkit.getOnlinePlayers().stream().forEach(pl -> pl.setSaturation(20F));
            Bukkit.getOnlinePlayers().stream()
                    .filter(pl -> !LocationUtil.isInBox(pl.getLocation(), PvP.BOX[0], PvP.BOX[1]))
                    .filter(pl -> !pl.isDead()).forEach(pl -> pl.setHealth(20D));
            ;
        } , 20L, 20L);
    }

    private boolean isBlockedBlock(Block bl) {
        if (bl == null) {
            return false;
        }
        switch (bl.getType()) {
        case WOOD_BUTTON:
        case STONE_BUTTON:
        case WOOD_PLATE:
        case STONE_PLATE:
        case GOLD_PLATE:
        case IRON_PLATE:
            return false;
        default:
            return true;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClickEntity(PlayerInteractAtEntityEvent piaee) {
        if (piaee.getPlayer().getGameMode() == GameMode.ADVENTURE) {
            piaee.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityHurt(EntityDamageByEntityEvent edbee) {
        if (edbee.getDamager() instanceof Player && ((Player) edbee.getDamager()).getGameMode() == GameMode.ADVENTURE) {
            edbee.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockChange(EntityChangeBlockEvent ecbe) {
        ecbe.setCancelled(isBlockedBlock(ecbe.getBlock()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onHangBreak(HangingBreakByEntityEvent hbbee) {
        if (hbbee.getRemover() instanceof Player && ((Player) hbbee.getRemover()).getGameMode() == GameMode.ADVENTURE
                || hbbee instanceof Projectile) {
            hbbee.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageEvent ede) {
        if (ede.getEntity() instanceof Player) {
            switch (ede.getCause()) {
            case FIRE:
            case FIRE_TICK:
            case LAVA:
                ede.getEntity().setFireTicks(0);
            case FALL:
            case CONTACT:
            case BLOCK_EXPLOSION:
            case DROWNING:
            case ENTITY_EXPLOSION:
            case FALLING_BLOCK:
            case SUFFOCATION:
                ede.setCancelled(true);
                break;
            default:
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityExplodeEvent eee) {
        eee.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent edbee) {
        if (edbee.getEntity() instanceof Player) {
            if ((LocationUtil.isInBox(edbee.getEntity().getLocation(), Kotl.BOX[0], Kotl.BOX[1]))) {
                edbee.setDamage(0D);
                return;
            }
            edbee.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent pie) {
        if (pie.getPlayer().getGameMode() == GameMode.ADVENTURE) {
            pie.setCancelled(isBlockedBlock(pie.getClickedBlock()));
        }
    }

    @EventHandler
    public void onBlock(BlockPlaceEvent bpe) {
        if (bpe.getPlayer().getGameMode() == GameMode.ADVENTURE) {
            bpe.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlock(BlockBreakEvent bbe) {
        if (bbe.getPlayer().getGameMode() == GameMode.ADVENTURE) {
            bbe.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent wce) {
        wce.setCancelled(true);
    }
}
