package nl.imine.hubtweaks.util.gui;

import nl.imine.hubtweaks.HubTweaks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 *
 * @author Sansko1337
 */
public class GuiListener implements Listener {

    public static void init() {
        HubTweaks.getInstance().getServer().getPluginManager().registerEvents(new GuiListener(), HubTweaks.getInstance());
    }

    @EventHandler
    public void onPlayerInventoryClick(InventoryClickEvent evt) {
        if (GuiManager.getInstance().isGuiInventory(evt.getClickedInventory())) {
            Container container = GuiManager.getInstance().getInventoryContainer(evt.getClickedInventory());
            if (container.getButton(evt.getSlot()) != null) {
                Button button = container.getButton(evt.getSlot());
                button.doAction((Player) evt.getWhoClicked());
            }
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInventoryClose(InventoryCloseEvent evt) {
        if (GuiManager.getInstance().isGuiInventory(evt.getInventory())) {
            GuiManager.getInstance().getInventoryContainer(evt.getInventory()).removeInventory(evt.getInventory());
        }
    }
}
