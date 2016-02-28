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

    private Map<UUID, Integer[]> flyMap = new HashMap<>();

    public static void init() {
        new AntiFly();
    }

    public AntiFly() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(HubTweaks.getInstance(), () -> {
            flyMap.entrySet().stream().filter(map -> Bukkit.getPlayer(map.getKey()) != null).forEach(map -> {
                Player pl = Bukkit.getPlayer(map.getKey());
                // is falling
                if (map.getValue()[1] - 30 > pl.getLocation().getY()) {
                    return;
                }
                // meer dan 40ticks aan t vliegen
                if (map.getValue()[0] >= 10) {
                    PlayerUtil.sendGlobalAdmin(ColorUtil.replaceColors(
                            "&l[&5&lFLY LOG&r&l]&r &c%s &7is now flying in &e%s&7. [Packets: &c%d&7]", pl.getName(),
                            pl.getWorld().getName(), map.getValue()[0] * 4));
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
            flyMap.put(pl.getUniqueId(), new Integer[] { 0, pl.getLocation().getBlockY() });
        }
        Integer[] map = flyMap.get(pl.getUniqueId());
        map[0] = map[0] + 1;
        flyMap.put(pl.getUniqueId(), map);
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
        if (pl.getAllowFlight() || pl.getVehicle() != null) {
            return false;
        }
        return true;
    }

}
