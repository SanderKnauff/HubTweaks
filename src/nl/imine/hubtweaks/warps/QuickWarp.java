package nl.imine.hubtweaks.warps;

import nl.imine.hubtweaks.HubTweaks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class QuickWarp {

    private Plugin plugin;

    private Inventory warpInv;

    public QuickWarp(Plugin plugin) {
        this.plugin = plugin;
        QuickWarpListener.init(plugin, this);
        this.loadInv();
    }

    public void loadInv() {
        warpInv = Bukkit.createInventory(null, 9, "Select your destination:");
        try {
            System.out.println(plugin.getConfig().getConfigurationSection("WarpItems").getKeys(false));
        } catch (NullPointerException e) {
            System.out.println("ConfigSection 'WarpItems' empty, Ignoring");
            return;
        }
        for (String key : plugin.getConfig().getConfigurationSection("WarpItems").getKeys(false)) {
            //Location
            Double x = plugin.getConfig().getDouble("WarpItems." + key + ".x");
            Double y = plugin.getConfig().getDouble("WarpItems." + key + ".y");
            Double z = plugin.getConfig().getDouble("WarpItems." + key + ".z");
            World world = Bukkit.getWorld(plugin.getConfig().getString("WarpItems." + key + ".world"));
            Float yaw = Float.parseFloat(plugin.getConfig().getString("WarpItems." + key + ".yaw"));
            Float pitch = Float.parseFloat(plugin.getConfig().getString("WarpItems." + key + ".pitch"));
            Location loc = new Location(world, x, y, z, yaw, pitch);
            //Slot
            int slot = plugin.getConfig().getInt("WarpItems." + key + ".slot");
            //ItemStack
            Material type = Material.getMaterial(plugin.getConfig().getString("WarpItems." + key + ".type"));
            Short meta = Short.parseShort(plugin.getConfig().getString("WarpItems." + key + ".meta"));
            String[] Strlist = new String[2];
            Strlist[0] = plugin.getConfig().getString("WarpItems." + key + ".displayname");
            Strlist[1] = plugin.getConfig().getString("WarpItems." + key + ".lore");
            new Warp(this, loc, slot, Strlist, type, meta);
			//Debug

            /*System.out.println(key);
             System.out.println(x);
             System.out.println(y);
             System.out.println(z);
             System.out.println(world);
             System.out.println(yaw);
             System.out.println(pitch);
             System.out.println(slot);
             System.out.println(type);
             System.out.println(meta);*/
        }
    }

    public void addQuickWarp(Player player, String warpname, int slot) {
        //plugin.getConfig().createSection("WarpItems." + warpname);
        plugin.getConfig().set("WarpItems." + warpname + ".x", player.getLocation().getX());
        plugin.getConfig().set("WarpItems." + warpname + ".y", player.getLocation().getY());
        plugin.getConfig().set("WarpItems." + warpname + ".z", player.getLocation().getZ());
        plugin.getConfig().set("WarpItems." + warpname + ".world", player.getLocation().getWorld().getName());
        plugin.getConfig().set("WarpItems." + warpname + ".yaw", player.getLocation().getYaw());
        plugin.getConfig().set("WarpItems." + warpname + ".pitch", player.getLocation().getPitch());
        plugin.getConfig().set("WarpItems." + warpname + ".slot", slot);
        plugin.getConfig().set("WarpItems." + warpname + ".type", player.getItemInHand().getType().toString());
        plugin.getConfig().set("WarpItems." + warpname + ".meta", player.getItemInHand().getDurability());
        plugin.getConfig().set("WarpItems." + warpname + ".displayname", player.getItemInHand().getItemMeta().getDisplayName());
        Location loc = player.getLocation();
        ItemStack item = new ItemStack(player.getItemInHand().getType(), 1, player.getItemInHand().getDurability());
        String[] Strlist = new String[1];
        ItemMeta meta = (ItemMeta) item.getItemMeta();
        meta.setDisplayName(player.getItemInHand().getItemMeta().getDisplayName());
        Strlist[0] = player.getItemInHand().getItemMeta().getDisplayName();
        if (player.getItemInHand().getItemMeta().getLore() != null) {
            player.getItemInHand().getItemMeta().getLore().get(0);
            plugin.getConfig().set("WarpItems." + warpname + ".lore", player.getItemInHand().getItemMeta().getLore().get(0));
            Strlist[1] = player.getItemInHand().getItemMeta().getLore().get(0);
            meta.setLore(player.getItemInHand().getItemMeta().getLore());
        }
        item.setItemMeta(meta);
        new Warp(this, loc, slot, Strlist, player.getItemInHand().getType(), player.getItemInHand().getDurability());
        //itemIndex.put(slot, player.getLocation());
        plugin.saveConfig();
    }

    public void openWarpInv(Player player) {
        player.openInventory(warpInv);
    }
    
    public Inventory getInventory(){
        return this.warpInv;
    }
}
