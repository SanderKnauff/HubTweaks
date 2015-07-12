/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.imine.hubtweaks.parkour;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import nl.imine.hubtweaks.HubTweaks;
import nl.imine.hubtweaks.util.Log;
import nl.imine.hubtweaks.util.Messenger;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Wool;

/**
 *
 * @author Sander
 */
public class ParkourListener implements Listener {

    public static void init() {
        HubTweaks.getInstance().getServer().getPluginManager().registerEvents(new ParkourListener(), HubTweaks.getInstance());
    }

    @EventHandler
    public void onPlayerPlateInteract(PlayerInteractEvent evt) {
        Parkour parkour = Parkour.getInstance();
        if (evt.getAction().equals(Action.PHYSICAL)) {
            if (evt.getClickedBlock().getRelative(BlockFace.DOWN).getType().equals(Material.WOOL)) {
                ParkourPlayer player = parkour.getPlayer(evt.getPlayer());
                if (player != null) {
                    if (!player.isBuilding()) {
                        if (parkour.getLevel(((Wool) evt.getClickedBlock().getRelative(BlockFace.DOWN).getState().getData()).getColor()).getLevel() > player.getLevel().getLevel()) {
                            player.setLevel(parkour.getLevel(((Wool) evt.getClickedBlock().getRelative(BlockFace.DOWN).getState().getData()).getColor()));
                            player.save();
                            if(parkour.getLevel(((Wool) evt.getClickedBlock().getRelative(BlockFace.DOWN).getState().getData()).getColor()).getLevel() == 5){
                                Messenger.sendActionMessageToAll(evt.getPlayer().getName() + " has reached the end of the parkour!");
                            }
                        }
                        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
                        LeatherArmorMeta meta = (LeatherArmorMeta) boots.getItemMeta();
                        meta.setColor(player.getLevel().getColor().getColor());
                        boots.setItemMeta(meta);
                        if(player.getLevel().getLevel() != -1){
                            evt.getPlayer().getInventory().setBoots(boots);  
                        }
                    } else {
                        if (parkour.getLevel(((Wool) evt.getClickedBlock().getRelative(BlockFace.DOWN).getState().getData()).getColor()).equals(new ParkourLevel(-1, DyeColor.BLACK))) {
                            File f = new File(ParkourConfig.CONFIGPATH + "Levels.yml");
                            YamlConfiguration config = new YamlConfiguration();
                            try {
                                config.load(f);
                                config.set((parkour.getLevels().size() + 1) + ".Color", ((Wool) evt.getClickedBlock().getRelative(BlockFace.DOWN).getState().getData()).getColor().toString());
                                config.set(-1 + ".Color", DyeColor.BLACK.name());
                                config.save(f);
                            } catch (FileNotFoundException e) {
                                Log.warning("Exception finding file: " + f.getPath() + " || " + e.getMessage());
                            } catch (IOException e) {
                                Log.warning("Exception opening file: " + f.getPath() + " || " + e.getMessage());
                            } catch (InvalidConfigurationException e) {
                                Log.warning("Exception reading ymlfile: " + f.getPath() + " || " + e.getMessage());
                            }
                            parkour.addLevel(new ParkourLevel(parkour.getLevels().size() + 1, ((Wool) evt.getClickedBlock().getRelative(BlockFace.DOWN).getState().getData()).getColor()));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent evt) {
        if (Parkour.getInstance().getPlayer(evt.getPlayer()) == null) {
            Parkour.getInstance().addPlayer(new ParkourPlayer(evt.getPlayer().getUniqueId().toString(), new ParkourLevel(-1, DyeColor.BLACK)));
        }
    }

    @EventHandler
    public void changeMode(PlayerInteractEvent evt) {
        if (evt.getAction().equals(Action.PHYSICAL)) {
            if (evt.getClickedBlock().getRelative(BlockFace.DOWN).getType().equals(Material.BARRIER)) {
                if (evt.getPlayer().hasPermission("ht.createParkour")) {
                    Parkour.getInstance().getPlayer(evt.getPlayer()).setBuilding(!Parkour.getInstance().getPlayer(evt.getPlayer()).isBuilding());
                    evt.getPlayer().sendMessage("Building: " + Parkour.getInstance().getPlayer(evt.getPlayer()).isBuilding());
                }
            }
        }

    }
}
