/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.imine.hubtweaks.parkour;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Wool;

import net.md_5.bungee.api.ChatColor;
import nl.imine.api.util.ColorUtil;
import nl.imine.api.util.ItemUtil;
import nl.imine.api.util.LocationUtil;
import nl.imine.api.util.PlayerUtil;
import nl.imine.hubtweaks.HubTweaks;
import nl.imine.hubtweaks.Statistic;

/**
 *
 * @author Sander
 */
public class ParkourListener implements Listener {

    public static void init() {
        HubTweaks.getInstance().getServer().getPluginManager().registerEvents(new ParkourListener(),
                HubTweaks.getInstance());
    }

    @EventHandler
    public void onPlayerPlateInteract(PlayerInteractEvent evt) {
        Parkour parkour = Parkour.getInstance();
        if (evt.getAction().equals(Action.PHYSICAL)) {
            if (evt.getClickedBlock().getRelative(BlockFace.DOWN).getType().equals(Material.WOOL)) {
                ParkourPlayer player = parkour.getPlayer(evt.getPlayer());
                if (player != null) {
                    // Als de player het parkour aan het doen is (zonder te
                    // bouwen dus).
                    if (!player.isBuilding()) {
                        if (parkour.getLevel(
                                ((Wool) evt.getClickedBlock().getRelative(BlockFace.DOWN).getState().getData())
                                        .getColor())
                                .getLevel() > player.getLevel().getLevel()) {
                            player.setLevel(parkour.getLevel(
                                    ((Wool) evt.getClickedBlock().getRelative(BlockFace.DOWN).getState().getData())
                                            .getColor()));
                            player.save();
                        }
                        if (parkour.getLevel(
                                ((Wool) evt.getClickedBlock().getRelative(BlockFace.DOWN).getState().getData())
                                        .getColor())
                                .getLevel() == 5) {
                            if (!player.hasReachedTop()) {
                                player.setReachedTop(true);
                                Statistic.addToParkour(evt.getPlayer());
                                String mssg;
                                if (!player.hasTouchedPlate()) {
                                    player.setLevel(parkour.getLevel(DyeColor.MAGENTA));
                                    mssg = "&c&l%s &r&5has reached the end of the parkour!";
                                    player.save();
                                    for (int i = 0; i < 10; i++) {
                                        Bukkit.getScheduler().scheduleSyncDelayedTask(HubTweaks.getInstance(), () -> {
                                            LocationUtil.firework(evt.getPlayer().getLocation(),
                                                    FireworkEffect.builder().with(Type.BALL_LARGE)
                                                            .withColor(Color.PURPLE).withFade(Color.GREEN, Color.LIME,
                                                                    Color.YELLOW, Color.ORANGE, Color.RED)
                                                            .build(),
                                                    20L);
                                        } , 20 * (i + 5));
                                    }
                                } else {
                                    mssg = "&c&l%s &r&6 has reached the end of the parkour!";
                                }
                                Bukkit.getOnlinePlayers().stream().forEach(pl -> PlayerUtil.sendActionMessage(pl,
                                        ColorUtil.replaceColors(mssg, evt.getPlayer().getName())));
                            }
                        }
                        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
                        LeatherArmorMeta meta = (LeatherArmorMeta) boots.getItemMeta();
                        meta.setColor(player.getLevel().getColor().getColor());
                        if (player.getLevel().getColor() == DyeColor.MAGENTA) {
                           //TODO: player.getPlayer().getInventory().setChestplate(new ItemStack(ItemUtil.getBuilder(Material.ELYTRA).build()));
                        }
                        boots.setItemMeta(meta);
                        if (player.getLevel().getLevel() != -1) {
                            evt.getPlayer().getInventory().setBoots(boots);
                        } else {
                            evt.getPlayer().teleport(
                                    new Location(evt.getPlayer().getWorld(), 74.5D, 36D, -504.5D, 140.3F, -27.3F));
                            evt.getPlayer().sendMessage(
                                    ChatColor.GOLD + "You have not reached any parkour checkpoints yet. Start here.");
                        }
                        player.setTouchedPlate(true);
                        // Als de player het parkour aan het maken is.
                    } else {
                        if (parkour.getLevel(
                                ((Wool) evt.getClickedBlock().getRelative(BlockFace.DOWN).getState().getData())
                                        .getColor())
                                .equals(new ParkourLevel(-1, DyeColor.BLACK))) {
                            File f = new File(ParkourConfig.CONFIGPATH + "Levels.yml");
                            YamlConfiguration config = new YamlConfiguration();
                            try {
                                config.load(f);
                                config.set((parkour.getLevels().size() + 1) + ".Color",
                                        ((Wool) evt.getClickedBlock().getRelative(BlockFace.DOWN).getState().getData())
                                                .getColor().toString());
                                config.set(-1 + ".Color", DyeColor.BLACK.name());
                                config.save(f);
                            } catch (FileNotFoundException e) {
                                System.err.println("Exception finding file: " + f.getPath() + " || " + e.getMessage());
                            } catch (IOException e) {
                                System.err.println("Exception opening file: " + f.getPath() + " || " + e.getMessage());
                            } catch (InvalidConfigurationException e) {
                                System.err
                                        .println("Exception reading ymlfile: " + f.getPath() + " || " + e.getMessage());
                            }
                            parkour.addLevel(new ParkourLevel(parkour.getLevels().size() + 1,
                                    ((Wool) evt.getClickedBlock().getRelative(BlockFace.DOWN).getState().getData())
                                            .getColor()));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent evt) {
        ParkourPlayer p = Parkour.getInstance().getPlayer(evt.getPlayer());
        if (Parkour.getInstance().getPlayer(evt.getPlayer()) == null) {
            p = new ParkourPlayer(evt.getPlayer().getUniqueId().toString(), new ParkourLevel(-1, DyeColor.BLACK));
            Parkour.getInstance().addPlayer(p);
        }
        p.setReachedTop(false);
        p.setTouchedPlate(false);
    }

    @EventHandler
    public void changeMode(PlayerInteractEvent evt) {
        if (evt.getAction().equals(Action.PHYSICAL)) {
            if (evt.getClickedBlock().getRelative(BlockFace.DOWN).getType().equals(Material.BARRIER)) {
                if (evt.getPlayer().hasPermission("ht.createParkour")) {
                    Parkour.getInstance().getPlayer(evt.getPlayer())
                            .setBuilding(!Parkour.getInstance().getPlayer(evt.getPlayer()).isBuilding());
                    evt.getPlayer()
                            .sendMessage("Building: " + Parkour.getInstance().getPlayer(evt.getPlayer()).isBuilding());
                }
            }
        }
    }

    // PARKOUR ANTICHEAT DOWN HERE
    public static void resetCheat(Player pl) {
        ParkourPlayer pp = Parkour.getInstance().getPlayer(pl);
        pp.setTouchedPlate(false);
        pp.setReachedTop(false);
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent evt) {
        if (!evt.getNewGameMode().equals(GameMode.ADVENTURE)) {
            ParkourPlayer pp = Parkour.getInstance().getPlayer(evt.getPlayer());
            pp.setTouchedPlate(true);
            pp.setReachedTop(true);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent evt) {
        if (evt.getTo().getBlockY() > 40) {
            ParkourPlayer pp = Parkour.getInstance().getPlayer(evt.getPlayer());
            pp.setTouchedPlate(true);
            pp.setReachedTop(true);
        }
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent evt) {
        ParkourPlayer pp = Parkour.getInstance().getPlayer(evt.getPlayer());
        pp.setTouchedPlate(true);
        pp.setReachedTop(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent evt) {
        resetCheat(evt.getEntity());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent evt) {
        if (!evt.getPlayer().getActivePotionEffects().isEmpty()) {
            ParkourPlayer pp = Parkour.getInstance().getPlayer(evt.getPlayer());
            pp.setTouchedPlate(true);
            pp.setReachedTop(true);
        }
    }
}
