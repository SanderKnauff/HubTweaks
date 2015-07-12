package nl.imine.hubtweaks.util;

import nl.imine.hubtweaks.HubTweaks;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * @author MakerTim
 */
public class Messenger {

    /**
     * @Author MakerTim Send action message.
     *
     * @param pl the pl
     * @param message the message
     */
    public static void sendActionMessage(Player pl, String message) {
        try {
            IChatBaseComponent icbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
            PacketPlayOutChat ppoc = new PacketPlayOutChat(icbc, (byte) 2);
            ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(ppoc);
        } catch (Exception ex) {
            pl.sendMessage(message);
        }
    }

    /**
     * @Author MakerTim Send action message to all online players.
     *
     * @param message the message
     */
    public static void sendActionMessageToAll(String message) {
        for (Player pl : HubTweaks.getInstance().getServer().getOnlinePlayers()) {
            try {
                IChatBaseComponent icbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
                PacketPlayOutChat ppoc = new PacketPlayOutChat(icbc, (byte) 2);
                ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(ppoc);
            } catch (Exception ex) {
                pl.sendMessage(message);
            }
        }
    }

    /**
     * @Author MakerTim Send title message.
     *
     * @param pl the pl
     * @param title the title
     * @param subTitle the sub title
     * @param duratio the duratio
     */
    public static void sendTitleMessage(Player pl, String title, String subTitle, int duratio) {
        try {
            CraftPlayer player = ((CraftPlayer) pl);
            if (title != null) {
                player.getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\": \"" + title + "\"}"), duratio, duratio, duratio));
            }
            if (subTitle != null) {
                player.getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a("{\"text\": \"" + subTitle + "\"}")));
            }
        } catch (Exception ex) {
            if (title != null) {
                pl.sendMessage(title);
            }
            if (subTitle != null) {
                pl.sendMessage(subTitle);
            }
        }
    }
}
