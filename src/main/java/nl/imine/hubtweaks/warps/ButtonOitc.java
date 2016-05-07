package nl.imine.hubtweaks.warps;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;

import nl.imine.api.gui.Button;
import nl.imine.api.gui.Container;
import nl.imine.api.util.ItemUtil;
import nl.imine.hubtweaks.oitc.PvP;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ButtonOitc extends Button {

	public ButtonOitc(int slot) {
		super(ItemUtil.getBuilder(Material.IRON_SWORD).setName(ChatColor.RED + "One in the Chamber")
				.addFlag(ItemFlag.HIDE_ATTRIBUTES).build(), slot);
	}

	@Override
	public void doAction(Player player, Container container, ClickType ct) {
		if (!PvP.getSpawnList().isEmpty()) {
			if (player.getVehicle() != null) {
				player.leaveVehicle();
			}
			if (player.getPassenger() != null) {
				player.getPassenger().leaveVehicle();
			}
			player.teleport(PvP.getRandomSpawn(), PlayerTeleportEvent.TeleportCause.PLUGIN);
		} else {
			player.sendMessage(ChatColor.DARK_RED + "ERROR: Warp aborted due to no avalible spawns.");
		}
	}
}
