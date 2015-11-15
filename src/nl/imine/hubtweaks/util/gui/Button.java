package nl.imine.hubtweaks.util.gui;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Sansko1337
 */
public class Button {

    protected Container container;

    protected Material material;
    protected String name;
    protected ArrayList<String> subtext;
    protected int slot;

    public Button(Container container, Material material, String name, int slot) {
        this.container = container;
        this.material = material;
        this.name = name;
        this.slot = slot;
    }

    public Button(Container container, Material material, String name, int slot, ArrayList<String> subtext) {
        this(container, material, name, slot);
        this.subtext = subtext;
    }

    public Button(Container container, Material material, String name, int slot, String subtext) {
        this(container, material, name, slot);
        ArrayList<String> arr = new ArrayList<>();
        arr.add(subtext);
        this.subtext = arr;
    }

    public ItemStack getItemStack() {
        ItemStack is = new ItemStack(this.material);
        ItemMeta ism = is.getItemMeta();
        ism.setDisplayName(name);
        ism.setLore(null);
        is.setItemMeta(ism);
        return is;
    }

    /**
     * @return the container
     */
    public Container getContainer() {
        return container;
    }

    /**
     * @param container the container to set
     */
    public void setContainer(Container container) {
        this.container = container;
    }

    /**
     * @return the material
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * @param material the material to set
     */
    public void setMaterial(Material material) {
        this.material = material;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the subtext
     */
    public ArrayList<String> getSubtext() {
        return subtext;
    }

    /**
     * @param subtext the subtext to set
     */
    public void setSubtext(ArrayList<String> subtext) {
        this.subtext = subtext;
    }

    /**
     * @return the slot
     */
    public int getSlot() {
        return slot;
    }

    /**
     * @param slot the slot to set
     */
    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void doAction(Player player) {
    }
}
