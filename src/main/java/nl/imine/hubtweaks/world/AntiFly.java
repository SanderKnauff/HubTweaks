package nl.imine.hubtweaks.world;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import nl.imine.api.util.ColorUtil;
import nl.imine.api.util.PlayerUtil;
import nl.imine.hubtweaks.HubTweaks;

public class AntiFly implements Listener {

    private Map<UUID, Integer> flyMap = new HashMap<>();
    private boolean scan = false;

    public static void init() {
        new AntiFly();
    }

    public AntiFly() {
        Bukkit.getPluginManager().registerEvents(this, HubTweaks.getInstance());
        Bukkit.getScheduler().scheduleSyncRepeatingTask(HubTweaks.getInstance(), () -> {
            flyMap.clear();
            scan = true;
        } , 0L, 20L * 3);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent pme) {
        if (pme.getFrom().getY() < pme.getTo().getY() && !pme.getPlayer().getAllowFlight() && scan) {
            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 1; y++) {
                    for (int z = -1; z < 2; z++) {
                        if (pme.getFrom().clone().add(x, y, z).getBlock().getType() != Material.AIR) {
                            return;
                        }
                    }
                }
            }
            UUID uuid = pme.getPlayer().getUniqueId();
            if (!flyMap.containsKey(uuid)) {
                flyMap.put(uuid, 0);
            }
            flyMap.put(uuid, flyMap.get(uuid) + 1);
            if (flyMap.get(uuid) > 20) {
                PlayerUtil
                        .sendGlobalAdmin(ColorUtil.replaceColors("&l[&5FLY LOG&r&l]&r &c%s &7is now flying in &e%s&7. [&c%d&7]",
                                pme.getPlayer().getName(), pme.getFrom().getWorld().getName(), flyMap.get(uuid)));
            }
        } else if (flyMap.containsKey(pme.getPlayer().getUniqueId())) {
            flyMap.put(pme.getPlayer().getUniqueId(), 0);
        }
    }
}
