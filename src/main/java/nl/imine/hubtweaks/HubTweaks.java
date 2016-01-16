package nl.imine.hubtweaks;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import nl.imine.hubtweaks.entity.Spawner;
import nl.imine.hubtweaks.kotl.Kotl;
import nl.imine.hubtweaks.login.HideLogs;
import nl.imine.hubtweaks.parkour.Parkour;
import nl.imine.hubtweaks.pvp.PvP;
import nl.imine.hubtweaks.ride.EntityRide;
import nl.imine.hubtweaks.warps.CompassWarp;
import nl.imine.hubtweaks.world.WorldProtector;

public class HubTweaks extends JavaPlugin {

    private static HubTweaks plugin;

    @Override
    public void onEnable() {
        plugin = this;
        HideLogs.init();
        EventListener.init();
        WorldProtector.init();
        PvP.init();
        Kotl.init();
        CompassWarp.init();
        Parkour.init();
        EntityRide.init();
        Spawner.init();
        Statistic.init();
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        getCommand("HubTweaks").setExecutor(new CommandHandler(this));
        getCommand("kotl").setExecutor(new CommandHandler(this));
        PlayerDataManager.RemoveAllPlayerData();
        this.getConfig().addDefault("WarpItems", "[]");
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
//        Tag t = TagAPI.createTag(new Location(Bukkit.getWorlds().get(0), 56.5, 34, -488.5));
//        t.addLine(ChatColor.GOLD + "Dit is " + ChatColor.RED + "Fred");
//        t.addLine(ChatColor.RED + "100" + ChatColor.GOLD + "punten");
//        t.addLine(ChatColor.RED + "Fred" + ChatColor.GOLD + " is een faggot. Net als " + ChatColor.RED + "Beauseant" + ChatColor.GOLD + ".");
    }

    @Override
    public void onDisable() {
        PlayerDataManager.RemoveAllPlayerData();
        plugin = null;
    }

    public static Plugin getInstance() {
        return plugin;
    }

    public static World getMainWorld() {
        return Bukkit.getWorlds().get(0);
    }
}
