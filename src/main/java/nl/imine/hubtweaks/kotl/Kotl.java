package nl.imine.hubtweaks.kotl;

import java.util.Optional;

import nl.imine.hubtweaks.HubTweaksPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Kotl {

	public static final Location[] BOX = new Location[]{new Location(Bukkit.getWorlds().get(0), 58, 44, -513),
			new Location(Bukkit.getWorlds().get(0), 63, 34, -508)};

	private static Kotl kotl;

	private final Location plate = new Location(Bukkit.getWorlds().get(0), 60, 41, -511);
	private Player king;
	private Player oldKing;
	private final int radius = 10;

	public static void init() {
		Kotl.kotl = new Kotl();
		KotlListener.init(kotl);
	}

	public void addEntropiaWandTo(Player p) {
		ItemStack wand = new ItemStack(Material.GOLDEN_CARROT, 1);
		ItemMeta wandMeta = wand.getItemMeta();
		wandMeta.setDisplayName(ChatColor.RESET + "Entropia Wand");
		wandMeta.addEnchant(Enchantment.KNOCKBACK, (int) (Math.random() * 255D), true);
		wand.setItemMeta(wandMeta);
		ItemStack EntropiaWand = wand;

		final ItemStack helmet = new ItemStack(Material.SLIME_BALL);
		Optional.ofNullable(helmet.getItemMeta())
			.ifPresent(itemMeta -> {
				itemMeta.setCustomModelData(69);
				helmet.setItemMeta(itemMeta);
			});

		p.getInventory().addItem(new ItemStack[]{EntropiaWand});
		p.getInventory().setHelmet(helmet);
	}

	public void removeEntropiaWand(final Player p) {
		p.getInventory().remove(Material.GOLDEN_CARROT);
		p.getInventory().remove(Material.GOLDEN_HELMET);
		p.getInventory().setHelmet(new ItemStack(Material.AIR));
	}

	public Location getPlateLoc() {
		return plate;
	}

	public void setKing(Player player) {
		this.oldKing = king;
		this.king = player;
		if (this.king != null) {
			kotl.addEntropiaWandTo(player);
		}
	}

	public Player getKing() {
		return this.king;
	}

	public Player getOldKing() {
		return this.oldKing;
	}

	public int getRadius() {
		return this.radius;
	}

	private Location getLocationFromSection(ConfigurationSection section) {
		World world = HubTweaksPlugin.getInstance().getServer().getWorld(section.getString(KotlConfig.LOCATION_WORLD));
		int x = (int) section.getDouble(KotlConfig.LOCATION_X);
		int y = (int) section.getDouble(KotlConfig.LOCATION_Y);
		int z = (int) section.getDouble(KotlConfig.LOCATION_Z);
		return new Location(world, x, y, z);
	}

	public static Kotl getInstance() {
		return Kotl.kotl;
	}
}
