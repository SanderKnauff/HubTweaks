package nl.imine.hubtweaks.parkour;

import java.sql.Timestamp;

public class ParkourTiming {

	private Timestamp dateObtained;

	private final ParkourLevel startLevel;
	private final ParkourLevel destLevel;

	private long timeMiliseconds;

	public ParkourTiming(Timestamp dateObtained, ParkourLevel startLevel, ParkourLevel destLevel, long timeMiliseconds) {
		this.dateObtained = dateObtained;
		this.startLevel = startLevel;
		this.destLevel = destLevel;
		this.timeMiliseconds = timeMiliseconds;
	}

	public void setDateObtained(Timestamp dateObtained) {
		this.dateObtained = dateObtained;
	}

	public Timestamp getDateObtained() {
		return dateObtained;
	}

	public ParkourLevel getStartLevel() {
		return startLevel;
	}

	public ParkourLevel getDestLevel() {
		return destLevel;
	}

	public void setTimeMiliseconds(long timeMiliseconds) {
		this.timeMiliseconds = timeMiliseconds;
	}

	public long getTimeMiliseconds() {
		return timeMiliseconds;
	}

}
