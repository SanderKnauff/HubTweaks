package nl.imine.hubtweaks.parkour;

import org.bukkit.DyeColor;

public class ParkourLevel {

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
