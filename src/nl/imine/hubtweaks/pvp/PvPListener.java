package nl.imine.hubtweaks.pvp;

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

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import nl.imine.hubtweaks.HubTweaks;
import org.bukkit.event.entity.ProjectileHitEvent;
import nl.imine.hubtweaks.Statistic;

public class PvPListener implements Listener {

    public static void init() {
        HubTweaks.getInstance().getServer().getPluginManager().registerEvents(new PvPListener(), HubTweaks.getInstance());
    }

    @EventHandler
    public void onPlayerJoinArena(PvPJoinEvent evt) {
        Player player = evt.getPlayer();
        if (!PvP.getSpawnList().isEmpty()) {
            PvP.addPlayerToArena(player);
        } else {
            player.sendMessage(ChatColor.DARK_RED + "ERROR: Warp aborted due to no spawns avalible");
        }
    }

    @EventHandler
    public void onPvPDamage(EntityDamageByEntityEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            if (PvP.getPlayerList().contains(player)) {
                if (evt.getDamager() instanceof Player) {
                    if (PvP.getPlayerList().contains((Player) evt.getDamager())) {
                        if (evt.getDamage() > 0) {
                            Location loc = new Location(evt.getEntity().getLocation().getWorld(), evt.getEntity().getLocation().getX() + 0.5, evt.getEntity().getLocation().getY() + 0.5, evt.getEntity().getLocation().getZ() + 0.5);
                            HubTweaks.getInstance().getServer().getWorld(evt.getDamager().getLocation().getWorld().getName()).playEffect(loc, Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
                        }
                    }
                }
                if (evt.getEntity() instanceof Player && evt.getDamager().getType().equals(EntityType.ARROW)) {
                    Arrow arrow = (Arrow) evt.getDamager();
                    if (arrow.getShooter() instanceof Player) {
                        Player attacker = (Player) arrow.getShooter();
                        if (PvP.getPlayerList().contains(player) && PvP.getPlayerList().contains(attacker) && attacker != player) {
                            player.damage(player.getHealth(), attacker);
                            arrow.remove();
                        }
                    }
                    evt.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent evt) {
        if (evt.getEntity().getKiller() instanceof Player && evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            Player killer = (Player) evt.getEntity().getKiller();
            if (PvP.getPlayerList().contains(player) && PvP.getPlayerList().contains(killer) && killer.getItemInHand().getType() != null) {
                PvP.removePlayerFromArena(player);
                if (killer.getInventory().all(Material.ARROW).isEmpty()) {
                    killer.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                }
                player.sendMessage(ChatColor.WHITE + "You have been killed by: '" + ChatColor.GRAY + killer.getName() + ChatColor.WHITE + "'");
                killer.sendMessage(ChatColor.WHITE + "You killed: '" + ChatColor.GRAY + player.getName() + ChatColor.WHITE + "'");
            }
            Statistic.addToKill(killer);
        }
        evt.getDrops().clear();
        evt.setDroppedExp(0);
        evt.setDeathMessage(null);
        if (PvP.getPlayerList().contains(evt.getEntity())) {
            PvP.getPlayerList().remove(evt.getEntity());
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent evt) {
        if (evt.getEntity().getShooter() instanceof Player) {
            evt.getEntity().remove();
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
        Plugin wg = HubTweaks.getInstance().getServer().getPluginManager().getPlugin("WorldGuard");
        if (wg == null || !(wg instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) wg;
    }
}
