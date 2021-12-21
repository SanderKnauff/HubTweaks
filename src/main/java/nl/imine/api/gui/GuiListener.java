package nl.imine.api.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;

public class GuiListener implements Listener {

	private static Plugin plugin;

	private GuiListener() {
	}

	static void init(Plugin plugin) {
		GuiListener.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(new GuiListener(), plugin);
	}

	@EventHandler
	public void onPlayerInventoryClick(InventoryClickEvent evt) {
		if (GuiManager.getInstance().isGuiInventory(evt.getClickedInventory())) {
			Container container = GuiManager.getInstance().getInventoryContainer(evt.getClickedInventory());
			if (container.getButton(evt.getSlot(), container.getOpenPage()) != null) {
				Button button = container.getButton(evt.getSlot(), container.getOpenPage());
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
					try {
						button.doAction((Player) evt.getWhoClicked(), container, evt.getClick());
					} catch (Throwable thr) {
						thr.printStackTrace();
						evt.getWhoClicked().sendMessage(ChatColor.GRAY + thr.getMessage());
					}
				});
			}
			evt.setCancelled(true);
		}
		if (GuiManager.getInstance().isGuiInventory(evt.getView().getTopInventory())) {
			if (evt.getClickedInventory() != null) {
				if (evt.getClickedInventory().equals(evt.getView().getBottomInventory())) {
					if (evt.getClick().isShiftClick()) {
						evt.setCancelled(true);
					}
					if (evt.getClick().equals(ClickType.DOUBLE_CLICK)) {
						evt.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInventoryClose(InventoryCloseEvent evt) {
		if (GuiManager.getInstance().isGuiInventory(evt.getInventory())) {
			Container container = GuiManager.getInstance().getInventoryContainer(evt.getInventory());
			container.removeInventory(evt.getInventory());
			if (container.getOpenInventories().isEmpty()) {
				GuiManager.getInstance().removeContainer(container);
			}
		}
	}
}
