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

    public ButtonBrowseUHC(Container container, ItemStack itemStack, int slot) {
        super(container, itemStack, slot);
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
    public void doAction(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(HubTweaks.getInstance(), () -> {
            Container uhcContainer = GuiManager.getInstance().createContainer("Select a lobby", 9, true, false);
            for (int i : UHCRequester.getPortalIds()) {
                PortalRequest request = UHCRequester.request(i);
                if (request.hasError()) {
                    String name = ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Game " + (i + 1)
                            + " is unavalible.";
                    ItemStack buttonItem = ItemUtil.getBuilder(Material.APPLE).setName(name).build();
                    uhcContainer.addButton(new ButtonUHC(uhcContainer, i, buttonItem, i + 2, true));
                } else {
                    String name = (request.isOpen() ? ChatColor.GREEN.toString() : ChatColor.RED.toString())
                            + ChatColor.BOLD.toString() + "Game " + (i + 1);
                    String players = ChatColor.BLUE + "Players currently in the game: " + ChatColor.RED
                            + request.getPlayerCount();
                    String status = ChatColor.BLUE + "Status: " + ChatColor.RED + request.getStatus();
                    String timer = ChatColor.BLUE + "Timer: " + ChatColor.RED
                            + (int) Math.floor(request.getTimer() / 60) + "min " + ChatColor.BLUE + "in game.";
                    if (request.isOpen()) {
                        timer = ChatColor.BLUE + "Game will begin in " + request.getTimer();
                    }
                    ItemStack buttonItem = ItemUtil.getBuilder(Material.GOLDEN_APPLE).setName(name)
                            .setLore(players, status, timer).setAmmount(request.getPlayerCount()).build();
                    uhcContainer.addButton(new ButtonUHC(uhcContainer, i, buttonItem, i + 2, false));
                }
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(HubTweaks.getInstance(), () -> {
                uhcContainer.open(player);
            });
        });
    }
}
