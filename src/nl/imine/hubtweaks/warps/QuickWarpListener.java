package nl.imine.hubtweaks.warps;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import nl.imine.hubtweaks.pvp.PvPJoinEvent;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class QuickWarpListener implements Listener {

    private Plugin plugin;
    private QuickWarp qw;

    public static void init(Plugin plugin, QuickWarp qw){
        plugin.getServer().getPluginManager().registerEvents(new QuickWarpListener(plugin, qw), plugin);
    }
    
    private QuickWarpListener(Plugin plugin, QuickWarp qw) {
        this.plugin = plugin;
        this.qw = qw;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerClickEvent(PlayerInteractEvent Event) {
        if (Event.getAction().equals(Action.RIGHT_CLICK_AIR) || Event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Player player = Event.getPlayer();
            if (player.getItemInHand().getType().equals(Material.COMPASS)) {
                qw.openWarpInv(player);
            }
        }
    }

    @EventHandler
    public void onPlayerInventoryClickEvent(InventoryClickEvent Event) {
        Player player = (Player) Event.getWhoClicked();
        if (Event.getInventory().getName().equals(qw.getInventory().getName())) {
            if (Event.getCurrentItem() != null) {
                if (Event.getCurrentItem().getType() != Material.AIR) {
                    if (Warp.getWarpByName(Event.getCurrentItem().getItemMeta().getDisplayName()) != null) {
                        if (!Warp.getWarpByName(Event.getCurrentItem().getItemMeta().getDisplayName()).getData()[0].equals("PvP")) {
                            player.teleport(Warp.getWarpByName(Event.getCurrentItem().getItemMeta().getDisplayName()).getLoc(), TeleportCause.PLUGIN);
                        }
                        if (Event.getCurrentItem().getItemMeta().getDisplayName().contains("One in the Chamber")) {
                            PvPJoinEvent pvpjoinevent = new PvPJoinEvent(player);
                            plugin.getServer().getPluginManager().callEvent(pvpjoinevent);
                            Event.setCancelled(true);
                        }
                    }
                }
            }
            Event.setCancelled(true);
        }
        /*if (quickWarp.warpExists(Event.getSlot()).equals(true)) {
         if (Event.getInventory().getName().equals(QuickWarp.WarpInv.getName())) {
         int slot = Event.getSlot();
         if (Event.getInventory().getItem(slot).getItemMeta().getDisplayName().contains("Creative")) {
         ByteArrayOutputStream b = new ByteArrayOutputStream();
         DataOutputStream out = new DataOutputStream(b);
         try {
         out.writeUTF("Connect");
         out.writeUTF("Creative"); // Target Server
         } catch (IOException e) {
         // Can never happen
         }
         player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
         Event.setCancelled(true);
         } else {
         Location loc = quickWarp.TpLoc(slot);
         if (loc != null) {
         player.teleport(loc, TeleportCause.PLUGIN);
         Event.setCancelled(true);
         }
         }
         }
         }*/
        if (!Event.getInventory().getName().equals(qw.getInventory().getName())) {
            if (Event.getCurrentItem() != null) {
                if (Event.getCurrentItem().getType().equals(Material.LEATHER_BOOTS)) {
                    player.getInventory().setBoots(new ItemStack(Material.AIR, 1));
                    Event.setCancelled(true);
                }
            }
        }
        /*if(!player.hasPermission("Hubtweaks.inv")){
         Event.setCancelled(true);
         }*/
        if (Event.getInventory().getName().equals(qw.getInventory().getName())) {
            Event.setCancelled(true);
        }
    }
}
