package nl.imine.hubtweaks.pvp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nl.imine.hubtweaks.HubTweaks;
import nl.imine.hubtweaks.util.Log;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class PvP {

    private static final List<Location> arenaSpawns = new ArrayList<>();
    private static File pvpConfigFile = null;
    private static FileConfiguration pvpConfig = null;

    private static List<Player> pvpList;

    public static void init() {
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
        PvP.addGear(player);
        player.setFireTicks(0);
        player.setFallDistance(0);
        player.setVelocity(new Vector());
        player.setHealth(player.getMaxHealth());
        player.getActivePotionEffects().clear();
        player.teleport(PvP.getRandomSpawn());
        player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect((int) 100, 0));
    }

    public static void removePlayerFromArena(Player player) {
        if (pvpList.contains(player)) {
            pvpList.remove(player);
            player.getInventory().clear();
        }
    }

    public static void loadArena() {
        Log.fine("HTPVP: Loading Arena");
        pvpConfigFile = new File(HubTweaks.getInstance().getDataFolder().getPath() + File.separatorChar + "PvP.yml");
        if (!pvpConfigFile.exists()) {
            try {
                pvpConfigFile.createNewFile();
            } catch (IOException e) {
                Log.warning("ERROR: PVP FILE COULD NOT BE CREATED");
                return;
            }
        } else {
            System.out.println("PVPCONFIG Exists");
        }
        pvpConfig = YamlConfiguration.loadConfiguration(pvpConfigFile);
        if (pvpConfig.getConfigurationSection("Spawns") != null) {
            for (String key : pvpConfig.getConfigurationSection("Spawns").getKeys(false)) {
                World world = HubTweaks.getInstance().getServer().getWorld(pvpConfig.getString("Spawns." + key + ".world"));
                double x = pvpConfig.getDouble("Spawns." + key + ".x");
                double y = pvpConfig.getDouble("Spawns." + key + ".y");
                double z = pvpConfig.getDouble("Spawns." + key + ".z");
                arenaSpawns.add(new Location(world, x, y, z));
            }
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
            Log.warning("IOException loading hubtweaks PvP spawns: " + e.getMessage());
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
