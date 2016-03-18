package nl.imine.hubtweaks.parkour;

import org.bukkit.DyeColor;

public class ParkourLevel {

	public static ParkourLevel START_LEVEL = new ParkourLevel((short) 0, true, null);
	public static ParkourLevel EQUIPMENT_LEVEL = new ParkourLevel((short) -1, true, null);

	private final short level;
	private final boolean bonusLevel;
	private final DyeColor reward;

	public ParkourLevel(short level, boolean bonusLevel, DyeColor reward) {
		this.level = level;
		this.bonusLevel = bonusLevel;
		this.reward = reward;
	}

	public short getLevel() {
		return level;
	}

	public boolean isBonusLevel() {
		return bonusLevel;
	}

	public DyeColor getReward() {
		return reward;
	}
}
