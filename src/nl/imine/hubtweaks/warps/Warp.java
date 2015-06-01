package nl.imine.hubtweaks.warps;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Warp {

    private static final List<Warp> warpList = new ArrayList<Warp>();

    private Location loc;
    private int slot;
    private String[] data = new String[2];
    private Material material;
    private short metaid;
    private ItemStack item;

    public Warp(QuickWarp qw, Location loc, int slot, String[] data, Material material, short metaid) {
        this.loc = loc;
        this.slot = slot;
        this.data = data;
        this.material = material;
        this.metaid = metaid;
        item = new ItemStack(material, 1, metaid);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(data[0]);
        List<String> LoreString = new ArrayList<>();
        if (data.length < 1) {
            LoreString.add(data[1]);
        }
        meta.setLore(LoreString);
        item.setItemMeta(meta);
        qw.getInventory().setItem(slot, item);
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public String[] getData() {
        return data.clone();
    }

    public void setData(String[] data) {
        this.data = data.clone();
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public short getMetaid() {
        return metaid;
    }

    public void setMetaid(short metaid) {
        this.metaid = metaid;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }
    
    public static void addWarp(Warp warp){
        warpList.add(warp);
    }

    public static Warp getWarpByName(String name) {
        for (Warp warp : warpList) {
            if (warp.getData()[0].equals(name)) {
                return warp;
            }
        }
        return null;
    }
}
