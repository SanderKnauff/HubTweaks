package nl.imine.hubtweaks;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class PlayerDataManager {

	public static void removeAllPlayerData() {
		for (World w : Bukkit.getServer().getWorlds()) {
			String worldDir = w.getName();
			File file = new File(worldDir + File.separatorChar + "playerdata");
			for (File f : file.listFiles()) {
				if (Bukkit.getPlayer(UUID.fromString(f.getName().split(".")[0])) != null) {
					f.delete();
				}
			}
		}
	}

	public static void removePlayerData(Player player) {
		String UUID = player.getUniqueId().toString();
		for (World w : Bukkit.getServer().getWorlds()) {
			String worldDir = w.getName();
			File file = new File(worldDir + File.separatorChar + "playerdata" + File.separatorChar + UUID + ".dat");
			file.delete();
		}
	}
}
