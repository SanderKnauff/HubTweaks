package nl.imine.hubtweaks.oitc;

import nl.imine.hubtweaks.CommandHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddPvPSpawnCommand extends CommandHandler {

    private final PvPSpawnRepository pvpSpawnRepository;

    public AddPvPSpawnCommand(PvPSpawnRepository pvpSpawnRepository) {
        this.pvpSpawnRepository = pvpSpawnRepository;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by a Player");
            return true;
        }

        if (!player.hasPermission("hubtweaks.admin.rules")) {
            return true;
        }

        pvpSpawnRepository.addOne(player.getLocation());
        return true;
    }
}
