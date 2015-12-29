package nl.imine.hubtweaks.warps;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import nl.imine.api.gui.Button;
import nl.imine.api.gui.Container;
import nl.imine.api.util.ItemUtil;
import nl.imine.hubtweaks.HubTweaks;
import nl.imine.hubtweaks.pvp.PvPJoinEvent;

public class ButtonOitc extends Button {

    public ButtonOitc(Container container, int slot) {
        super(container, ItemUtil.getBuilder(Material.IRON_SWORD).setName(ChatColor.RED + "One in the Chamber")
                .addFlag(ItemFlag.HIDE_ATTRIBUTES).build(), slot);
    }

    @Override
    public void doAction(Player player) {
        HubTweaks.getInstance().getServer().getPluginManager().callEvent(new PvPJoinEvent(player));
    }
}