package nl.imine.hubtweaks.parkour.command;

import nl.imine.hubtweaks.CommandHandler;
import nl.imine.hubtweaks.parkour.ParkourGoalRepository;
import nl.imine.hubtweaks.parkour.ParkourLevelRepository;
import nl.imine.hubtweaks.parkour.model.ParkourGoal;
import nl.imine.hubtweaks.parkour.model.ParkourLevel;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddLevelCommand extends CommandHandler {

    private final ParkourLevelRepository parkourLevelRepository;

    public AddLevelCommand(ParkourLevelRepository parkourLevelRepository) {
        this.parkourLevelRepository = parkourLevelRepository;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length != 2) {
            return false;
        }

        if(!sender.hasPermission("hubtweaks.parkour.manage")) {
            sender.sendMessage("No permission.");
            return true;
        }

        short level = Short.parseShort(args[0]);
        final DyeColor reward = DyeColor.valueOf(args[1]);
        parkourLevelRepository.addOne(new ParkourLevel(level, false, reward));
        sender.sendMessage("Added level " + level + " with color " + reward);
        return true;
    }
}
