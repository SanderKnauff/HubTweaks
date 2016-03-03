package nl.imine.hubtweaks.world;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffectType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import nl.imine.api.util.ColorUtil;
import nl.imine.api.util.LocationUtil.Position;
import nl.imine.api.util.PlayerUtil;
import nl.imine.hubtweaks.HubTweaks;

public class AntiFly implements Listener {

    private static final int TIME_FLYING_SEC = 3;
    private static final int CHECK_TIMES_SEC = 10;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String TEMP = HubTweaks.getInstance().getDataFolder().getAbsolutePath() + File.separator
            + "tmp" + File.separator;

    private Map<UUID, Path> flyMap = new HashMap<>();

    public static void init() {
        new AntiFly();
    }

    public AntiFly() {
        Bukkit.getPluginManager().registerEvents(this, HubTweaks.getInstance());
        Bukkit.getScheduler().scheduleSyncRepeatingTask(HubTweaks.getInstance(), () -> {
            flyMap.entrySet().stream().filter(map -> Bukkit.getPlayer(map.getKey()) != null).forEach(map -> {
                Player pl = Bukkit.getPlayer(map.getKey());
                // is falling
                if (map.getValue().getFirstPosition().getY() > pl.getLocation().getY()) {
                    return;
                }
                // meer dan x ticks aan t vliegen
                if (map.getValue().getTimes() > 15) {
                    PlayerUtil.sendGlobalAdmin(ColorUtil.replaceColors(
                            "&l[&5&lFLY LOG&r&l]&r &c%s &7is now flying in &e%s&7. [Packets: &c%d&7]", pl.getName(),
                            pl.getWorld().getName(), map.getValue().getTimes()));
                    try {
                        FileUtils.write(new File(TEMP + "FLY" + new Date().toString() + " FROM "
                                + map.getKey().toString() + ".Path.gson"), GSON.toJson(map.getValue()));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            flyMap.clear();
        } , 0L, 20L * TIME_FLYING_SEC);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(HubTweaks.getInstance(), () -> {
            Bukkit.getOnlinePlayers().stream().filter(pl -> isFlying(pl)).forEach(pl -> addFly(pl));
        } , 0L, 20L / CHECK_TIMES_SEC);
    }

    private void addFly(Player pl) {
        if (!flyMap.containsKey(pl.getUniqueId())) {
            flyMap.put(pl.getUniqueId(), new Path());
        }
        flyMap.get(pl.getUniqueId()).addPos(pl.getLocation());
    }

    public void resetFly(Player pl) {
        flyMap.remove(pl.getUniqueId());
    }

    private static boolean isFlying(Player pl) {
        for (int x = -1; x < 2; x++) {
            for (int y = -2; y < 0; y++) {
                for (int z = -1; z < 2; z++) {
                    if (pl.getLocation().clone().add(x, y, z).getBlock().getType() != Material.AIR) {
                        return false;
                    }
                }
            }
        }
        if (pl.hasPotionEffect(PotionEffectType.LEVITATION) || /*pl.getAllowFlight() || */pl.getVehicle() != null
                || (pl.getInventory().getChestplate() != null
                        && pl.getInventory().getChestplate().getType() == Material.ELYTRA)) {
            return false;
        }
        return true;
    }

    @EventHandler
    public void onTP(PlayerTeleportEvent pte) {
        resetFly(pte.getPlayer());
    }

    @EventHandler
    public void onTP(PlayerRespawnEvent pre) {
        resetFly(pre.getPlayer());
    }

    private static class Path {
        // Because lagg, 1 sec more time
        private Position[] positions = new Position[AntiFly.CHECK_TIMES_SEC * (AntiFly.TIME_FLYING_SEC + 1)];
        private int index = 0;

        public void addPos(Location loc) {
            positions[index++] = new Position(loc);
        }

        public int getTimes() {
            return index;
        }

        public Position getFirstPosition() {
            return positions[0];
        }
    }
}
