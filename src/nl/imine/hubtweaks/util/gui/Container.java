package nl.imine.hubtweaks.util.gui;

import java.util.ArrayList;
import nl.imine.hubtweaks.HubTweaks;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author Sansko1337
 */
public class Container {

    private final ArrayList<Inventory> openInvs = new ArrayList<>();

    private final ArrayList<Button> buttons = new ArrayList<>();

    private final String name;
    private int size;

    public Container(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public void open(Player player) {
        if (buttons.size() < this.size) {
            this.size = ((int) (buttons.size() / 9) + 1) * 9;
        }
        Inventory inv = HubTweaks.getInstance().getServer().createInventory(player, size, name);
        for (Button b : buttons) {
            inv.setItem(b.getSlot(), b.getItemStack());
        }
        openInvs.add(inv);
        player.openInventory(inv);
    }

    public void update() {
        for (Inventory i : openInvs) {
            i.clear();
            for (Button b : buttons) {
                i.setItem(b.getSlot(), b.getItemStack());
            }
        }
    }

    public Button getButton(int slot) {
        for (Button b : buttons) {
            if (b.getSlot() == slot) {
                return b;
            }
        }
        return null;
    }

    public ArrayList<Inventory> getOpenInventories() {
        return openInvs;
    }

    public void removeInventory(Inventory inv) {
        openInvs.remove(inv);
    }

    public void addButton(Button button) {
        buttons.add(button);
    }

    public ArrayList<Button> getButtons() {
        return buttons;
    }

    public String getName() {
        return name;
    }

    public int getFreeSlot() {
        ArrayList<Integer> nr = new ArrayList<>();
        for (int i = 0; i <= buttons.size(); i++) {
            nr.add(i);
        }
        int maxSize = nr.size();
        for (Button b : this.getButtons()) {
            if (b.getSlot() < maxSize) {
                nr.remove((Integer) b.getSlot());
            }
        }
        if (nr.isEmpty()) {
            return 0;
        }
        return (nr.get(0) == null ? 0 : nr.get(0));
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }

}
