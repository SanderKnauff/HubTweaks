package nl.imine.hubtweaks.pvp;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PvPJoinEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Player player;

    public PvPJoinEvent(Player player) {
        this.player = player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }
}
