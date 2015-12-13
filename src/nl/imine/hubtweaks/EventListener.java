package nl.imine.hubtweaks;

import java.util.ArrayList;
import java.util.List;
import nl.imine.hubtweaks.parkour.Parkour;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class EventListener implements Listener, Runnable {

    public static void init() {
        EventListener el = new EventListener();
        HubTweaks.getInstance().getServer().getPluginManager().registerEvents(el, HubTweaks.getInstance());
        Bukkit.getScheduler().scheduleSyncRepeatingTask(HubTweaks.getInstance(), el, 1L, 1L);
    }

    public void run() {
        for (World w : Bukkit.getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (e instanceof LivingEntity) {
                    teleportSpawn(e);
                }
            }
        }
    }

    private void teleportSpawn(final Entity e) {
        if (e.getLocation().getY() <= 0) {
            e.setFallDistance(0f);
            e.teleport(e.getWorld().getSpawnLocation());
            if (e instanceof Player) {
                playerRespawn((Player) e);
            }
        }
    }

    @EventHandler
    public void onAnimalHurt(final EntityDamageEvent ede) {
        if (!(ede.getEntity() instanceof Player)) {
            if (ede instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent edebe = (EntityDamageByEntityEvent) ede;
                if (edebe.getDamager() != null && edebe.getDamager() instanceof Player) {
                    if (!((Player) edebe.getDamager()).hasPermission("iMine.hub.hurtEntity")) {
                        return;
                    }
                }
            }
            ede.setDamage(0D);
        }
    }

    @EventHandler
    public void onPlayerDisconnect(final PlayerQuitEvent pqe) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(HubTweaks.getInstance(), () -> {
            PlayerDataManager.RemovePlayerData(pqe.getPlayer());
        } , 10);
    }

    @EventHandler
    public void onPlayerItemDrop(final PlayerDropItemEvent pdie) {
        if (!pdie.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            pdie.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent pje) {
        this.playerRespawn(pje.getPlayer());
    }

    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent pre) {
        this.playerRespawn(pre.getPlayer());
    }

    private void playerRespawn(final Player pl) {
        FileConfiguration config = HubTweaks.getInstance().getConfig();
        pl.setGameMode(GameMode.ADVENTURE);
        Parkour.getInstance().getPlayer(pl).setTouchedPlate(false);
        final ItemStack item = new ItemStack(Material.COMPASS, 1);
        ItemMeta metadat = (ItemMeta) item.getItemMeta();
        List<String> list = new ArrayList<>();
        list.add(ChatColor.GOLD + "Right click to open Warp Menu");
        metadat.setLore(list);
        metadat.setDisplayName(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD.toString() + "Teleporter");
        item.setItemMeta(metadat);
        pl.closeInventory();
        Bukkit.getScheduler().scheduleSyncDelayedTask(HubTweaks.getInstance(), new Runnable() {
            @Override
            public void run() {
                pl.getInventory().clear();
                pl.getInventory().addItem(item);
                if (config.getConfigurationSection("RuleBook.Pages") != null) {
                    if (!config.getConfigurationSection("RuleBook.Pages").getKeys(false).isEmpty()) {
                        ItemStack RuleBook = new ItemStack(Material.WRITTEN_BOOK, 1);
                        BookMeta RuleBookMeta = (BookMeta) RuleBook.getItemMeta();
                        List<String> PageList = new ArrayList<>();
                        for (int Pages = 1; Pages <= config.getConfigurationSection("RuleBook.Pages").getKeys(false)
                                .size(); Pages++) {
                            PageList.add(config.getString("RuleBook.Pages." + Pages));
                        }
                        RuleBookMeta.setPages(PageList);
                        RuleBookMeta.setTitle(config.getString("RuleBook.Title"));
                        RuleBook.setItemMeta(RuleBookMeta);
                        pl.getInventory().addItem(RuleBook);
                    }
                }
            }
        }, 10L);
    }
}
