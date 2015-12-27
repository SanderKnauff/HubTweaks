package nl.imine.hubtweaks.kotl;

import java.io.File;
import java.io.IOException;
import nl.imine.hubtweaks.HubTweaks;
import nl.imine.hubtweaks.Statistic;
import nl.imine.hubtweaks.util.Log;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Kotl {

    private static Kotl kotl;

    private Location plate;
    private Player king;
    private Player oldKing;
    private int radius;
    private final File configFile;
    private YamlConfiguration config;

    public static void init() {
        Kotl.kotl = new Kotl();
        KotlListener.init(kotl);
    }

    public Kotl() {
        this.configFile = new File(HubTweaks.getInstance().getDataFolder() + File.separator + "KOTL.yml");
        if (!configFile.exists()) {
            try {
                boolean success = configFile.createNewFile();
                Log.finest("Creating file " + configFile.getPath() + ": " + success);
            } catch (IOException e) {
            }
        }
        loadKOTL();
    }

    private void loadKOTL() {
        Log.info("Loading Config from " + configFile.getPath());
        if (configFile.exists()) {
            config = YamlConfiguration.loadConfiguration(configFile);
            ConfigurationSection section = config.getConfigurationSection("Kotl");
            if (section != null) {
                plate = getLocationFromSection(section);
                radius = section.getInt(KotlConfig.AREA_RADIUS);
            }
        }
    }

    public void addEntropiaWand(Player p) {
        ItemStack EWStack = new ItemStack(Material.GOLDEN_CARROT, 1);
        ItemMeta EWMeta = EWStack.getItemMeta();
        EWMeta.setDisplayName(ChatColor.RESET + "Entropia Wand");
        EWMeta.addEnchant(Enchantment.KNOCKBACK, (int) (Math.random() * 255D), true);
        EWStack.setItemMeta(EWMeta);
        ItemStack EntropiaWand = EWStack;

        p.getInventory().addItem(new ItemStack[] { EntropiaWand });
        p.getInventory().setHelmet(new ItemStack(Material.GOLD_HELMET));
    }

    public void removeEntropiaWand(final Player p) {
        p.getInventory().remove(Material.GOLDEN_CARROT);
        p.getInventory().remove(Material.GOLD_HELMET);
        p.getInventory().setHelmet(new ItemStack(Material.AIR));
        p.setItemOnCursor(new ItemStack(Material.AIR));
    }

    public Location getPlateLoc() {
        return plate;
    }

    public void setKing(Player player) {
        if (king != player && player != null) {
            Statistic.addToKing(player);
        }
        this.oldKing = king;
        this.king = player;
    }

    public Player getKing() {
        return this.king;
    }

    public Player getOldKing() {
        return this.oldKing;
    }

    public int getRadius() {
        return this.radius;
    }

    public YamlConfiguration getConfig() {
        return this.config;
    }

    public void saveConfig() {
        try {
            this.config.save(configFile);
        } catch (IOException e) {
        }
    }

    private Location getLocationFromSection(ConfigurationSection section) {
        World world = HubTweaks.getInstance().getServer().getWorld(section.getString(KotlConfig.LOCATION_WORLD));
        int x = (int) section.getDouble(KotlConfig.LOCATION_X);
        int y = (int) section.getDouble(KotlConfig.LOCATION_Y);
        int z = (int) section.getDouble(KotlConfig.LOCATION_Z);
        return new Location(world, x, y, z);
    }

    public static Kotl getInstance() {
        return Kotl.kotl;
    }
}
