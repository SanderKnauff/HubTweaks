package nl.imine.hubtweaks.warps;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import nl.imine.api.gui.Button;
import nl.imine.api.gui.Container;
import nl.imine.api.gui.GuiManager;
import nl.imine.api.util.ColorUtil;
import nl.imine.api.util.ItemUtil;
import nl.makertim.uhchub.api.UHCRequester;

public class ButtonBrowseUHC extends Button {

    public ButtonBrowseUHC(ItemStack itemStack, int slot) {
        super(itemStack, slot);
    }

    @Override
    public ItemStack getItemStack() {
        boolean error = false;
        for (int i : UHCRequester.getPortalIds()) {
            if (UHCRequester.request(i).hasError()) {
                error = true;
                break;
            }
        }
        if (error) {
            return ItemUtil.getBuilder(Material.APPLE)
                    .setName(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "UHC IS OFFLINE").build();
        }
        return super.getItemStack();
    }

    @Override
    public void doAction(Player player, Container container, ClickType clickType) {
        Container uhcContainer = GuiManager.getInstance().createContainer(ColorUtil.replaceColors("&cSelect a lobby"),
                9, true, false);
        for (int i : UHCRequester.getPortalIds()) {
            uhcContainer.addButton(new ButtonUHC(i, i + 2));
        }
        uhcContainer.setRefreshRate(20L);
        uhcContainer.open(player);
    }
}
