package nl.imine.hubtweaks.pvp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import nl.imine.hubtweaks.HubTweaks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class PvP {

    private static List<Location> arenaSpawns = new ArrayList<Location>();
    private static File pvpConfigFile = null;
    private static FileConfiguration pvpConfig = null;

    private static List<Player> pvpList;

    public static void init() {
        System.out.println("HTPVP: Initialized PVP class");
        PvPListener.init();
        pvpList = new ArrayList<>();
        loadArena();
    }

    public PvP(Plugin plugin) {
    }

    public static boolean isPlayerInArena(Player player) {
        if (pvpList.contains(player)) {
            return true;
        }
        return false;
    }

    public static void addPlayerToArena(Player player) {
        pvpList.add(player);
    }

    public static void removePlayerFromArena(Player player) {
        if (pvpList.contains(player)) {
            pvpList.remove(player);
            player.getInventory().clear();
        }
    }

    public static void loadArena() {
        System.out.println("HTPVP: Loading Arena");
        pvpConfigFile = new File(HubTweaks.getInstance().getDataFolder().getPath() + File.separatorChar + "PvP.yml");
        if (!pvpConfigFile.exists()) {
            System.out.println("PVPCONFIG does not Exist, Creating it");
            try {
                pvpConfigFile.createNewFile();
            } catch (IOException e) {
                System.out.println("ERROR: PVP FILE COULD NOT BE CREATED");
                e.printStackTrace();
                return;
            }
        } else {
            System.out.println("PVPCONFIG Exists");
        }
        pvpConfig = YamlConfiguration.loadConfiguration(pvpConfigFile);
        if (pvpConfig.getConfigurationSection("Spawns") != null) {
            int spawncount = 0;
            for (String key : pvpConfig.getConfigurationSection("Spawns").getKeys(false)) {
                World world = HubTweaks.getInstance().getServer().getWorld(pvpConfig.getString("Spawns." + key + ".world"));
                double x = pvpConfig.getDouble("Spawns." + key + ".x");
                double y = pvpConfig.getDouble("Spawns." + key + ".y");
                double z = pvpConfig.getDouble("Spawns." + key + ".z");
                arenaSpawns.add(new Location(world, x, y, z));
                spawncount++;
            }
            System.out.println("Spawncount is: " + spawncount);
        }
    }

    public static Location getRandomSpawn() {
        Random r = new Random();
        return arenaSpawns.get(r.nextInt(arenaSpawns.size()));
    }

    public static List<Player> getPlayerList() {
        return pvpList;
    }

    public static List<Location> getSpawnList() {
        return arenaSpawns;
    }

    public static void addSpawn(Location spawn) {
        arenaSpawns.add(spawn);
        System.out.println(arenaSpawns.size());
        pvpConfig.set("Spawns." + arenaSpawns.size() + ".world", spawn.getWorld().getName());
        pvpConfig.set("Spawns." + arenaSpawns.size() + ".x", spawn.getX());
        pvpConfig.set("Spawns." + arenaSpawns.size() + ".y", spawn.getY());
        pvpConfig.set("Spawns." + arenaSpawns.size() + ".z", spawn.getZ());
        try {
            pvpConfig.save(pvpConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addGear(Player p) {
        p.getInventory().clear();
        ItemStack sword = new ItemStack(Material.IRON_SWORD, 1);
        ItemStack bow = new ItemStack(Material.BOW, 1);
        ItemStack arrow = new ItemStack(Material.ARROW, 1);
        p.getInventory().setItem(0, sword);
        p.getInventory().setItem(1, bow);
        p.getInventory().setItem(2, arrow);
    }
}
