package nl.imine.hubtweaks.parkour.model;

import java.time.Instant;

public record ParkourTiming (
	Instant obtained,
	ParkourLevel segment,
	long elapsedTime
) {
}
