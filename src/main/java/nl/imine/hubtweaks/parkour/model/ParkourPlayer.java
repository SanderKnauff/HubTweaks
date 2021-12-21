package nl.imine.hubtweaks.parkour.model;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

public class ParkourPlayer {
	private final UUID uuid;
	@Nullable
	private ParkourLevel highestLevel;
	private final Map<ParkourLevel, ParkourTiming> timings;

	private Map<ParkourLevel, ParkourTiming> currentRunTimings;
	private boolean hasCheated = false;
	private boolean hasTouchedPlated = false;

	private Short currentSegment;
	private Instant segmentStart;

	public ParkourPlayer(UUID uuid, @Nullable ParkourLevel highestLevel, Map<ParkourLevel, ParkourTiming> timings) {
		this.uuid = uuid;
		this.highestLevel = highestLevel;
		this.timings = timings;

		this.currentRunTimings = new HashMap<>();
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setHighestLevel(ParkourLevel highestLevel) {
		this.highestLevel = highestLevel;
	}

	public Optional<ParkourLevel> getHighestLevel() {
		return Optional.ofNullable(highestLevel);
	}

	public void restartParkour() {
		this.hasCheated = false;
		this.hasTouchedPlated = false;
		this.currentRunTimings.clear();
		this.currentSegment = 1;
		this.segmentStart = Instant.now();
	}

	public boolean hasTouchedPlated() {
		return hasTouchedPlated;
	}

	public void setTouchedPlated(boolean hasTouchedPlated) {
		this.hasTouchedPlated = hasTouchedPlated;
	}

	public void setCheated(boolean hasCheated) {
		this.hasCheated = hasCheated;
	}

	public boolean hasCheated() {
		return hasCheated;
	}

	public Map<ParkourLevel, ParkourTiming> getTimings() {
		return timings;
	}

	public Map<ParkourLevel, ParkourTiming> getCurrentRunTimings() {
		return currentRunTimings;
	}

	public void setCurrentRunTimings(Map<ParkourLevel, ParkourTiming> currentRunTimings) {
		this.currentRunTimings = currentRunTimings;
	}

	public void setCurrentSegment(Short currentSegment) {
		this.currentSegment = currentSegment;
	}

	public short getCurrentSegment() {
		return currentSegment;
	}

	public void setSegmentStart(Instant segmentStart) {
		this.segmentStart = segmentStart;
	}

	public Instant getSegmentStart() {
		return segmentStart;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ParkourPlayer that = (ParkourPlayer) o;
		return Objects.equals(uuid, that.uuid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid);
	}
}
