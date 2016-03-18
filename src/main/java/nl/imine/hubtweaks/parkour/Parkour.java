package nl.imine.hubtweaks.parkour;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Parkour {

	private List<ParkourGoal> goals = new ArrayList<>();
	private List<ParkourLevel> levels;
	private List<ParkourPlayer> players = new ArrayList<>();

	public Parkour(List<ParkourLevel> levels) {
		this.levels = levels;
	}

	public void addGoals(List<ParkourGoal> goals) {
		this.goals.addAll(goals);
	}

	public List<ParkourGoal> getGoals() {
		return goals;
	}

	public List<ParkourLevel> getLevels() {
		return levels;
	}

	public boolean isParkourGoal(Location l) {
		return goals.stream().anyMatch(g -> g.getTarget().equals(l));
	}

	public boolean isParkourLevel(short level) {
		return levels.stream().anyMatch(l -> l.getLevel() == level);
	}

	public ParkourGoal getParkourGoal(Location l) {
		if (isParkourGoal(l)) {
			return goals.stream().filter(g -> g.getTarget().equals(l)).findFirst().get();
		}
		return null;
	}

	public ParkourLevel getLevel(short level) {
		if (isParkourLevel(level)) {
			return levels.stream().filter(l -> l.getLevel() == level).findFirst().get();
		}
		return null;
	}

	public ParkourLevel getFinalLevel(boolean includeBonus) {
		return getLevels().stream().filter(l -> !l.isBonusLevel() || !includeBonus)
				.sorted((ParkourLevel p1, ParkourLevel p2) -> p2.getLevel() - p1.getLevel()).findFirst().get();
	}

	public void addPlayer(ParkourPlayer player) {
		players.add(player);
	}

	public void removePlayer(ParkourPlayer player) {
		players.remove(player);
	}

	public boolean isParkourPlayer(Player player) {
		return players.stream().anyMatch(p -> p.getUuid().equals(player.getUniqueId()));
	}

	public ParkourPlayer getParkourPlayer(Player player) {
		if (isParkourPlayer(player)) {
			return players.stream().filter(p -> p.getUuid().equals(player.getUniqueId())).findFirst().get();
		}
		return null;
	}

	public List<ParkourPlayer> getPlayers() {
		return players;
	}
}
