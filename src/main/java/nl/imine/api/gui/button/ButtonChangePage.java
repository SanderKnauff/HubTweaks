package nl.imine.api.gui.button;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import nl.imine.api.gui.Button;
import nl.imine.api.gui.Container;

public class ButtonChangePage extends Button {

	private final BrowseDirection browseDirection;
	private final Container container;

	public ButtonChangePage(Container container, int slot, BrowseDirection browseDirection) {
		super(browseDirection.getItemStack(), slot);
		this.browseDirection = browseDirection;
		this.container = container;
	}

	@Override
	public ItemStack getItemStack() {
		ItemStack is = super.getItemStack();
		Container c = getContainer();
		BrowseDirection bd = getBrowseDirection();
		if (c == null || bd == null) {
			return is;
		}
		if (bd == BrowseDirection.PREVIOUS) {
			if (c.getOpenPage() == 0) {
				is.setType(Material.BARRIER);
			} else {
				is.setType(Material.OAK_BUTTON);
			}
		} else if (bd == BrowseDirection.NEXT) {
			if (c.getOpenPage() == c.getPageAmount()) {
				is.setType(Material.BARRIER);
			} else {
				is.setType(Material.STONE_BUTTON);
			}
		}
		return is;
	}

	public BrowseDirection getBrowseDirection() {
		return browseDirection;
	}

	public Container getContainer() {
		return container;
	}

	@Override
	public void doAction(Player player, Container c, ClickType clickType) {
		int amount = 0;
		if (getBrowseDirection() == BrowseDirection.PREVIOUS) {
			if (c.getOpenPage() != 0) {
				amount = getBrowseDirection().getAmount();
			}
		} else if (getBrowseDirection() == BrowseDirection.NEXT) {
			if (c.getOpenPage() != c.getPageAmount()) {
				amount = getBrowseDirection().getAmount();
			}
		}
		c.changeToPage(c.getOpenPage() + amount);
	}
}
