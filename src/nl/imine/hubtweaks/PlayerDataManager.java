package nl.imine.hubtweaks;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class PlayerDataManager {

	public static void RemoveAllPlayerData() {
		for (World w : Bukkit.getServer().getWorlds()) {
			String worldDir = w.getName();
			File file = new File(worldDir + File.separatorChar + "playerdata");
			for (File f : file.listFiles()) {
				System.out.println("Deleting: " + f.getName());
				f.delete();
			}
		}
	}

	public static void RemovePlayerData(Player player) {
		String UUID = player.getUniqueId().toString();
		for (World w : Bukkit.getServer().getWorlds()) {
			String worldDir = w.getName();
			File file = new File(worldDir + File.separatorChar + "playerdata" + File.separatorChar + UUID + ".dat");
			if (file.exists()) {
				file.delete();
				System.out.println("Deleting: " + file.getName());
			}
		}
	}
}
