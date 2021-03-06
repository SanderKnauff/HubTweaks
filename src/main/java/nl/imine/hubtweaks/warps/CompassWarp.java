
package nl.imine.hubtweaks.warps;

import nl.imine.api.gui.Container;
import nl.imine.api.gui.GuiManager;
import nl.imine.api.gui.button.ButtonTeleport;
import nl.imine.api.util.ColorUtil;
import nl.imine.api.util.ItemUtil;
import nl.imine.hubtweaks.HubTweaks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;

public class CompassWarp implements Listener {

	private static Container inv;

	public static void init() {
		inv = GuiManager.getInstance().createContainer(ColorUtil.replaceColors("&9Select your destination:"), 18, false,
			false);

		inv.addButton(new ButtonTeleport(
				ItemUtil.getBuilder(Material.INK_SACK).setDurability((short) 5)
						.setName(ColorUtil.replaceColors("&dSpawn")).build(),
				3, new Location(HubTweaks.getMainWorld(), 47.5D, 36.0D, -509.5D, -45F, 0F)));
		inv.addButton(new ButtonTeleport(
				ItemUtil.getBuilder(Material.STONE).setName(ColorUtil.replaceColors("&8Voidblock")).build(), 5,
				new Location(HubTweaks.getMainWorld(), 54.5D, 36.0D, -529.5D, 180F, 0F)));
		inv.addButton(new ButtonTeleportCustom(
				ItemUtil.getBuilder(Material.ELYTRA).setName(ColorUtil.replaceColors("&5Elytra Parkour")).build(), 11,
				new Location(HubTweaks.getMainWorld(), 36.5D, 163.5D, -528.5D, -45F, 0F)));
		inv.addButton(new ButtonBrowseUHC(ItemUtil.getBuilder(Material.GOLDEN_APPLE).setDurability((short) 1)
				.setName(ColorUtil.replaceColors("&4UHC")).build(), 13));
		inv.addButton(new ButtonTeleport(
				ItemUtil.getBuilder(Material.GOLD_PICKAXE).setName(ColorUtil.replaceColors("&6Survival"))
						.addFlag(ItemFlag.HIDE_ATTRIBUTES).build(),
				15, new Location(HubTweaks.getMainWorld(), 28.5D, 31.1D, -500.5D, 90F, 0F)));
		inv.addButton(new ButtonTeleport(
				ItemUtil.getBuilder(Material.BRICK).setName(ColorUtil.replaceColors("&aCreative")).build(), 17,
				new Location(HubTweaks.getMainWorld(), 33.5D, 64.1D, -534.5D, 180F, 0F)));

		inv.addButton(new ButtonOitc(9));

		Bukkit.getPluginManager().registerEvents(new CompassWarp(), HubTweaks.getInstance());
	}

	@EventHandler
	public void onCompassClick(PlayerInteractEvent pie) {
		if ((pie.getAction() == Action.RIGHT_CLICK_AIR || pie.getAction() == Action.RIGHT_CLICK_BLOCK)
				&& pie.getPlayer().getInventory().getItemInMainHand().getType() == Material.COMPASS) {
			inv.open(pie.getPlayer());
		}
	}
}
