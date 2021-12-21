package nl.imine.api.gui.button;

import java.util.ArrayList;
import java.util.List;

import nl.imine.hubtweaks.util.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import nl.imine.api.gui.Button;
import nl.imine.api.gui.Container;

public class ButtonListed extends Button {

	private final List<String>[] info;
	private int page;

	public ButtonListed(ItemStack is, List<String>[] info, int slot) {
		super(is, slot);
		this.info = info;
		this.page = 0;
	}

	@Override
	public ItemStack getItemStack() {
		ItemStack is = super.getItemStack();
		ItemMeta im = is.getItemMeta();
		im.setLore(getInfoPaged());
		is.setItemMeta(im);
		return is;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public List<String>[] getInfo() {
		return info;
	}

	public List<String> getInfoPaged() {
		List<String> ret = new ArrayList<>();
		List<String>[] info = getInfo();
		int page = getPage();
		if (info[page] != null) {
			info[page].forEach(str -> ret.add(str));
		} else {
			ret.add("&8Null");
		}
		ret.add("");
		ret.add(ColorUtil.replaceColors("&e&oLClick &8&olower page"));
		ret.add(ColorUtil.replaceColors("&e&oRClick &8&ohigher page"));
		ret.add(ColorUtil.replaceColors("&7Current page: &c%d&7/&c%d&7.".formatted(page + 1, info.length)));
		return ret;
	}

	@Override
	public void doAction(Player player, Container container, ClickType clickType) {
		if (clickType.isLeftClick()) {
			page = Math.max(0, page - 1);
		} else if (clickType.isRightClick()) {
			page = Math.min(info.length - 1, page + 1);
		}
		container.refresh();
	}
}
