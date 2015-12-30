package nl.imine.hubtweaks.warps;

import nl.imine.api.gui.Button;
import nl.imine.api.gui.Container;
import nl.makertim.uhchub.api.UHCRequester;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ButtonUHC extends Button {

    private final boolean hasError;
    private final int id;

    public ButtonUHC(Container container, int id, ItemStack itemStack, int slot, boolean error) {
        super(container, itemStack, slot);
        this.hasError = error;
        this.id = id;
    }

    @Override
    public void doAction(Player player) {
        if (!hasError) {
            UHCRequester.sendTo(id, player);
        } else {
            player.sendMessage(
                    ChatColor.RED.toString() + ChatColor.BOLD.toString() + "This server is not avalible at the moment");
        }
    }
}
