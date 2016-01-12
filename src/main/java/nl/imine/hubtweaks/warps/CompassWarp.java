package nl.imine.hubtweaks.warps;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;

import nl.imine.api.gui.Container;
import nl.imine.api.gui.GuiManager;
import nl.imine.api.gui.button.ButtonTeleport;
import nl.imine.api.util.ItemUtil;
import nl.imine.hubtweaks.HubTweaks;

public class CompassWarp implements Listener {

    private Container inv;

    public static void init() {
        new CompassWarp();
    }

    public Container getInv() {
        return inv;
    }

    public CompassWarp() {
        Bukkit.getScheduler().runTaskAsynchronously(HubTweaks.getInstance(), () -> {
            inv = GuiManager.getInstance().createContainer(ChatColor.DARK_GRAY + "Select your destination:", 18, false,
                    false);

            inv.addButton(new ButtonTeleport(inv,
                    ItemUtil.getBuilder(Material.INK_SACK).setDurability((short) 5)
                    .setName(ChatColor.LIGHT_PURPLE + "Spawn").build(),
                    0, new Location(HubTweaks.getMainWorld(), 52.5D, 36.1D, -503.5D, -90F, 0F)));
            inv.addButton(
                    new ButtonTeleport(inv, ItemUtil.getBuilder(Material.TNT).setName(ChatColor.YELLOW + "Outlaws").build(),
                            2, new Location(HubTweaks.getMainWorld(), 73.5D, 36.1D, -503.5D, -90F, 0F)));
            inv.addButton(new ButtonBrowseUHC(
                    inv, ItemUtil.getBuilder(Material.GOLDEN_APPLE).setDurability((short) 1)
                    .setName(ChatColor.DARK_RED + "UHC").build(),
                    4));
            inv.addButton(new ButtonTeleport(inv,
                    ItemUtil.getBuilder(Material.GOLD_PICKAXE).setName(ChatColor.GOLD + "Survival")
                    .addFlag(ItemFlag.HIDE_ATTRIBUTES).build(),
                    6, new Location(HubTweaks.getMainWorld(), 28.5D, 31.1D, -500.5D, 90F, 0F)));
            inv.addButton(new ButtonTeleport(inv,
                    ItemUtil.getBuilder(Material.BRICK).setName(ChatColor.GREEN + "Creative").build(), 8,
                    new Location(HubTweaks.getMainWorld(), 33.5D, 64.1D, -534.5D, 180F, 0F)));

            inv.addButton(new ButtonOitc(inv, 12));
            inv.addButton(
                    new ButtonTeleport(inv, ItemUtil.getBuilder(Material.STICK).setName(ChatColor.AQUA + "Tag").build(), 14,
                            new Location(HubTweaks.getMainWorld(), -15.5D, 33.1D, -481.5D, -90F, 0F)));

            Bukkit.getPluginManager().registerEvents(this, HubTweaks.getInstance());
        });
    }

    @EventHandler
    public void onCompassClick(PlayerInteractEvent pie) {
        if ((pie.getAction() == Action.RIGHT_CLICK_AIR || pie.getAction() == Action.RIGHT_CLICK_BLOCK)
                && pie.getPlayer().getItemInHand().getType() == Material.COMPASS) {
            inv.open(pie.getPlayer());
        }
    }
}
