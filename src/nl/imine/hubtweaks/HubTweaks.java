package nl.imine.hubtweaks;

import nl.imine.hubtweaks.kotl.Kotl;
import nl.imine.hubtweaks.parkour.Parkour;
import nl.imine.hubtweaks.pvp.PvP;
import nl.imine.hubtweaks.warps.QuickWarp;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class HubTweaks extends JavaPlugin {

    public static HubTweaks plugin;
    public static Plugin WorldGuard;
    
    private Kotl kotl;
    private QuickWarp qw;
    private Parkour parkour;

    @Override
    public void onEnable() {
        plugin = this;
        EventListener.init(this);
        PvP.init(this);
        this.kotl = new Kotl(this);
        qw = new QuickWarp(this);
        this.parkour = new Parkour(this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        //getServer().getPluginManager().registerEvents(this.EventListener, this);
        getCommand("createQuickWarp").setExecutor(new CommandHandler(this));
        getCommand("ToggleParkourCreation").setExecutor(new CommandHandler(this));
        getCommand("ConvertRuleBook").setExecutor(new CommandHandler(this));
        getCommand("ReloadTracks").setExecutor(new CommandHandler(this));
        getCommand("HubTweaks").setExecutor(new CommandHandler(this));
        getCommand("kotl").setExecutor(new CommandHandler(this));
        PlayerDataManager.RemoveAllPlayerData();
        this.getConfig().addDefault("WarpItems", "[]");
        this.getConfig().addDefault("ParkourLevels", "[]");
        this.getConfig().addDefault("PlayerData", "[]");
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
    
    public Kotl getKotl(){
        return this.kotl;
    }
    
    public QuickWarp getQuickWarp(){
        return this.qw;
    }
}
