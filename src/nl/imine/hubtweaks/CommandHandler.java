package nl.imine.hubtweaks;

import nl.imine.hubtweaks.pvp.PvP;
import nl.imine.hubtweaks.warps.QuickWarp;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BookMeta;

public class CommandHandler implements CommandExecutor {

    public HubTweaks plugin;

    public CommandHandler(HubTweaks plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("createQuickWarp")) {
            if (!(sender instanceof Player)) {
                System.out.println("This command can only be used by a Player");
                return true;
            } else {
                Player player = (Player) sender;
                if (player.hasPermission("hubtweaks.admin.quickwarp")) {
                    if (args.length > 1) {
                        if (isInteger(args[1]) == true) {
                            if (player.getItemInHand().getType() != Material.AIR) {
                                plugin.getQuickWarp().addQuickWarp(player, args[0], Integer.parseInt(args[1]));
                            } else {
                                player.sendMessage("You need to have an In-Hand item to do this");
                            }
                        } else {
                            player.sendMessage("Error: \"" + args[1] + "\" is not a valid number");
                        }
                    } else {
                        return false;
                    }
                } else {
                    player.sendMessage("You don't have permission to do that");
                }
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("ToggleParkourCreation")) {
            if (!(sender instanceof Player)) {
                System.out.println("This command can only be used by a Player");
                return true;
            } else {
                Player player = (Player) sender;
                if (player.hasPermission("hubtweaks.admin.parkour")) {
                    HubTweaks.pmc.put(player, 1);
                    System.out.println("Toggled Parkour Creation");
                    return true;
                }
            }
        }
        if (cmd.getName().equalsIgnoreCase("ConvertRuleBook")) {
            if (!(sender instanceof Player)) {
                System.out.println("This command can only be used by a Player");
                return true;
            } else {
                Player player = (Player) sender;
                if (player.hasPermission("hubtweaks.admin.rules")) {
                    if (player.getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
                        BookMeta RuleBookMeta = (BookMeta) player.getItemInHand().getItemMeta();
                        int PageCount = 0;
                        for (int Pages = 1; Pages <= RuleBookMeta.getPageCount(); Pages++) {
                            plugin.getConfig().set("RuleBook.Pages." + Pages, RuleBookMeta.getPage(Pages));
                            PageCount++;
                        }
                        plugin.getConfig().set("RuleBook.Title", RuleBookMeta.getTitle());
                        player.sendMessage("Added " + PageCount + " Pages to the rulebook with the title:" + RuleBookMeta.getTitle());
                        plugin.saveConfig();
                    } else {
                        player.sendMessage("You need to have a Signed Book in your hand for this command to work");
                    }
                    return true;
                }
            }
        }
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
                        if (plugin.getKotl().getConfig().getConfigurationSection("Kotl") == null) {
                            plugin.getKotl().getConfig().createSection("Kotl");
                            plugin.getKotl().saveConfig();
                        }
                        int x = player.getLocation().getBlockX();
                        int y = player.getLocation().getBlockY();
                        int z = player.getLocation().getBlockZ();
                        String worldName = player.getWorld().getName();
                        plugin.getKotl().getConfig().getConfigurationSection("Kotl").set("w", worldName);
                        plugin.getKotl().getConfig().getConfigurationSection("Kotl").set("x", x);
                        plugin.getKotl().getConfig().getConfigurationSection("Kotl").set("y", y);
                        plugin.getKotl().getConfig().getConfigurationSection("Kotl").set("z", z);
                        player.sendMessage(ChatColor.DARK_RED + "[Kotl]" + ChatColor.WHITE + " Kotl set");
                        plugin.getKotl().saveConfig();
                        return true;
                    }
                    sendHelp(player);
                    return true;
                }
                if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("setPlayersInRadius")) {
                        try {
                            int integer = Integer.parseInt(args[1]);
                            plugin.getKotl().getConfig().set("pir", integer);
                            player.sendMessage(ChatColor.DARK_RED + "[Kotl]" + ChatColor.WHITE + " PlayersInRadius set");
                            plugin.getKotl().saveConfig();
                            return true;
                        } catch (NumberFormatException e) {
                            player.sendMessage(ChatColor.DARK_RED + "[Kotl]" + ChatColor.WHITE + " Bad usage for help:'/Kotl'" + ChatColor.ITALIC + " Example: /Kotl setPlayersInRadius 4");
                            return true;
                        }
                    }
                    if (args[0].equalsIgnoreCase("setCoinsInSeconds")) {
                        try {
                            int integer = Integer.parseInt(args[1]);
                            plugin.getKotl().getConfig().set("cis", integer);
                            player.sendMessage(ChatColor.DARK_RED + "[Kotl]" + ChatColor.WHITE + " CoinsInSeconds set");
                            plugin.getKotl().saveConfig();
                            return true;
                        } catch (NumberFormatException e) {
                            player.sendMessage(ChatColor.DARK_RED + "[Kotl]" + ChatColor.WHITE + " Bad usage for help:'/Kotl'" + ChatColor.ITALIC + " Example: /Kotl setcoinsinseconds 4");
                            return true;
                        }
                    }
                    if (args[0].equalsIgnoreCase("setRadiusOfKOTL")) {
                        try {
                            int integer = Integer.parseInt(args[1]);
                            plugin.getKotl().getConfig().set("rok", integer);
                            player.sendMessage(ChatColor.DARK_RED + "[Kotl]" + ChatColor.WHITE + " RadiusOfKOTL set");
                            plugin.getKotl().saveConfig();
                            return true;
                        } catch (NumberFormatException e) {
                            player.sendMessage(ChatColor.DARK_RED + "[Kotl]" + ChatColor.WHITE + " Bad usage for help:'/Kotl'" + ChatColor.ITALIC + " Example: /Kotl setRadiusOfKOTL 4");
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
        player.sendMessage(ChatColor.RED + "/Kotl setplayersinradius 'number'" + ChatColor.WHITE + ChatColor.ITALIC + " Set the playersinradius!");
        player.sendMessage(ChatColor.RED + "/Kotl setCoinsInSeconds 'number'" + ChatColor.WHITE + ChatColor.ITALIC + " Set the Coins In Seconds!");
        player.sendMessage(ChatColor.RED + "/Kotl setRadiusOfKOTL 'number'" + ChatColor.WHITE + ChatColor.ITALIC + " Set the radius of the Kotl!");
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
