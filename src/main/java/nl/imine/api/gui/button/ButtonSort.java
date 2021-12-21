package nl.imine.api.gui.button;

import java.util.ArrayList;
import java.util.List;

import nl.imine.hubtweaks.util.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import nl.imine.api.event.ContainerBuildInventoryEvent;
import nl.imine.api.gui.Button;
import nl.imine.api.gui.Container;
import nl.imine.api.gui.InventorySorter;

public class ButtonSort extends Button {

	private final InventorySorter[] sorters;
	private int page;

	public ButtonSort(ItemStack is, int slot) {
		this(is, slot, new InventorySorter("On name"));
	}

	public ButtonSort(ItemStack is, int slot, InventorySorter... sorters) {
		super(is, slot);
		this.sorters = sorters;
		this.page = 0;
	}

	@Override
	public ItemStack getItemStack() {
		ItemStack is = super.getItemStack();
		ItemMeta im = is.getItemMeta();
		im.setLore(getInfo());
		is.setItemMeta(im);
		return is;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public InventorySorter[] getSorters() {
		return sorters;
	}

	public List<String> getInfo() {
		int page = getPage();
		InventorySorter[] sorters = getSorters();
		List<String> ret = new ArrayList<>();
		if (sorters[page] != null) {
			ret.add(ColorUtil.replaceColors("&e" + sorters[page].getName()));
		} else {
			ret.add("&8Null");
		}
		ret.add("");
		ret.add(ColorUtil.replaceColors("&e&oLClick &8&olower page"));
		ret.add(ColorUtil.replaceColors("&e&oRClick &8&ohigher page"));
		ret.add(ColorUtil.replaceColors("&7Current page: &c%d&7/&c%d&7.", page + 1, sorters.length));
		return ret;
	}

	@Override
	public void onRefresh(ContainerBuildInventoryEvent cbie) {
		int page = getPage();
		InventorySorter[] sorters = getSorters();
		if (cbie.getContainer().hasButton(this)) {
			if (sorters[page] != null) {
				cbie.getContainer().setSorter(sorters[page]);
			}
		}
	}

	@Override
	public void doAction(Player player, Container container, ClickType clickType) {
		if (clickType.isLeftClick()) {
			setPage(Math.max(0, getPage() - 1));
		} else if (clickType.isRightClick()) {
			setPage(Math.min(getSorters().length - 1, getPage() + 1));
		}
		container.refresh();
	}
}
