package nl.imine.hubtweaks.parkour;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

	private ParkourGoal lastGoal = null;

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
		Optional<ParkourTiming> oRecordTime = timings.stream()
				.filter(t -> t.getDestLevel().equals(timing.getDestLevel()))
				.filter(t -> t.getStartLevel().equals(timing.getStartLevel()))
				.sorted(
					(ParkourTiming t1, ParkourTiming t2) -> (int) (t1.getTimeMiliseconds() - t2.getTimeMiliseconds()))
				.findFirst();
		ParkourLevel lastLevel = ParkourManager.getParkourInstance().getLevels().stream().filter(l -> !l.isBonusLevel())
				.sorted((ParkourLevel p1, ParkourLevel p2) -> p2.getLevel() - p1.getLevel()).findFirst().get();
		long recordTime = -1;
		boolean isEnd = (timing.getStartLevel().getLevel() != 0 && timing.getDestLevel().equals(lastLevel));
		if (oRecordTime.isPresent()) {
			recordTime = oRecordTime.get().getTimeMiliseconds();
		}
		if (recordTime > timing.getTimeMiliseconds() || recordTime == -1) {
			if (!isEnd) {
				PlayerUtil.sendTitleMessage(Bukkit.getPlayer(uuid), "", ColorUtil.replaceColors("&7New Record: &c%s",
					StringUtil.readableMiliseconds(timing.getTimeMiliseconds())), 60l);
			} else {
				PlayerUtil.sendTitleMessage(Bukkit.getPlayer(uuid),
					ColorUtil.replaceColors("&7New overall Record: &c%s",
						StringUtil.readableMiliseconds(timing.getTimeMiliseconds())),
					ColorUtil.replaceColors("&7Sum of best segments: &c%s",
						StringUtil.readableMiliseconds(getSumOfBest())),
					60l);
			}
		}
		if (isEnd) {
			PlayerUtil.sendTitleMessage(Bukkit.getPlayer(uuid), "", ColorUtil.replaceColors("&7Final Time: &c%s",
				StringUtil.readableMiliseconds(timing.getTimeMiliseconds())), 60l);
		}
		String oldTime = (recordTime == -1) ? "--:--:--" : StringUtil.readableMiliseconds(recordTime);
		PlayerUtil.sendActionMessage(Bukkit.getPlayer(uuid),
			ColorUtil.replaceColors("&7Old time: &c%s &8|| &7New Time: &c%s", oldTime,
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

	public void setLastGoal(ParkourGoal lastGoal) {
		this.lastGoal = lastGoal;
	}

	public ParkourGoal getLastGoal() {
		return lastGoal;
	}

	public long getSumOfBest() {
		long sumOfBest = 0;
		List<ParkourTiming> bestSegments = new ArrayList<>();
		for (ParkourTiming timing : timings) {
			List<ParkourTiming> bestSegmentsCopy = new ArrayList<>(bestSegments);
			if (bestSegmentsCopy.stream().filter(t -> t.getStartLevel().equals(timing.getStartLevel()))
					.filter(t -> t.getDestLevel().equals(timing.getDestLevel()))
					.noneMatch(t -> t.getTimeMiliseconds() < timing.getTimeMiliseconds())) {
				bestSegmentsCopy.stream().filter(t -> t.getStartLevel().equals(timing.getStartLevel()))
						.filter(t -> t.getDestLevel().equals(timing.getDestLevel()))
						.forEach(t -> bestSegments.remove(t));
				bestSegments.add(timing);
			}
		}
		sumOfBest = bestSegments.stream().mapToLong(l -> l.getTimeMiliseconds()).sum();
		// for (ParkourTiming t : timings) {
		// List<ParkourTiming> bestSegment = new ArrayList<>();
		// if (t.getStartLevel().getLevel() + 1 == t.getDestLevel().getLevel())
		// {
		// Optional<ParkourTiming> best = bestSegment.stream()
		// .filter(s -> (s.getStartLevel() == t.getStartLevel()) &&
		// (s.getDestLevel() == t.getDestLevel()))
		// .sorted((ParkourTiming p1, ParkourTiming p2) -> (int)
		// (p1.getTimeMiliseconds()
		// - p2.getTimeMiliseconds()))
		// .findFirst();
		// if (best.isPresent()) {
		// bestSegment.add(best.get());
		// }
		// }
		// for (ParkourTiming best : bestSegment) {
		// sumOfBest += best.getTimeMiliseconds();
		// }
		// }
		return sumOfBest;
	}

	public void save() {
		DM.updateQuery("UPDATE parkour_player SET level=%d WHERE uuid LIKE '%s'", highestLevel.getLevel(),
			uuid.toString());
	}
}
