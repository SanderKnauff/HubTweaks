
package nl.imine.hubtweaks.warps;

import nl.imine.api.gui.Container;
import nl.imine.api.gui.GuiManager;
import nl.imine.api.gui.button.ButtonTeleport;
import nl.imine.hubtweaks.oitc.PvP;
import nl.imine.hubtweaks.util.ColorUtil;
import nl.imine.hubtweaks.HubTweaksPlugin;
import nl.imine.hubtweaks.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.Plugin;

public class CompassWarp implements Listener {

	private final PvP pvp;
	private final Container inv;

	public CompassWarp(PvP pvp) {
		this.pvp = pvp;
		this.inv = GuiManager.getInstance().createContainer(ColorUtil.replaceColors("&9Select your destination:"), 18, false, false);
	}

	public void init(Plugin plugin) {
		inv.addButton(new ButtonTeleport(
				ItemUtil.getBuilder(Material.PURPLE_DYE)
						.setName(ColorUtil.replaceColors("&dSpawn")).build(),
				3, new Location(HubTweaksPlugin.getMainWorld(), 47.5D, 36.0D, -509.5D, -45F, 0F)));
		inv.addButton(new ButtonTeleport(
				ItemUtil.getBuilder(Material.WOODEN_SWORD).setName(ColorUtil.replaceColors("&8Cluedo")).build(), 5,
				new Location(HubTweaksPlugin.getMainWorld(), 54.5D, 36.0D, -529.5D, 180F, 0F)));
		inv.addButton(new ButtonTeleportCustom(
				ItemUtil.getBuilder(Material.ELYTRA).setName(ColorUtil.replaceColors("&5Elytra Parkour")).build(), 11,
				new Location(HubTweaksPlugin.getMainWorld(), 36.5D, 163.5D, -528.5D, -45F, 0F)));
		inv.addButton(new ButtonTeleport(
				ItemUtil.getBuilder(Material.GOLDEN_PICKAXE).setName(ColorUtil.replaceColors("&6Survival"))
						.addFlag(ItemFlag.HIDE_ATTRIBUTES).build(),
				15, new Location(HubTweaksPlugin.getMainWorld(), 28.5D, 31.1D, -500.5D, 90F, 0F)));
		inv.addButton(new ButtonTeleport(
				ItemUtil.getBuilder(Material.BRICK).setName(ColorUtil.replaceColors("&aCreative")).build(), 17,
				new Location(HubTweaksPlugin.getMainWorld(), 33.5D, 64.1D, -534.5D, 180F, 0F)));

		inv.addButton(new ButtonOitc(9, pvp));

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onCompassClick(PlayerInteractEvent pie) {
		if ((pie.getAction() == Action.RIGHT_CLICK_AIR || pie.getAction() == Action.RIGHT_CLICK_BLOCK)
				&& pie.getPlayer().getInventory().getItemInMainHand().getType() == Material.COMPASS) {
			inv.open(pie.getPlayer());
		}
	}
}
