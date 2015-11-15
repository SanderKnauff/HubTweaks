package nl.imine.hubtweaks.util.gui.button;

import java.util.ArrayList;
import nl.imine.hubtweaks.util.gui.Button;
import nl.imine.hubtweaks.util.gui.Container;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 *
 * @author Sander
 */
public class ButtonBrowse extends Button {

    private Container destination;

    public ButtonBrowse(Container container, Material material, String name, int slot, Container destination) {
        super(container, material, name, slot);
        this.destination = destination;
    }

    public ButtonBrowse(Container container, Material material, String name, int slot, ArrayList<String> subtext, Container destination) {
        super(container, material, name, slot, subtext);
        this.destination = destination;
    }

    public ButtonBrowse(Container container, Material material, String name, int slot, String subtext, Container destination) {
        super(container, material, name, slot, subtext);
        this.destination = destination;
    }

    public Container getDestination() {
        return this.destination;
    }

    @Override
    public void doAction(Player player) {
        System.out.println("Opening inv for player: " + player.getName());
        this.destination.open(player);
    }
}
