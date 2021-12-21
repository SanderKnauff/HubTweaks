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

public class ButtonCommand extends Button {

	private final String command;

	public ButtonCommand(ItemStack itemStack, int slot, String command) {
		super(addLore(itemStack, command), slot);
		this.command = command;
	}

	private static ItemStack addLore(ItemStack is, String command) {
		ItemMeta im = is.getItemMeta();
		List<String> lore = im.getLore();
		if (lore == null) {
			lore = new ArrayList<>();
		} else {
			lore.add("");
		}
		lore.add(ColorUtil.replaceColors("&8/%s".formatted(command)));
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}

	public String getCommand() {
		return command;
	}

	@Override
	public void doAction(Player player, Container container, ClickType clickType) {
		String cmd = getCommand();
		if (cmd != null) {
			player.performCommand(getCommand());
		}
	}
}
