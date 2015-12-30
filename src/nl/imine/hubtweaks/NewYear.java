package nl.imine.hubtweaks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import nl.imine.api.util.ItemUtil;

public class NewYear implements Listener, Runnable {

    private static final long DELAY = 20 * 60;
    private static final Random RAND = new Random();

    public NewYear() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(HubTweaks.getInstance(), this, 0L, DELAY);
    }

    private static ItemStack generateFirework() {
        ItemStack is = ItemUtil.getBuilder(Material.FIREWORK).addFlag(ItemFlag.HIDE_ATTRIBUTES)
                .addFlag(ItemFlag.HIDE_POTION_EFFECTS)
                .setName(MKTUtilsTemp.rainbow(MKTUtilsTemp.repeat(RAND.nextInt(3), " ") + "FIREWORKZ")).build();
        FireworkMeta fem = (FireworkMeta) is.getItemMeta();
        fem.setLore(MKTUtilsTemp.toList(
                new String[] { ChatColor.GRAY + "Happy new year", MKTUtilsTemp.rainbow("From the iMine admin team!"),
                        "" + ChatColor.GRAY + ChatColor.STRIKETHROUGH + "Behalve van tijmen" }));
        for (int i = 0; i < RAND.nextInt(3) + 1; i++) {
            fem.addEffect(randomEffect());
        }
        is.setItemMeta(fem);
        return is;
    }

    private static FireworkEffect randomEffect() {
        int cs = RAND.nextInt(2) + 1;
        Color[] colors = new Color[cs];
        for (int i = 0; i < cs; i++) {
            colors[i] = Color.fromRGB(RAND.nextInt(255), RAND.nextInt(255), RAND.nextInt(255));
        }
        int fcs = RAND.nextInt(2) + 1;
        Color[] fcolors = new Color[fcs];
        for (int i = 0; i < fcs; i++) {
            fcolors[i] = Color.fromRGB(RAND.nextInt(255), RAND.nextInt(255), RAND.nextInt(255));
        }
        Type type = Type.values()[RAND.nextInt(Type.values().length)];
        boolean flicker = RAND.nextBoolean();
        boolean trail = RAND.nextBoolean();
        return FireworkEffect.builder().withColor(colors).with(type).withFade(fcolors).flicker(flicker).trail(trail)
                .build();
    }

    @Override
    public void run() {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            Inventory inv = pl.getInventory();
            if (!inv.contains(Material.FIREWORK)) {
                inv.addItem(generateFirework());
            }
        }
    }

    private static class MKTUtilsTemp {
        private final static String[][] COLOR_CODES = new String[][] { { "&0", ChatColor.BLACK.toString() },
                { "&1", ChatColor.DARK_BLUE.toString() }, { "&2", ChatColor.DARK_GREEN.toString() },
                { "&3", ChatColor.DARK_AQUA.toString() }, { "&4", ChatColor.DARK_RED.toString() },
                { "&5", ChatColor.DARK_PURPLE.toString() }, { "&6", ChatColor.GOLD.toString() },
                { "&7", ChatColor.GRAY.toString() }, { "&8", ChatColor.DARK_GRAY.toString() },
                { "&9", ChatColor.BLUE.toString() }, { "&a", ChatColor.GREEN.toString() },
                { "&b", ChatColor.AQUA.toString() }, { "&c", ChatColor.RED.toString() },
                { "&d", ChatColor.LIGHT_PURPLE.toString() }, { "&e", ChatColor.YELLOW.toString() },
                { "&f", ChatColor.WHITE.toString() }, { "&k", ChatColor.MAGIC.toString() },
                { "&l", ChatColor.BOLD.toString() }, { "&m", ChatColor.STRIKETHROUGH.toString() },
                { "&n", ChatColor.UNDERLINE.toString() }, { "&o", ChatColor.ITALIC.toString() },
                { "&r", ChatColor.RESET.toString() } };

        public static <T> List<T> toList(T[] in) {
            List<T> ret = new ArrayList<>();
            for (T t : in) {
                ret.add(t);
            }
            return ret;
        }

        public static String rainbow(String toReplace) {
            if (toReplace == null) {
                return "";
            }
            String ret = "";
            int i = 10;
            for (char c : toReplace.toCharArray()) {
                ret += COLOR_CODES[i++][1] + c;
                if (i == 15) {
                    i = 10;
                }
            }
            return ret;
        }

        private static String repeat(int x, String toRepeat) {
            String str = "";
            for (int i = 0; i < x; i++) {
                str += toRepeat;
            }
            return str;
        }
    }
}
