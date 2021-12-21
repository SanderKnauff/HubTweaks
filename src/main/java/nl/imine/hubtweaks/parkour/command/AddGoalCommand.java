package nl.imine.hubtweaks.parkour.command;

import nl.imine.hubtweaks.CommandHandler;
import nl.imine.hubtweaks.parkour.ParkourGoalRepository;
import nl.imine.hubtweaks.parkour.ParkourLevelRepository;
import nl.imine.hubtweaks.parkour.model.ParkourGoal;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddGoalCommand extends CommandHandler {

    private final ParkourLevelRepository parkourLevelRepository;
    private final ParkourGoalRepository parkourGoalRepository;

    public AddGoalCommand(ParkourLevelRepository parkourLevelRepository, ParkourGoalRepository parkourGoalRepository) {
        this.parkourLevelRepository = parkourLevelRepository;
        this.parkourGoalRepository = parkourGoalRepository;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by a player.");
            return true;
        }

        if(!player.hasPermission("hubtweaks.parkour.manage")) {
            player.sendMessage("No permission.");
            return true;
        }

        if(args.length != 4) {
            return false;
        }

        short level = Short.parseShort(args[0]);
        parkourGoalRepository.addOne(new ParkourGoal(parkourLevelRepository.findOne(level).orElse(null), new Location(player.getWorld(), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]))));
        player.sendMessage("Added goal for level " + level);
        return true;
    }
}
