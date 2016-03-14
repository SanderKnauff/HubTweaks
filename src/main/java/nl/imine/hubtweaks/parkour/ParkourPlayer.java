package nl.imine.hubtweaks.parkour;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import nl.imine.api.Credentials;
import nl.imine.api.db.DatabaseManager;
import nl.imine.api.util.ColorUtil;
import nl.imine.api.util.PlayerUtil;
import nl.imine.api.util.StringUtil;
import org.bukkit.Bukkit;

public class ParkourPlayer {

	public static final DatabaseManager DM = new DatabaseManager(Credentials.getUrl(), Credentials.getUsername(),
			Credentials.getPassword(), "iMine_Statistics");

	private final UUID uuid;
	private ParkourLevel highestLevel;
	private final List<ParkourTiming> timings;

	private final List<ParkourTiming> pendingTimes = new ArrayList<>();

	public ParkourPlayer(UUID uuid, ParkourLevel highestLevel, List<ParkourTiming> timings) {
		this.uuid = uuid;
		this.highestLevel = highestLevel;
		this.timings = timings;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setHighestLevel(ParkourLevel highestLevel) {
		this.highestLevel = highestLevel;
	}

	public ParkourLevel getHighestLevel() {
		return highestLevel;
	}

	public void addTiming(ParkourTiming timing) {
		DM.insertQuery("INSERT INTO parkour_timing VALUES ('%s','%s',%d,%d,%d)", uuid,
			timing.getDateObtained().toString(), timing.getStartLevel().getLevel(), timing.getDestLevel().getLevel(),
			timing.getTimeMiliseconds());
		long recordTime = timings.stream().filter(t -> t.getDestLevel().equals(timing.getDestLevel()))
				.filter(t -> t.getStartLevel().equals(timing.getStartLevel()))
				.sorted(
					(ParkourTiming t1, ParkourTiming t2) -> (int) (t1.getTimeMiliseconds() - t2.getTimeMiliseconds()))
				.findFirst().get().getTimeMiliseconds();

		if (recordTime > timing.getTimeMiliseconds()) {
			PlayerUtil.sendTitleMessage(Bukkit.getPlayer(uuid), null, ColorUtil.replaceColors("&7New Record: &c%s",
				StringUtil.readableMiliseconds(timing.getTimeMiliseconds())), 40l);
		}
		PlayerUtil.sendActionMessage(Bukkit.getPlayer(uuid),
			ColorUtil.replaceColors("&7Old time: &c%s &8|| &7New Time: &c%s",
				StringUtil.readableMiliseconds(recordTime),
				StringUtil.readableMiliseconds(timing.getTimeMiliseconds())));
		timings.add(timing);
	}

	public List<ParkourTiming> getTimings() {
		return timings;
	}

	public void addPendingTime(ParkourTiming time) {
		pendingTimes.add(time);
	}

	public void removePendingTime(ParkourTiming time) {
		pendingTimes.remove(time);
	}

	public List<ParkourTiming> getPendingTimes() {
		return pendingTimes;
	}

	public void resetPendingTimes() {
		pendingTimes.clear();
	}

	public void save() {
		DM.updateQuery("UPDATE parkour_player SET level=%d WHERE uuid LIKE '%s'", highestLevel.getLevel(),
			uuid.toString());
	}
}
