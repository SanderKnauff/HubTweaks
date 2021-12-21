package nl.imine.hubtweaks.buttongame;

import nl.imine.api.gui.Button;
import nl.imine.hubtweaks.HubTweaksPlugin;
import nl.imine.hubtweaks.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Switch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ButtonGame implements Listener {

    private static final List<Material> colors = List.of(Material.LIME_CONCRETE, Material.MAGENTA_CONCRETE, Material.RED_CONCRETE, Material.YELLOW_CONCRETE, Material.LIGHT_BLUE_CONCRETE);

    private final Map<Location, Material> originalBlocks;
    private int guesses;
    private Material wrongChoice;

    public ButtonGame() {
        this.originalBlocks = new HashMap<>();
        this.guesses = 0;
        this.wrongChoice = colors.get(ThreadLocalRandom.current().nextInt(colors.size()));
    }

    private void reset() {
        this.originalBlocks.forEach((key, value) -> key.getBlock().setType(value));
        this.originalBlocks.clear();
        this.wrongChoice = colors.get(ThreadLocalRandom.current().nextInt(colors.size()));
        this.guesses = 0;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || !event.getClickedBlock().getType().equals(Material.POLISHED_BLACKSTONE_BUTTON) || !(event.getClickedBlock().getBlockData() instanceof Switch data)) {
            return;
        }

        final var choiceBlock = switch (data.getAttachedFace()) {
            case WALL -> event.getClickedBlock().getRelative(data.getFacing().getOppositeFace());
            case FLOOR -> event.getClickedBlock().getRelative(BlockFace.DOWN);
            case CEILING -> event.getClickedBlock().getRelative(BlockFace.UP);
        };

        if (!colors.contains(choiceBlock.getType())) {
            return;
        }

        if (choiceBlock.getType().equals(wrongChoice)) {
            event.getPlayer().damage(100d);
            event.getPlayer().getWorld().spawnParticle(Particle.EXPLOSION_HUGE, event.getPlayer().getLocation(), 5);
            event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 10, 0.5f);
            reset();
            return;
        }

        guesses++;
        if (guesses == colors.size() - 1) {
            reset();
            for (int i = 0; i < 5; i++) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(HubTweaksPlugin.class), () -> LocationUtil.firework(
                    event.getPlayer().getLocation(),
                    FireworkEffect.builder()
                        .with(FireworkEffect.Type.BALL_LARGE)
                        .withColor(Color.PURPLE)
                        .withFade(Color.GREEN, Color.LIME, Color.YELLOW, Color.ORANGE, Color.RED)
                        .build(),
                    20L), 20L * i);
                Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(HubTweaksPlugin.class), this::reset, 60L);
            }
        }

        this.originalBlocks.put(choiceBlock.getLocation(), choiceBlock.getType());
        choiceBlock.setType(Material.BLACK_CONCRETE);
    }
}
