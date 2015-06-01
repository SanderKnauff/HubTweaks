package nl.imine.hubtweaks.pvp;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class PvPListener implements Listener {

    private final Plugin plugin;

    public static void init(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new PvPListener(plugin), plugin);
    }

    private PvPListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoinArena(PvPJoinEvent E) {
        final Player p = E.getPlayer();
        if (!PvP.getSpawnList().isEmpty()) {
            PvP.addPlayerToArena(p);
            PvP.addGear(p);
            p.teleport(PvP.getRandomSpawn());
            p.addPotionEffect(PotionEffectType.BLINDNESS.createEffect((int) 100, 0));
            /*
             * for (Player enemy : PvP.getPlayerList()) { enemy.hidePlayer(p);
             * p.hidePlayer(enemy);
             * plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
             * new Runnable() { public void run() { for (Player enemy :
             * PvP.getPlayerList()) { enemy.showPlayer(p); p.showPlayer(p); } }
             * }, 100L); }
             */
        } else {
            p.sendMessage(ChatColor.DARK_RED + "ERROR: Warp aborted due to no spawns avalible");
        }
    }

    @EventHandler
    public void onPvPDamage(EntityDamageByEntityEvent E) {
        if (E.getDamager() instanceof Player) {
            if (PvP.getPlayerList().contains((Player) E.getDamager())) {
                if (E.getDamage() > 0) {
                    Location loc = new Location(E.getEntity().getLocation().getWorld(), E.getEntity().getLocation().getX() + 0.5, E.getEntity().getLocation().getY() + 0.5, E.getEntity().getLocation().getZ() + 0.5);
                    plugin.getServer().getWorld(E.getDamager().getLocation().getWorld().getName()).playEffect(loc, Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
                }
            }
        }
        if (E.getEntity() instanceof Player && E.getDamager().getType().equals(EntityType.ARROW)) {
            Player p = (Player) E.getEntity();
            Arrow arrow = (Arrow) E.getDamager();
            if (arrow.getShooter() instanceof Player) {
                Player a = (Player) arrow.getShooter();
                if (PvP.getPlayerList().contains(p) && PvP.getPlayerList().contains(a) && a != p) {
                    if (a.getInventory().all(Material.ARROW).size() != 0) {
                        int ArrowCount = 0;
                        for (int i = 0; i < a.getInventory().all(Material.ARROW).size(); i++) {
                            if (a.getInventory().all(Material.ARROW).get(i) != null) {
                                if (a.getInventory().all(Material.ARROW).get(i).getType().equals(Material.ARROW)) {
                                    ArrowCount += a.getInventory().all(Material.ARROW).get(i).getAmount();
                                    if (ArrowCount < 8) {
                                        a.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                                    }
                                }
                            }
                        }
                    } else {
                        a.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                    }
                    if (a.hasPermission("coins.vip")) {
                        p.sendMessage(ChatColor.WHITE + "You have been killed by: '" + ChatColor.GOLD + a.getName() + ChatColor.WHITE + "'");
                    } else {
                        p.sendMessage(ChatColor.WHITE + "You have been killed by: '" + ChatColor.GRAY + a.getName() + ChatColor.WHITE + "'");
                    }
                    if (p.hasPermission("coins.vip")) {
                        a.sendMessage(ChatColor.WHITE + "You killed: '" + ChatColor.GOLD + p.getName() + ChatColor.WHITE + "'");
                    } else {
                        a.sendMessage(ChatColor.WHITE + "You killed: '" + ChatColor.GRAY + p.getName() + ChatColor.WHITE + "'");
                    }
                    p.setHealth(0.0D);
                    arrow.remove();
                    E.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent E) {
        if (E.getEntity().getKiller() instanceof Player && E.getEntity() instanceof Player) {
            Player p = (Player) E.getEntity();
            Player a = (Player) E.getEntity().getKiller();
            if (PvP.getPlayerList().contains(p) && PvP.getPlayerList().contains(a) && a.getItemInHand().getType() != null) {
                PvP.removePlayerFromArena(p);
                if (a.getInventory().all(Material.ARROW).size() != 0) {
                    int ArrowCount = 0;
                    for (int i = 0; i < a.getInventory().all(Material.ARROW).size(); i++) {
                        if (a.getInventory().all(Material.ARROW).get(i) != null) {
                            if (a.getInventory().all(Material.ARROW).get(i).getType().equals(Material.ARROW)) {
                                ArrowCount += a.getInventory().all(Material.ARROW).get(i).getAmount();
                                if (ArrowCount < 8) {
                                    a.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                                }
                            }
                        }
                    }
                } else {
                    a.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                }
                if (a.hasPermission("coins.vip")) {
                    p.sendMessage(ChatColor.WHITE + "You have been killed by: '" + ChatColor.GOLD + a.getName() + ChatColor.WHITE + "'");
                } else {
                    p.sendMessage(ChatColor.WHITE + "You have been killed by: '" + ChatColor.GRAY + a.getName() + ChatColor.WHITE + "'");
                }
                if (p.hasPermission("coins.vip")) {
                    a.sendMessage(ChatColor.WHITE + "You killed: '" + ChatColor.GOLD + p.getName() + ChatColor.WHITE + "'");
                } else {
                    a.sendMessage(ChatColor.WHITE + "You killed: '" + ChatColor.GRAY + p.getName() + ChatColor.WHITE + "'");
                }
            }

        }
        List<ItemStack> removeDrops = new ArrayList<ItemStack>();
        for (ItemStack drop : E.getDrops()) {
            removeDrops.add(drop);
        }
        E.getDrops().removeAll(removeDrops);
        E.setDroppedExp(0);
        E.setDeathMessage(null);
        if (PvP.getPlayerList().contains(E.getEntity())) {
            PvP.getPlayerList().remove(E.getEntity());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent E) {
        if (PvP.getPlayerList().contains(E.getPlayer())) {
            Vector loc = new Vector(E.getPlayer().getLocation().getBlockX(), E.getPlayer().getLocation().getBlockY(), E.getPlayer().getLocation().getBlockZ());
            if (!getWorldGuard().getRegionManager(E.getPlayer().getWorld()).getApplicableRegionsIDs(loc).contains(getWorldGuard().getRegionManager(E.getPlayer().getWorld()).getRegion("PvP").getId())) {
                E.getPlayer().setHealth(0.0D);
            }
        }
    }

    @EventHandler
    public void onPlayerItemDrop(PlayerDropItemEvent E) {
        if (PvP.getPlayerList().contains(E.getPlayer())) {
            E.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerItemPickup(PlayerPickupItemEvent E) {
        if (PvP.getPlayerList().contains(E.getPlayer())) {
            E.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerIventoryChange(InventoryClickEvent E) {
        if (PvP.getPlayerList().contains((Player) E.getWhoClicked())) {
            E.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent E) {
        if (PvP.getPlayerList().contains(E.getPlayer())) {
            PvP.removePlayerFromArena(E.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent E) {
        if (PvP.getPlayerList().contains(E.getPlayer())) {
            PvP.removePlayerFromArena(E.getPlayer());
        }
    }

    private WorldGuardPlugin getWorldGuard() {
        Plugin wg = plugin.getServer().getPluginManager().getPlugin("WorldGuard");

        // WorldGuard may not be loaded
        if (wg == null || !(wg instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldGuardPlugin) wg;
    }

}

/*
 * 
 * NAME COLOR
 */
