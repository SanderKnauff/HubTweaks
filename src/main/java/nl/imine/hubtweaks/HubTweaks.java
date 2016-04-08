package nl.imine.hubtweaks;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import nl.imine.api.util.FlyUtil;
import nl.imine.hubtweaks.entity.Spawner;
import nl.imine.hubtweaks.kotl.Kotl;
import nl.imine.hubtweaks.login.HideLogs;
import nl.imine.hubtweaks.oitc.PvP;
import nl.imine.hubtweaks.parkour.ParkourManager;
import nl.imine.hubtweaks.ride.EntityRide;
import nl.imine.hubtweaks.warps.CompassWarp;
import nl.imine.hubtweaks.world.WorldProtector;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

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
		ParkourManager.init();
		EntityRide.init();
		Spawner.init();
		FlyUtil.init(this);
		Statistic.init();
		Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
		getCommand("HubTweaks").setExecutor(new CommandHandler(this));
		getCommand("kotl").setExecutor(new CommandHandler(this));
		PlayerDataManager.removeAllPlayerData();
		this.getConfig().addDefault("WarpItems", "[]");
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
	}

	@Override
	public void onDisable() {
		for (Player pl : Bukkit.getOnlinePlayers()) {
			if (PvP.isPlayerInArena(pl)) {
				PvP.removePlayerFromArena(pl);
				pl.teleport(getMainWorld().getSpawnLocation(), TeleportCause.PLUGIN);
				pl.setHealth(0D);
			}
		}
		plugin = null;
	}

	public static Plugin getInstance() {
		return plugin;
	}

	public static World getMainWorld() {
		return Bukkit.getWorlds().get(0);
	}
}
