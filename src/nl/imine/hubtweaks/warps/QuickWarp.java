package nl.imine.hubtweaks.warps;

import nl.imine.hubtweaks.HubTweaks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class QuickWarp {

    private static QuickWarp quickWarp;
    private Inventory warpInv;

    public static void init(){
        QuickWarp.quickWarp = new QuickWarp();
        QuickWarpListener.init(quickWarp);
    }
    
    public QuickWarp() {
        loadInv();
    }

    private void loadInv() {
        warpInv = Bukkit.createInventory(null, 9, "Select your destination:");
        FileConfiguration config = HubTweaks.getInstance().getConfig();
        try {
            System.out.println(config.getConfigurationSection("WarpItems").getKeys(false));
        } catch (NullPointerException e) {
            System.out.println("ConfigSection 'WarpItems' empty, Ignoring");
            return;
        }
        for (String key : config.getConfigurationSection("WarpItems").getKeys(false)) {
            //Location
            Double x = config.getDouble("WarpItems." + key + ".x");
            Double y = config.getDouble("WarpItems." + key + ".y");
            Double z = config.getDouble("WarpItems." + key + ".z");
            World world = Bukkit.getWorld(config.getString("WarpItems." + key + ".world"));
            Float yaw = Float.parseFloat(config.getString("WarpItems." + key + ".yaw"));
            Float pitch = Float.parseFloat(config.getString("WarpItems." + key + ".pitch"));
            Location loc = new Location(world, x, y, z, yaw, pitch);
            //Slot
            int slot = config.getInt("WarpItems." + key + ".slot");
            //ItemStack
            Material type = Material.getMaterial(config.getString("WarpItems." + key + ".type"));
            Short meta = Short.parseShort(config.getString("WarpItems." + key + ".meta"));
            String[] Strlist = new String[2];
            Strlist[0] = config.getString("WarpItems." + key + ".displayname");
            Strlist[1] = config.getString("WarpItems." + key + ".lore");
            Warp.addWarp(new Warp(this, loc, slot, Strlist, type, meta));
        }
    }

    public void addQuickWarp(Player player, String warpname, int slot) {
        FileConfiguration config = HubTweaks.getInstance().getConfig();
        config.set("WarpItems." + warpname + ".x", player.getLocation().getX());
        config.set("WarpItems." + warpname + ".y", player.getLocation().getY());
        config.set("WarpItems." + warpname + ".z", player.getLocation().getZ());
        config.set("WarpItems." + warpname + ".world", player.getLocation().getWorld().getName());
        config.set("WarpItems." + warpname + ".yaw", player.getLocation().getYaw());
        config.set("WarpItems." + warpname + ".pitch", player.getLocation().getPitch());
        config.set("WarpItems." + warpname + ".slot", slot);
        config.set("WarpItems." + warpname + ".type", player.getItemInHand().getType().toString());
        config.set("WarpItems." + warpname + ".meta", player.getItemInHand().getDurability());
        config.set("WarpItems." + warpname + ".displayname", player.getItemInHand().getItemMeta().getDisplayName());
        Location loc = player.getLocation();
        ItemStack item = new ItemStack(player.getItemInHand().getType(), 1, player.getItemInHand().getDurability());
        String[] Strlist = new String[1];
        ItemMeta meta = (ItemMeta) item.getItemMeta();
        meta.setDisplayName(player.getItemInHand().getItemMeta().getDisplayName());
        Strlist[0] = player.getItemInHand().getItemMeta().getDisplayName();
        if (player.getItemInHand().getItemMeta().getLore() != null) {
            player.getItemInHand().getItemMeta().getLore().get(0);
            config.set("WarpItems." + warpname + ".lore", player.getItemInHand().getItemMeta().getLore().get(0));
            Strlist[1] = player.getItemInHand().getItemMeta().getLore().get(0);
            meta.setLore(player.getItemInHand().getItemMeta().getLore());
        }
        item.setItemMeta(meta);
        Warp.addWarp(new Warp(this, loc, slot, Strlist, player.getItemInHand().getType(), player.getItemInHand().getDurability()));
        HubTweaks.getInstance().saveConfig();
    }

    public void openWarpInv(Player player) {
        player.openInventory(warpInv);
    }
    
    public Inventory getInventory(){
        return this.warpInv;
    }
    
    public static QuickWarp getInstance(){
        return QuickWarp.quickWarp;
    }
}
