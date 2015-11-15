package nl.imine.hubtweaks.util.gui.button;

import java.util.ArrayList;
import nl.imine.hubtweaks.util.gui.Button;
import nl.imine.hubtweaks.util.gui.Container;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 *
 * @author Sander
 */
public class ButtonTeleport extends Button {

    private final Location location;

    public ButtonTeleport(Container container, Material material, String name, int slot, Location location) {
        super(container, material, name, slot);
        this.location = location;
    }

    public ButtonTeleport(Container container, Material material, String name, int slot, ArrayList<String> subtext, Location location) {
        super(container, material, name, slot, subtext);
        this.location = location;
    }

    public ButtonTeleport(Container container, Material material, String name, int slot, String subtext, Location location) {
        super(container, material, name, slot, subtext);
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }

    @Override
    public void doAction(Player player) {
        player.teleport(this.location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
}
