package nl.imine.hubtweaks.warps;

import nl.imine.hubtweaks.HubTweaks;
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

public class QuickWarpListener implements Listener {


    public static void init(QuickWarp qw){
        HubTweaks.getInstance().getServer().getPluginManager().registerEvents(new QuickWarpListener(), HubTweaks.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerClickEvent(PlayerInteractEvent Event) {
        if (Event.getAction().equals(Action.RIGHT_CLICK_AIR) || Event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Player player = Event.getPlayer();
            if (player.getItemInHand().getType().equals(Material.COMPASS)) {
                QuickWarp.getInstance().openWarpInv(player);
            }
        }
    }

    @EventHandler
    public void onPlayerInventoryClickEvent(InventoryClickEvent Event) {
        Player player = (Player) Event.getWhoClicked();
        if (Event.getInventory().getName().equals(QuickWarp.getInstance().getInventory().getName())) {
            if (Event.getCurrentItem() != null) {
                if (Event.getCurrentItem().getType() != Material.AIR) {
                    if (Warp.getWarpByName(Event.getCurrentItem().getItemMeta().getDisplayName()) != null) {
                        if (!Warp.getWarpByName(Event.getCurrentItem().getItemMeta().getDisplayName()).getData()[0].equals("PvP")) {
                            player.teleport(Warp.getWarpByName(Event.getCurrentItem().getItemMeta().getDisplayName()).getLoc(), TeleportCause.PLUGIN);
                        }
                        if (Event.getCurrentItem().getItemMeta().getDisplayName().contains("One in the Chamber")) {
                            PvPJoinEvent pvpjoinevent = new PvPJoinEvent(player);
                            HubTweaks.getInstance().getServer().getPluginManager().callEvent(pvpjoinevent);
                            Event.setCancelled(true);
                        }
                    }
                }
            }
            Event.setCancelled(true);
        }
        if (!Event.getInventory().getName().equals(QuickWarp.getInstance().getInventory().getName())) {
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
        if (Event.getInventory().getName().equals(QuickWarp.getInstance().getInventory().getName())) {
            Event.setCancelled(true);
        }
    }
}
