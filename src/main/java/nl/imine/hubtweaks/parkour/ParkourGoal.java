package nl.imine.hubtweaks.parkour;

import org.bukkit.Location;

public class ParkourGoal {

	private final ParkourLevel level;
	private final Location target;

	public ParkourGoal(ParkourLevel level, Location target) {
		this.level = level;
		this.target = target;
	}

	public Location getTarget() {
		return target;
	}

	public ParkourLevel getLevel() {
		return level;
	}
}
