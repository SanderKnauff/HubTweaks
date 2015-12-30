package nl.imine.hubtweaks.warps;

import nl.imine.api.gui.Button;
import nl.imine.api.gui.Container;
import nl.makertim.uhchub.api.UHCRequester;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ButtonUHC extends Button {

    private boolean hasError;

    public ButtonUHC(Container container, ItemStack itemStack, int slot, boolean error) {
        super(container, itemStack, slot);
    }

    @Override
    public void doAction(Player player) {
        if (!hasError) {
            UHCRequester.sendTo(slot, player);
        } else {
            player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "This server is not avalible at the moment");
        }
    }
}
