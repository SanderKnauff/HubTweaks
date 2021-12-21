package nl.imine.api.gui.button;

import nl.imine.hubtweaks.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum BrowseDirection {

	PREVIOUS(-1, ItemUtil.getBuilder(Material.STONE_BUTTON).setName(ChatColor.RESET + "Previous page").setAmount(1)
			.build()),
	NEXT(1, ItemUtil.getBuilder(Material.STONE_BUTTON).setName(ChatColor.RESET + "Next page").setAmount(1).build());

	final int amount;
	final ItemStack itemStack;

	BrowseDirection(int amount, ItemStack itemStack) {
		this.amount = amount;
		this.itemStack = itemStack;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return itemStack.getItemMeta().getDisplayName();
	}

	/**
	 * @return the amount
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * @return the material
	 */
	public ItemStack getItemStack() {
		return itemStack;
	}
}
