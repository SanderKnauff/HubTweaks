package nl.imine.hubtweaks.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;

import nl.imine.hubtweaks.HubTweaks;

public class Spawner implements Runnable {

    private static final long DELAY = 20L * 60L * 5L;

    private Map<Chunk, List<LivingEntity>> mapping;

    public static void init() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(HubTweaks.getInstance(), new Spawner(), DELAY, DELAY);
    }

    @Override
    public void run() {
        mapping = new HashMap<>();
        for (World w : Bukkit.getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (!(e instanceof LivingEntity)) {
                    continue;
                }
                Chunk c = e.getLocation().getChunk();
                if (mapping.get(c) == null) {
                    mapping.put(c, new ArrayList<LivingEntity>());
                }
                mapping.get(c).add((LivingEntity) e);
            }
        }
        for (List<LivingEntity> entry : mapping.values()) {
            if (entry.size() > 100) {
                for (int i = 0; i < entry.size() - 100; i++) {
                    LivingEntity le = entry.get(i);
                    EntityDeathEvent ede = new EntityDeathEvent(le, new ArrayList<>());
                    Bukkit.getPluginManager().callEvent(ede);
                    entry.get(i).remove();
                }
            }
        }
        mapping = null;
    }
}
