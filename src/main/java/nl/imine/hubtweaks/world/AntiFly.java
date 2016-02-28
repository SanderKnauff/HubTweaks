package nl.imine.hubtweaks.world;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import nl.imine.api.util.ColorUtil;
import nl.imine.api.util.PlayerUtil;
import nl.imine.hubtweaks.HubTweaks;

public class AntiFly {

    private Map<UUID, Integer> flyMap = new HashMap<>();

    public static void init() {
        new AntiFly();
    }

    public AntiFly() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(HubTweaks.getInstance(), () -> {
            flyMap.entrySet().stream().filter(map -> Bukkit.getPlayer(map.getKey()) != null).forEach(map -> {
                if (map.getValue() > 15) {
                    Player pl = Bukkit.getPlayer(map.getKey());
                    PlayerUtil.sendGlobalAdmin(ColorUtil.replaceColors(
                            "&l[&5FLY LOG&r&l]&r &c%s &7is now flying in &e%s&7. [&ePackets: &c%d&7]", pl.getName(),
                            pl.getWorld().getName(), map.getValue()));
                }
            });
            flyMap.clear();
        } , 0L, 20L * 3);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(HubTweaks.getInstance(), () -> {
            Bukkit.getOnlinePlayers().stream().filter(pl -> isFlying(pl)).forEach(pl -> addFly(pl));
        } , 0L, 20L / 4);
    }

    private void addFly(Player pl) {
        if (!flyMap.containsKey(pl.getUniqueId())) {
            flyMap.put(pl.getUniqueId(), 0);
        }
        flyMap.put(pl.getUniqueId(), flyMap.get(pl.getUniqueId()) + 1);
    }

    private static boolean isFlying(Player pl) {
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                for (int z = -1; z < 2; z++) {
                    if (pl.getLocation().clone().add(x, y, z).getBlock().getType() != Material.AIR) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
