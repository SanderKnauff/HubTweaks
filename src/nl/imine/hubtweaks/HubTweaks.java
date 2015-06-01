package nl.imine.hubtweaks;

import nl.imine.hubtweaks.kotl.Kotl;
import nl.imine.hubtweaks.parkour.Parkour;
import nl.imine.hubtweaks.pvp.PvP;
import nl.imine.hubtweaks.warps.QuickWarp;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class HubTweaks extends JavaPlugin {

    private static HubTweaks plugin;
    
    @Override
    public void onEnable() {
        plugin = this;
        EventListener.init();
        PvP.init();
        Kotl.init();
        QuickWarp.init();
        Parkour.init();
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        getCommand("createQuickWarp").setExecutor(new CommandHandler(this));
        getCommand("ConvertRuleBook").setExecutor(new CommandHandler(this));
        getCommand("ReloadTracks").setExecutor(new CommandHandler(this));
        getCommand("HubTweaks").setExecutor(new CommandHandler(this));
        getCommand("kotl").setExecutor(new CommandHandler(this));
        PlayerDataManager.RemoveAllPlayerData();
        this.getConfig().addDefault("WarpItems", "[]");
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }

    @Override
    public void onDisable() {
        PlayerDataManager.RemoveAllPlayerData();
        plugin = null;
    }

    public static Plugin getInstance() {
        return plugin;
    }
}
