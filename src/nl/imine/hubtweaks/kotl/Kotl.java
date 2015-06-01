package nl.imine.hubtweaks.kotl;

import java.io.File;
import java.io.IOException;
import nl.imine.hubtweaks.HubTweaks;

import nl.imine.hubtweaks.refrence.KotlConfig;
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
import org.bukkit.plugin.Plugin;

public class Kotl {

    private static Kotl kotl;
    
    private Location plate;
    private Player king;
    private int radius;
    private final File configFile;
    private YamlConfiguration config; 

    public static void init(){
        Kotl.kotl = new Kotl();
        KotlListener.init(kotl);
    }
    
    public Kotl() {
        this.configFile = new File(HubTweaks.getInstance().getDataFolder() + File.separator + "KOTL.yml");
        if(!configFile.exists()){
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
        EWMeta.addEnchant(Enchantment.KNOCKBACK, 2, true);
        EWStack.setItemMeta(EWMeta);
        ItemStack EntropiaWand = EWStack;

        p.getInventory().addItem(new ItemStack[]{EntropiaWand});
        p.getInventory().setHelmet(new ItemStack(Material.GOLD_HELMET));
    }

    public void removeEntropiaWand(final Player p) {

        if (p.getInventory().contains(Material.GOLDEN_CARROT)) {
            p.getInventory().remove(Material.GOLDEN_CARROT);
        }
        if (p.getInventory().contains(Material.GOLD_HELMET)) {
            p.getInventory().remove(Material.GOLD_HELMET);
        }
        if ((p.getInventory().getHelmet() != null) && (p.getInventory().getHelmet().getType() != null) && (p.getInventory().getHelmet().getType().equals(Material.GOLD_HELMET))) {
            p.getInventory().setHelmet(null);
        }
    }

    public Location getPlateLoc() {
        return plate;
    }

    public void setKing(Player player) {
        king = player;
    }

    public Player getKing() {
        return king;
    }

    public int getRadius() {
        return radius;
    }
    
    public YamlConfiguration getConfig(){
        return this.config;
    }
    
    public void saveConfig(){
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
    
    public static Kotl getInstance(){
        return Kotl.kotl;
    }
}
