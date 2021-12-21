package nl.imine.api.gui.button;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import nl.imine.api.gui.Button;
import nl.imine.api.gui.Container;

public class ButtonTeleport extends Button {

	private final Location location;

	public ButtonTeleport(ItemStack itemStack, int slot, Location location) {
		super(itemStack, slot);
		this.location = location;
	}

	public Location getLocation() {
		return this.location;
	}

	@Override
	public void doAction(Player player, Container container, ClickType clickType) {
		if (getLocation() != null) {
			player.teleport(getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
		}
	}
}
