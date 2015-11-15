package nl.imine.hubtweaks.util.gui;

import java.util.ArrayList;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author Sansko1337
 */
public class GuiManager {

    private static GuiManager manager;

    private ArrayList<Container> containers = new ArrayList<>();

    public static void init() {
        GuiManager.manager = new GuiManager();
        GuiListener.init();
    }

    public static GuiManager getInstance() {
        return manager;
    }

    public Container createContainer(String name, int size) {
        Container container = new Container(name, size);
        containers.add(container);
        return container;
    }

    public boolean isGuiInventory(Inventory inv) {
        for (Container c : containers) {
            for (Inventory i : c.getOpenInventories()) {
                if (i.equals(inv)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Container getInventoryContainer(Inventory inv) {
        for (Container c : containers) {
            for (Inventory i : c.getOpenInventories()) {
                if (i.equals(inv)) {
                    return c;
                }
            }
        }
        return null;
    }

    private GuiManager() {

    }
}
