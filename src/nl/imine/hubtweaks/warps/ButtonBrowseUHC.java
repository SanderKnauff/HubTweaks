/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.imine.hubtweaks.warps;

import nl.imine.api.gui.Button;
import nl.imine.api.gui.Container;
import nl.imine.api.gui.GuiManager;
import nl.imine.api.util.ItemUtil;
import nl.imine.hubtweaks.HubTweaks;

import nl.makertim.uhchub.api.UHCRequester;
import nl.makertim.uhchub.api.UHCRequester.PortalRequest;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Sander
 */
public class ButtonBrowseUHC extends Button {

    private boolean error = false;

    public ButtonBrowseUHC(Container container, ItemStack itemStack, int slot) {
        super(container, itemStack, slot);
        for (int i : UHCRequester.getPortalIds()) {
            if (!UHCRequester.request(i).hasError()) {
                error = false;
            } else {
                this.itemStack = ItemUtil.getBuilder(Material.APPLE).setName(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "UHC IS OFFLINE").build();
            }
        }
    }

    @Override
    public void doAction(Player player) {
        if (!error) {
            Bukkit.getScheduler().runTaskAsynchronously(HubTweaks.getInstance(), () -> {
                Container uhcContainer = GuiManager.getInstance().createContainer("Select a lobby", 9, true, false);
                for (int i : UHCRequester.getPortalIds()) {
                    PortalRequest request = UHCRequester.request(i);
                    if (request.hasError()) {
                        String name = ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Game " + (i + 1) + "is unavalible.";
                        ItemStack buttonItem = ItemUtil.getBuilder(Material.APPLE).setName(name).build();
                        uhcContainer.addButton(new ButtonUHC(uhcContainer, buttonItem, i + 2, true));
                    } else {
                        String name = (request.isOpen() ? ChatColor.GREEN.toString() : ChatColor.RED.toString()) + ChatColor.BOLD.toString() + "Game " + (i + 1);
                        String players = ChatColor.BLUE + "Players currently in the game: " + ChatColor.RED + request.getPlayerCount();
                        String status = ChatColor.BLUE + "Status: " + ChatColor.RED + request.getStatus();
                        ItemStack buttonItem = ItemUtil.getBuilder(Material.GOLDEN_APPLE).setName(name).setLore(players, status).setAmmount(request.getPlayerCount()).build();
                        uhcContainer.addButton(new ButtonUHC(uhcContainer, buttonItem, i + 2, false));
                    }
                }
                Bukkit.getScheduler().scheduleSyncDelayedTask(HubTweaks.getInstance(), () -> {
                    uhcContainer.open(player);
                });
            });
        }
    }
}
