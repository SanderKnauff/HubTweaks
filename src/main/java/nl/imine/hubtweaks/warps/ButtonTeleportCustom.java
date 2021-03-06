package nl.imine.hubtweaks.warps;

import nl.imine.api.gui.Container;
import nl.imine.api.gui.button.ButtonTeleport;
import nl.imine.api.util.ColorUtil;
import nl.imine.api.util.ItemUtil;
import nl.imine.api.util.PlayerUtil;
import nl.imine.hubtweaks.parkour.ParkourManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ButtonTeleportCustom extends ButtonTeleport {

	public ButtonTeleportCustom(ItemStack itemStack, int slot, Location location) {
		super(itemStack, slot, location);
	}

	@Override
	public void doAction(Player player, Container container, ClickType clickType) {
		if (ParkourManager.getParkourInstance().getParkourPlayer(player).getHighestLevel().getLevel() > 4) {
			super.doAction(player, container, clickType);
			ItemStack is = ItemUtil.getBuilder(Material.ELYTRA).build();
			ItemMeta im = is.getItemMeta();
			im.spigot().setUnbreakable(true);
			is.setItemMeta(im);
			player.getInventory().setChestplate(is);
		} else {
			PlayerUtil.sendActionMessage(player,
				ColorUtil.replaceColors("&c&lYou need to complete the parkour before warping to the Elytra Parkour"));
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
		}
	}

}
