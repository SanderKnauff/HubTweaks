package nl.imine.hubtweaks.kotl;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import nl.imine.api.util.ColorUtil;
import nl.imine.api.util.LocationUtil;
import nl.imine.api.util.PlayerUtil;
import nl.imine.hubtweaks.HubTweaks;

public class KotlListener implements Listener {

    private final Kotl kotl;

    public static void init(Kotl kotl) {
        HubTweaks.getInstance().getServer().getPluginManager().registerEvents(new KotlListener(kotl),
                HubTweaks.getInstance());
    }

    private KotlListener(Kotl kotl) {
        this.kotl = kotl;

    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if ((event.getItemDrop().getItemStack() != null) && (event.getItemDrop().getItemStack().getType() != null)
                && ((event.getItemDrop().getItemStack().getType().equals(Material.GOLD_HELMET))
                        || (event.getItemDrop().getItemStack().getType().equals(Material.GOLDEN_CARROT)))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent event) {
        ItemStack is = event.getItem().getItemStack();
        if ((is != null) && (is.getType() != null)
                && ((is.getType().equals(Material.GOLD_HELMET)) || (is.getType().equals(Material.GOLDEN_CARROT)))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        for (ItemStack is : new ArrayList<>(event.getDrops())) {
            if ((is != null) && (is.getType() != null)
                    && ((is.getType().equals(Material.GOLD_HELMET)) || (is.getType().equals(Material.GOLDEN_CARROT)))) {
                event.getDrops().remove(is);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent evt) {
        Player player = evt.getPlayer();
        if (evt.getAction().equals(Action.PHYSICAL) && evt.getClickedBlock().getLocation().equals(kotl.getPlateLoc())) {
            if (kotl.getKing() == null || !kotl.getKing().isOnline()) {
                kotl.setKing(player);
                kotl.addEntropiaWandTo(player);
                if (!kotl.getKing().equals(kotl.getOldKing())) {
                    Bukkit.getOnlinePlayers().stream().forEach(pl -> PlayerUtil.sendActionMessage(pl,
                            ColorUtil.replaceColors("&6&l%s is the new king!", player.getDisplayName())));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (kotl.getKing() != null) {
            if (kotl.getKing().equals(player)) {
                if (event.getTo().distanceSquared(kotl.getPlateLoc()) > 2) {
                    kotl.setKing(null);
                    kotl.removeEntropiaWand(event.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if ((kotl.getKing() != null) && (event.getPlayer().equals(kotl.getKing()))) {
            kotl.setKing(null);
            kotl.removeEntropiaWand(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent evt) {
        if (((evt.getDamager() instanceof Player)) && ((evt.getEntity() instanceof Player))) {
            Player damager = (Player) evt.getDamager();
            if ((damager.getItemInHand().getType() != null)
                    && (damager.getItemInHand().getType().equals(Material.GOLDEN_CARROT))) {
                if (Kotl.getInstance().getKing() != null) {
                    if (Kotl.getInstance().getKing().equals(damager)
                            && LocationUtil.isInBox(evt.getEntity().getLocation(), Kotl.BOX[0], Kotl.BOX[1])) {
                        final Firework firework = (Firework) evt.getEntity().getWorld()
                                .spawnEntity(evt.getEntity().getLocation(), EntityType.FIREWORK);
                        FireworkMeta fireworkMeta = firework.getFireworkMeta();
                        fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.RED).withColor(Color.BLUE)
                                .withColor(Color.GREEN).withColor(Color.YELLOW).with(FireworkEffect.Type.BALL_LARGE)
                                .build());
                        firework.setFireworkMeta(fireworkMeta);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                firework.detonate();
                            }
                        }.runTaskLater(HubTweaks.getInstance(), 5L);
                    } else {
                        Kotl.getInstance().removeEntropiaWand(damager);
                        damager.setHealth(0);
                        damager.getLocation().getWorld().playEffect(damager.getLocation(), Effect.EXPLOSION_HUGE, 0);
                    }
                } else {
                    Kotl.getInstance().removeEntropiaWand(damager);
                    damager.setHealth(0);
                    damager.getLocation().getWorld().playEffect(damager.getLocation(), Effect.EXPLOSION_HUGE, 0);
                }
            }
        }
    }
}
