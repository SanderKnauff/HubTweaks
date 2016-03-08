package nl.imine.hubtweaks.warps;

import nl.imine.api.gui.Button;
import nl.imine.api.gui.Container;
import nl.imine.api.util.ColorUtil;
import nl.imine.api.util.ItemUtil;
import nl.makertim.uhchub.api.UHCRequester;
import nl.makertim.uhchub.api.UHCRequester.PortalRequest;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ButtonUHC extends Button {

    private final int id;
    private boolean safe = false;

    public ButtonUHC(int id, int slot) {
        super(null, slot);
        this.id = id;
    }

    @Override
    public ItemStack getItemStack() {
        PortalRequest request = UHCRequester.request(id);
        ItemUtil.Builder item = ItemUtil.getBuilder(Material.GOLDEN_APPLE);
        String name;
        if (request.hasError()) {
            name = ColorUtil.replaceColors("&c&lGame %s is unavalible.", id + 1);
        } else {
            name = ColorUtil.replaceColors((request.isOpen() ? "&a" : "&c") + "&lGame %s is unavalible.", id + 1);
            String players = ColorUtil.replaceColors("&bPlayers currently in the game: &c%d&b.",
                    request.getPlayerCount());
            String status = ColorUtil.replaceColors("&bStatus: &e%s&b.", request.getStatus());
            String timer = ColorUtil.replaceColors("&bTimer: &c%dmin &bin game.",
                    (int) Math.floor(request.getTimer() / 60));
            if (request.isOpen()) {
                timer = ColorUtil.replaceColors("&bGame will begin in &c%d seconds&b.", request.getTimer());
            }
            item.addLore(players, status, timer).setAmmount(request.getPlayerCount());
            safe = true;
        }
        item.setName(name);
        return item.build();
    }

    @Override
    public void doAction(Player player, Container container, ClickType clickType) {
        if (safe) {
            UHCRequester.sendTo(id, player);
        } else {
            player.sendMessage(
                    ChatColor.RED.toString() + ChatColor.BOLD.toString() + "This server is not avalible at the moment");
        }
    }
}
