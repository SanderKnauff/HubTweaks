package nl.imine.hubtweaks;

import nl.imine.hubtweaks.parkour.ParkourManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class HubTweaks extends JavaPlugin {

	private static HubTweaks plugin;

	@Override
	public void onEnable() {
		plugin = this;
		// HideLogs.init();
		// EventListener.init();
		// WorldProtector.init();
		// PvP.init();
		// Kotl.init();
		// CompassWarp.init();
		ParkourManager.init();
		// EntityRide.init();
		// Spawner.init();
		// AntiFly.init();
		Statistic.init();
		// Bukkit.getMessenger().registerOutgoingPluginChannel(plugin,
		// "BungeeCord");
		// getCommand("HubTweaks").setExecutor(new CommandHandler(this));
		// getCommand("kotl").setExecutor(new CommandHandler(this));
		// PlayerDataManager.RemoveAllPlayerData();
		// this.getConfig().addDefault("WarpItems", "[]");
		// this.getConfig().options().copyDefaults(true);
		// this.saveConfig();
		Bukkit.getWorlds().stream().forEach(w -> System.out.println(w.getName() + ": " + w.getUID()));
	}

	@Override
	public void onDisable() {
		// PlayerDataManager.RemoveAllPlayerData();
		plugin = null;
	}

	public static Plugin getInstance() {
		return plugin;
	}

	public static World getMainWorld() {
		return Bukkit.getWorlds().get(0);
	}
}
