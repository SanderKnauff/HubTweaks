package nl.imine.hubtweaks;

import java.util.ArrayList;
import java.util.List;
import nl.imine.hubtweaks.parkour.Parkour;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class EventListener implements Listener {

    public static void init() {
        HubTweaks.getInstance().getServer().getPluginManager().registerEvents(new EventListener(), HubTweaks.getInstance());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent evt) {
        if (evt.getPlayer().getLocation().getBlockY() < 0) {
            evt.getPlayer().teleport(evt.getPlayer().getWorld().getSpawnLocation());
            Parkour.getInstance().getPlayer(evt.getPlayer()).setTouchedPlate(false);
            evt.getPlayer().setGameMode(GameMode.ADVENTURE);
        }
    }

    @EventHandler
    public void onPlayerDisconnect(final PlayerQuitEvent Event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(HubTweaks.getInstance(), () -> {
            PlayerDataManager.RemovePlayerData(Event.getPlayer());
        }, 10);
    }

    @EventHandler
    public void onPlayerItemDrop(PlayerDropItemEvent evt) {
        if (!evt.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
        this.playerRespawn(evt);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent evt) {
        this.playerRespawn(evt);
    }

    private void playerRespawn(PlayerEvent e) {
        FileConfiguration config = HubTweaks.getInstance().getConfig();
        final Player player = e.getPlayer();
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
        ItemStack item = new ItemStack(Material.COMPASS, 1);
        ItemMeta metadat = (ItemMeta) item.getItemMeta();
        List<String> list = new ArrayList<>();
        list.add(ChatColor.GOLD + "Right click to open Warp Menu");
        metadat.setLore(list);
        metadat.setDisplayName(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD.toString() + "Teleporter");
        item.setItemMeta(metadat);
        player.closeInventory();
        Bukkit.getScheduler().scheduleSyncDelayedTask(HubTweaks.getInstance(), new Runnable() {
            @Override
            public void run() {
                player.getInventory().clear();
                player.getInventory().addItem(item);
                HubTweaks.getInstance().saveConfig();
                if (config.getConfigurationSection("RuleBook.Pages") != null) {
                    if (!config.getConfigurationSection("RuleBook.Pages").getKeys(false).isEmpty()) {
                        ItemStack RuleBook = new ItemStack(Material.WRITTEN_BOOK, 1);
                        BookMeta RuleBookMeta = (BookMeta) RuleBook.getItemMeta();
                        List<String> PageList = new ArrayList<>();
                        for (int Pages = 1; Pages <= config.getConfigurationSection("RuleBook.Pages").getKeys(false).size(); Pages++) {
                            PageList.add(config.getString("RuleBook.Pages." + Pages));
                        }
                        RuleBookMeta.setPages(PageList);
                        RuleBookMeta.setTitle(config.getString("RuleBook.Title"));
                        RuleBook.setItemMeta(RuleBookMeta);
                        player.getInventory().addItem(RuleBook);
                    }
                }
            }
        }, 10L);
    }
}
