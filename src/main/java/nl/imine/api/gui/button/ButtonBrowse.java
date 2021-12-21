package nl.imine.api.gui.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import nl.imine.api.gui.Button;
import nl.imine.api.gui.Container;

public class ButtonBrowse extends Button {

	private Container destination;

	public ButtonBrowse(ItemStack itemStack, int slot, Container destination) {
		super(itemStack, slot);
		this.destination = destination;
	}

	public Container getDestination() {
		return this.destination;
	}

	@Override
	public void doAction(Player player, Container container, ClickType clickType) {
		Container c = getDestination();
		if (c != null) {
			c.open(player);
		}
	}
}
