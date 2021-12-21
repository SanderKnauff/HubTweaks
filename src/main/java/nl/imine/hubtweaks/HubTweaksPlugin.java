package nl.imine.hubtweaks;

import nl.imine.api.gui.GuiManager;
import nl.imine.hubtweaks.boatrace.BoatRaceListener;
import nl.imine.hubtweaks.buttongame.ButtonGame;
import nl.imine.hubtweaks.db.DatabaseManager;
import nl.imine.hubtweaks.oitc.AddPvPSpawnCommand;
import nl.imine.hubtweaks.oitc.PvPSpawnRepository;
import nl.imine.hubtweaks.parkour.ParkourGoalRepository;
import nl.imine.hubtweaks.parkour.ParkourLevelRepository;
import nl.imine.hubtweaks.parkour.ParkourPlayerRepository;
import nl.imine.hubtweaks.parkour.command.AddGoalCommand;
import nl.imine.hubtweaks.parkour.command.AddLevelCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import nl.imine.hubtweaks.kotl.Kotl;
import nl.imine.hubtweaks.oitc.PvP;
import nl.imine.hubtweaks.parkour.ParkourManager;
import nl.imine.hubtweaks.ride.EntityRide;
import nl.imine.hubtweaks.warps.CompassWarp;
import nl.imine.hubtweaks.world.WorldProtector;
import org.bukkit.plugin.java.annotation.command.Command;
import org.bukkit.plugin.java.annotation.dependency.Library;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.flywaydb.core.Flyway;

@Plugin(name = "HubTweaks", version = "1.0")
@Library("org.mariadb.jdbc:mariadb-java-client:2.7.4")
@Library("org.flywaydb:flyway-core:8.1.0")
@Library("com.zaxxer:HikariCP:5.0.0")
@Command(name = "hubtweaks")
@Command(name = "kotl")
@Command(name = "addpvpspawn")
@Command(name = "addgoal", usage = "/addgoal level x y z")
@Command(name = "addlevel", usage = "/addlevel level dyecolor")
@ApiVersion(ApiVersion.Target.v1_17)
public class HubTweaksPlugin extends JavaPlugin {

	private DatabaseManager databaseManager;

	private static HubTweaksPlugin plugin;

	@Override
	public void onEnable() {
		plugin = this;
		this.databaseManager = initializeDatabase();

		final var parkourLevelRepository = new ParkourLevelRepository(getLogger(), databaseManager);
		final var parkourPlayerRepository = new ParkourPlayerRepository(getLogger(), databaseManager, parkourLevelRepository);
		final var parkourGoalRepository = new ParkourGoalRepository(getLogger(), databaseManager, parkourLevelRepository);

		new ParkourManager(getLogger(), parkourLevelRepository, parkourPlayerRepository, parkourGoalRepository).init(this);
		GuiManager.init(this);
		EventListener.init(this);
		WorldProtector.init();

		final var pvpSpawnRepository = new PvPSpawnRepository(getLogger(), databaseManager);
		final PvP pvp = new PvP(pvpSpawnRepository);
		pvp.init(this);
		Kotl.init();
		new CompassWarp(pvp).init(this);
		Bukkit.getPluginManager().registerEvents(new EntityRide(pvp, parkourPlayerRepository), this);
		Bukkit.getPluginManager().registerEvents(new BoatRaceListener(), this);
		Bukkit.getPluginManager().registerEvents(new ButtonGame(), this);

		getCommand("kotl").setExecutor(new CommandHandler());
		getCommand("addgoal").setExecutor(new AddGoalCommand(parkourLevelRepository, parkourGoalRepository));
		getCommand("addlevel").setExecutor(new AddLevelCommand(parkourLevelRepository));
		getCommand("addpvpspawn").setExecutor(new AddPvPSpawnCommand(pvpSpawnRepository));

		PlayerDataManager.removeAllPlayerData();
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
	}

	@Override
	public void onDisable() {
		plugin = null;
	}

	public DatabaseManager initializeDatabase() {
		final String url = getConfig().getString("db.url");
		final String username = getConfig().getString("db.username");
		final String password = getConfig().getString("db.password");

		Flyway.configure(this.getClassLoader())
			.dataSource(url,username, password)
			.locations("db.migration")
			.load()
			.migrate();

		return new DatabaseManager(url, username, password);
	}

	public static HubTweaksPlugin getInstance() {
		return plugin;
	}

	public static World getMainWorld() {
		return Bukkit.getWorlds().get(0);
	}
}
