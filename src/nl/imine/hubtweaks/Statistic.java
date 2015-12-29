package nl.imine.hubtweaks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import nl.makertim.stats.PlayerStatistics;

public class Statistic implements Listener {

    public static Statistic instance;

    public static void init() {
        instance = new Statistic();
    }

    public Statistic() {
        Bukkit.getPluginManager().registerEvents(this, HubTweaks.getInstance());
    }

    public static void addToKing(Player pl) {
        PlayerStatistics.getPlayer(pl).getHub().addOneBeenKing();
    }

    public static void addToKill(Player pl) {
        PlayerStatistics.getPlayer(pl).getHub().addOneKills();
    }

    public static void addToRide(Player pl) {
        PlayerStatistics.getPlayer(pl).getHub().addOneRide();
    }

    public static void addToRidden(Player pl) {
        PlayerStatistics.getPlayer(pl).getHub().addOneRidden();
    }

    public static void addToParkour(Player pl) {
        PlayerStatistics.getPlayer(pl).getHub().addOneParkourFinished();
    }

    @EventHandler
    public void onLoggoff(PlayerQuitEvent pqe) {
        PlayerStatistics.getPlayer(pqe.getPlayer()).getHub()
                .setTimePlayed(pqe.getPlayer().getStatistic(org.bukkit.Statistic.PLAY_ONE_TICK) / 20 / 60);
    }
}
