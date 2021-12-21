package nl.imine.hubtweaks.warps;

import nl.imine.hubtweaks.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;

import nl.imine.api.gui.Button;
import nl.imine.api.gui.Container;
import nl.imine.hubtweaks.oitc.PvP;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ButtonOitc extends Button {

	private final PvP pvp;

	public ButtonOitc(int slot, PvP pvp) {
		super(ItemUtil.getBuilder(Material.IRON_SWORD).setAmount(1).setName(ChatColor.RED + "One in the Chamber")
				.addFlag(ItemFlag.HIDE_ATTRIBUTES).build(), slot);
		this.pvp = pvp;
	}

	@Override
	public void doAction(Player player, Container container, ClickType ct) {
		if (!pvp.getSpawnList().isEmpty()) {
			if (player.getVehicle() != null) {
				player.leaveVehicle();
			}
			player.getPassengers().forEach(Entity::leaveVehicle);
			player.teleport(pvp.getRandomSpawn(), PlayerTeleportEvent.TeleportCause.PLUGIN);
		} else {
			player.sendMessage(ChatColor.DARK_RED + "ERROR: Warp aborted due to no available spawns.");
		}
	}
}
