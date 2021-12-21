package nl.imine.api.gui;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import nl.imine.api.event.ContainerBuildInventoryEvent;

public class Button {

	protected ItemStack itemStack;
	protected int slot;

	public Button(ItemStack is, int slot) {
		this.itemStack = is;
		this.slot = slot;
	}

	public ItemStack getItemStack() {
		ItemStack is = itemStack;
		ItemMeta ism = is.getItemMeta();
		ism.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ATTRIBUTES);
		is.setItemMeta(ism);
		return is;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return itemStack.getItemMeta().getDisplayName();
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		ItemMeta im = itemStack.getItemMeta();
		im.setDisplayName(name);
		itemStack.setItemMeta(im);
	}

	/**
	 * @return the subtext
	 */
	public List<String> getSubtext() {
		return itemStack.getItemMeta().getLore();
	}

	/**
	 * @param subtext
	 *            the subtext to set
	 */
	public void setSubtext(List<String> subtext) {
		ItemMeta im = itemStack.getItemMeta();
		im.setLore(subtext);
		itemStack.setItemMeta(im);
	}

	/**
	 * @return the slot
	 */
	public int getSlot() {
		return slot;
	}

	/**
	 * @param slot
	 *            the slot to set
	 */
	public Button setSlot(int slot) {
		this.slot = slot;
		return this;
	}

	public void onRefresh(ContainerBuildInventoryEvent cbie) {
	}

	public void doAction(final Player player, final Container container, final ClickType clickType) {
	}

	@Override
	public String toString() {
		return String.format("{Slot:%d, Item:%s}", slot, itemStack.toString());
	}
}
