package nl.imine.hubtweaks.world;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import nl.imine.hubtweaks.HubTweaks;

public class WorldProtector implements Listener {

    public static void init() {
        new WorldProtector();
    }

    public WorldProtector() {
        Bukkit.getPluginManager().registerEvents(this, HubTweaks.getInstance());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent pie) {
        if (pie.getPlayer().getGameMode() == GameMode.ADVENTURE) {
            pie.setCancelled(true);
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
