package nl.imine.hubtweaks.login;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import nl.imine.hubtweaks.HubTweaks;

public class HideLogs implements Listener {

    public static void init() {
        Bukkit.getPluginManager().registerEvents(new HideLogs(), HubTweaks.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerJoinEvent pje) {
        pje.setJoinMessage(null);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent pqe) {
        pqe.setQuitMessage(null);
    }
}
