package nl.imine.hubtweaks.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.Colorable;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class ItemUtil {

    public static ItemStack writtenBook(List<String> pages, String owner, String name) {
        return writtenBook(pages, owner, name, 1);
    }

    public static ItemStack writtenBook(List<String> pages, String owner, String name, int amount) {
        ItemStack ret = new ItemStack(Material.WRITTEN_BOOK, amount);
        BookMeta bm = (BookMeta) ret.getItemMeta();
        bm.setAuthor(owner);
        bm.setTitle(name);
        bm.setDisplayName(name);
        bm.setPages(pages);
        ret.setItemMeta(bm);
        return ret;
    }

    public static ItemStack enchantedBook(Map<Enchantment, Integer> enchants) {
        return enchantedBook(enchants, 1);
    }

    public static ItemStack enchantedBook(Map<Enchantment, Integer> enchants, int amount) {
        ItemStack ret = new ItemStack(Material.ENCHANTED_BOOK, amount);
        EnchantmentStorageMeta esm = (EnchantmentStorageMeta) ret.getItemMeta();
        for (Entry<Enchantment, Integer> enchant : enchants.entrySet()) {
            esm.addEnchant(enchant.getKey(), enchant.getValue(), true);
        }
        ret.setItemMeta(esm);
        return ret;
    }

    public static ItemStack potion(PotionType effect) {
        return potion(effect, 1);
    }

    public static ItemStack potion(PotionType effect, int amount) {
        ItemStack ret = new ItemStack(Material.POTION);
        PotionMeta pm = (PotionMeta) ret.getItemMeta();
        pm.setBasePotionData(new PotionData(effect));
        ret.setItemMeta(pm);
        return ret;
    }

    public static Builder getBuilder(ItemStack item) {
        return new Builder(item);
    }

    /**
     * Creates an ItemStack builder for creating complex itemstacks within one
     * line. Each child method of builder will return the builder instance back
     * to be able to keep working on one line.
     *
     * To finish building and exporting to an ItemStack instance, use the
     * Builder#build() method.
     *
     * @param material
     *            The Material the Item will be made of.
     * @return A builder instance for creating ItemStacks.
     */
    public static Builder getBuilder(Material material) {
        return new Builder(material);
    }

    /**
     * Creates an ItemStack builder for creating complex itemstacks within one
     * line. Each child method of builder will return the builder instance back
     * to be able to keep working on one line.
     *
     * To finish building and exporting to an ItemStack instance, use the
     * Builder#build() method.
     *
     * @param material
     *            The Material the Item will be made of.
     * @param itemMeta
     *            The ItemMeta which properties should be used as base.
     * @return A builder instance for creating ItemStacks.
     */
    public static Builder getBuilder(Material material, ItemMeta itemMeta) {
        return new Builder(material, itemMeta);
    }

    public static class Builder {

        private final ItemStack itemStack;

        private Builder(ItemStack is) {
            itemStack = is;
        }

        private Builder(Material material) {
            itemStack = new ItemStack(material, 1);
        }

        private Builder(Material material, ItemMeta im) {
            itemStack = new ItemStack(material, 1);
            itemStack.setItemMeta(im);
        }

        /**
         * Set the amount of Items of which ItemStack will contain.
         *
         * @param amount
         *            The amount of Items the ItemStack will contain.
         * @return The current ItemStack Builder instance.
         */
        public Builder setAmount(int amount) {
            itemStack.setAmount(amount);
            return this;
        }

        /**
         * Adds an Enchantment to the ItemStack.
         *
         * This can be used to add enchantments with non-standard levels. Be
         * aware that doing so may have unintended side-effects.
         *
         * @param type
         *            The Enchantment type the item will recieve.
         * @param level
         *            The level of which the enchant will be.
         * @return The current ItemStack Builder instance.
         */
        public Builder addEnchantment(Enchantment type, int level) {
            itemStack.addUnsafeEnchantment(type, level);
            return this;
        }

        /**
         * Set the Durability of the ItemStack.
         *
         * @param durability
         *            The durability the item will have on creation.
         * @return The current ItemStack Builder instance.
         */
        public Builder setDurability(int durability) {
            if(itemStack.getItemMeta() instanceof Damageable damageable) {
                damageable.setDamage(durability);
                itemStack.setItemMeta(damageable);
            }
            return this;
        }

        /**
         * Sets the name the ItemStack will recieve on creation.
         *
         * @param name
         *            The name the Itemstack will have.
         * @return The current ItemStack Builder instance.
         */
        public Builder setName(String name) {
            ItemMeta im = itemStack.getItemMeta();
            im.setDisplayName(name);
            itemStack.setItemMeta(im);
            return this;
        }

        /**
         * Sets the Lore the ItemStack will recieve on creation.
         *
         * @param lore
         *            The lines of lore the ItemStack will have.
         * @return The current ItemStack Builder instance.
         */
        public Builder setLore(String... lore) {
            return setLore(Arrays.asList(lore));
        }

        /**
         * Sets the Lore the ItemStack will recieve on creation.
         *
         * @param lore
         *            The lines of lore the ItemStack will have.
         * @return The current ItemStack Builder instance.
         */
        public Builder setLore(List<String> lore) {
            ItemMeta im = itemStack.getItemMeta();
            im.setLore(lore);
            itemStack.setItemMeta(im);
            return this;
        }

        /**
         * Adds a line of lore to the ItemStack. These will be inserted after
         * the last line of lore if this was already present.
         *
         * @param lore
         *            lines of lore to add.
         * @return The current ItemStack Builder instance.
         */
        public Builder addLore(String... lore) {
            return addLore(Arrays.asList(lore));
        }

        /**
         * Adds a line of lore to the ItemStack. These will be inserted after
         * the last line of lore if this was already present.
         *
         * @param lore
         *            lines of lore to add.
         * @return The current ItemStack Builder instance.
         */
        public Builder addLore(List<String> lore) {
            ItemMeta im = itemStack.getItemMeta();
            List<String> oldLore = im.getLore();
            if (oldLore != null) {
                oldLore.addAll(lore);
            } else {
                oldLore = lore;
            }
            im.setLore(oldLore);
            itemStack.setItemMeta(im);
            return this;
        }

        /**
         * Sets the ItemFlags the ItemStack will have on creation.
         *
         * ItemFlags have effect on the display on certain build in tags the
         * ItemStack has visible. See Spigot's ItemFlag javadoc for more
         * details.
         *
         * @param flag
         *            the flag to add to the ItemStack.
         * @return The current ItemStack Builder instance.
         */
        public Builder addFlag(ItemFlag flag) {
            ItemMeta im = itemStack.getItemMeta();
            im.addItemFlags(flag);
            itemStack.setItemMeta(im);
            return this;
        }

        /**
         * Sets the color of this ItemStack.
         *
         * This will only work on Items which metadata implements
         * <i>Colorable</i> and will fail silently if it does not.
         *
         * @param color
         *            The color to change the item to.
         * @return The current ItemStack Builder instance.
         */
        public Builder setColor(DyeColor color) {
            if (itemStack.getItemMeta() instanceof Colorable) {
                ItemMeta im = itemStack.getItemMeta();
                ((Colorable) im).setColor(color);
                itemStack.setItemMeta(im);
            }
            return this;
        }

        /**
         * Build the ItemStack with the data provided.
         *
         * If the builder has conflicting data the last one in the list will be
         * used in the final ItemStack.
         *
         * @return The ItemStack that has been build.
         */
        public ItemStack build() {
            return itemStack;
        }
    }
}
