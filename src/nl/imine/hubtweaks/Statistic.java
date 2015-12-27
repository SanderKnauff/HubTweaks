package nl.imine.hubtweaks;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import nl.makertim.stats.PlayerStatistics;

public class Statistic implements Listener {

    private static final String TAG_NAME = "StatTimePlayed";
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
    public void onLogin(PlayerLoginEvent ple) {
        Player pl = ple.getPlayer();
        pl.setMetadata(TAG_NAME,
                new FixedMetadataValue(HubTweaks.getInstance(), pl.getStatistic(org.bukkit.Statistic.PLAY_ONE_TICK)));
    }

    @EventHandler
    public void onLoggoff(PlayerQuitEvent pqe) {
        Player pl = pqe.getPlayer();
        List<MetadataValue> mvl = pl.getMetadata(TAG_NAME);
        int offset = 0;
        for (MetadataValue mv : mvl) {
            if (mv.getOwningPlugin().equals(HubTweaks.getInstance())) {
                offset = (int) ((FixedMetadataValue) mv).value();
                offset = (int) Math.floor((double) offset / 20D / 60D);
            }
        }
        int time = (int) Math.floor(((double) pl.getStatistic(org.bukkit.Statistic.PLAY_ONE_TICK)) / 20D / 60D);
        if (time - offset < 0) {
            return;
        }
        PlayerStatistics.getPlayer(pl).getHub().addTimePlayed(time - offset);
    }

}
