package nl.imine.hubtweaks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
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
import org.bukkit.plugin.Plugin;

public class EventListener implements Listener {

    private final Plugin plugin;

    public static void init(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new EventListener(plugin), plugin);
    }

    private EventListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if(e.getPlayer().getLocation().getBlockY() < 0){
            e.getPlayer().setHealth(0);
        }
    }
    
    @EventHandler
    public void onPlayerDisconnect(final PlayerQuitEvent Event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(HubTweaks.getInstance(), () -> {
            PlayerDataManager.RemovePlayerData(Event.getPlayer());
            HubTweaks.pmc.remove(Event.getPlayer());
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
        Player player = e.getPlayer();
        player.getInventory().clear();
        ItemStack item = new ItemStack(Material.COMPASS, 1);
        ItemMeta metadat = (ItemMeta) item.getItemMeta();
        List<String> list = new ArrayList<>();
        list.add(ChatColor.GOLD + "Right click to open Warp Menu");
        metadat.setLore(list);
        metadat.setDisplayName(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD.toString() + "Teleporter");
        item.setItemMeta(metadat);
        player.getInventory().clear();
        player.getInventory().addItem(item);
        HubTweaks.pmc.put(e.getPlayer(), 0);
        int lvl = 1;
        lvl = plugin.getConfig().getInt("PlayerData." + player.getUniqueId() + ".ParkourLvl");
        HubTweaks.ppl.put(player, lvl);
        plugin.saveConfig();
        if (plugin.getConfig().getConfigurationSection("RuleBook.Pages") != null) {
            if (!plugin.getConfig().getConfigurationSection("RuleBook.Pages").getKeys(false).isEmpty()) {
                ItemStack RuleBook = new ItemStack(Material.WRITTEN_BOOK, 1);
                BookMeta RuleBookMeta = (BookMeta) RuleBook.getItemMeta();
                List<String> PageList = new ArrayList<String>();
                for (int Pages = 1; Pages <= plugin.getConfig().getConfigurationSection("RuleBook.Pages").getKeys(false).size(); Pages++) {
                    PageList.add(plugin.getConfig().getString("RuleBook.Pages." + Pages));
                    // RuleBookMeta.setPage(Pages,
                    // plugin.getConfig().getString("RuleBook.Pages." + Pages));
                }
                RuleBookMeta.setPages(PageList);
                RuleBookMeta.setTitle(plugin.getConfig().getString("RuleBook.Title"));
                RuleBook.setItemMeta(RuleBookMeta);
                player.getInventory().addItem(RuleBook);
            }
        }
    }
}
