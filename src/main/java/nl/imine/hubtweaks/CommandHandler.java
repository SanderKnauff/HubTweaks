package nl.imine.hubtweaks;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.imine.hubtweaks.kotl.Kotl;
import nl.imine.hubtweaks.oitc.PvP;

public class CommandHandler implements CommandExecutor {

	public HubTweaks plugin;

	public CommandHandler(HubTweaks plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("HubTweaks")) {
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("addPvPSpawn")) {
					if (!(sender instanceof Player)) {
						System.out.println("This command can only be used by a Player");
					} else {
						Player p = (Player) sender;
						if (p.hasPermission("hubtweaks.admin.rules")) {
							PvP.addSpawn(p.getLocation());
						}
					}
					return true;
				}
			}
			return false;
		}
		if (cmd.getName().equalsIgnoreCase("kotl")) {
			Player player = (Player) sender;
			if (player.hasPermission("hubtweaks.admin.kotl")) {
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("help")) {
						sendHelp(player);
						return true;
					}
					if (args[0].equalsIgnoreCase("setKOTL")) {
						if (Kotl.getInstance().getConfig().getConfigurationSection("Kotl") == null) {
							Kotl.getInstance().getConfig().createSection("Kotl");
							Kotl.getInstance().saveConfig();
						}
						int x = player.getLocation().getBlockX();
						int y = player.getLocation().getBlockY();
						int z = player.getLocation().getBlockZ();
						String worldName = player.getWorld().getName();
						Kotl.getInstance().getConfig().getConfigurationSection("Kotl").set("w", worldName);
						Kotl.getInstance().getConfig().getConfigurationSection("Kotl").set("x", x);
						Kotl.getInstance().getConfig().getConfigurationSection("Kotl").set("y", y);
						Kotl.getInstance().getConfig().getConfigurationSection("Kotl").set("z", z);
						player.sendMessage(ChatColor.DARK_RED + "[Kotl]" + ChatColor.WHITE + " Kotl set");
						Kotl.getInstance().saveConfig();
						return true;
					}
					sendHelp(player);
					return true;
				}
				if (args.length == 2) {
					if (args[0].equalsIgnoreCase("setPlayersInRadius")) {
						try {
							int integer = Integer.parseInt(args[1]);
							Kotl.getInstance().getConfig().set("pir", integer);
							player.sendMessage(
								ChatColor.DARK_RED + "[Kotl]" + ChatColor.WHITE + " PlayersInRadius set");
							Kotl.getInstance().saveConfig();
							return true;
						} catch (NumberFormatException e) {
							player.sendMessage(
								ChatColor.DARK_RED + "[Kotl]" + ChatColor.WHITE + " Bad usage for help:'/Kotl'"
										+ ChatColor.ITALIC + " Example: /Kotl setPlayersInRadius 4");
							return true;
						}
					}
					if (args[0].equalsIgnoreCase("setRadiusOfKOTL")) {
						try {
							int integer = Integer.parseInt(args[1]);
							Kotl.getInstance().getConfig().set("rok", integer);
							player.sendMessage(ChatColor.DARK_RED + "[Kotl]" + ChatColor.WHITE + " RadiusOfKOTL set");
							Kotl.getInstance().saveConfig();
							return true;
						} catch (NumberFormatException e) {
							player.sendMessage(
								ChatColor.DARK_RED + "[Kotl]" + ChatColor.WHITE + " Bad usage for help:'/Kotl'"
										+ ChatColor.ITALIC + " Example: /Kotl setRadiusOfKOTL 4");
							return true;
						}
					}
					sendHelp(player);
					return true;
				}
				sendHelp(player);
				return true;
			}
			player.sendMessage(ChatColor.RED + "You must be a operator to use this command!");
			return true;
		}
		return false;
	}

	private void sendHelp(Player player) {
		player.sendMessage(ChatColor.DARK_RED + "Usage:");
		player.sendMessage(ChatColor.RED + "/Kotl setkotl" + ChatColor.WHITE + ChatColor.ITALIC + " Set the kotl!");
		player.sendMessage(ChatColor.RED + "/Kotl setplayersinradius 'number'" + ChatColor.WHITE + ChatColor.ITALIC
				+ " Set the playersinradius!");
		player.sendMessage(ChatColor.RED + "/Kotl setCoinsInSeconds 'number'" + ChatColor.WHITE + ChatColor.ITALIC
				+ " Set the Coins In Seconds!");
		player.sendMessage(ChatColor.RED + "/Kotl setRadiusOfKOTL 'number'" + ChatColor.WHITE + ChatColor.ITALIC
				+ " Set the radius of the Kotl!");
	}

	public boolean isInteger(String string) {
		try {
			Integer.valueOf(string);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
