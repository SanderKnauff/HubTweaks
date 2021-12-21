CREATE TABLE parkour_level (
    level INTEGER NOT NULL,
    bonus BIT(1) NOT NULL DEFAULT TRUE,
    reward VARCHAR(32) NOT NULL,
    CONSTRAINT pk_parkour_player PRIMARY KEY (level)
);

CREATE TABLE parkour_player (
    player_id VARCHAR(36) NOT NULL,
    highest_level INTEGER,
    CONSTRAINT pk_parkour_player PRIMARY KEY (player_id),
    CONSTRAINT fk_highest_level FOREIGN KEY (highest_level) REFERENCES parkour_level(level)
);

CREATE TABLE parkour_timing (
    player_id VARCHAR(36) NOT NULL,
    segment INTEGER NOT NULL,
    obtained TIMESTAMP NOT NULL,
    elapsed_time LONG NOT NULL,
    CONSTRAINT pk_timings PRIMARY KEY (player_id, segment),
    CONSTRAINT fk_timing_segment FOREIGN KEY (segment) REFERENCES parkour_level(level)
);

CREATE TABLE parkour_goal (
    level INTEGER NOT NULL,
    world VARCHAR(255) NOT NULL,
    x INTEGER NOT NULL,
    y INTEGER NOT NULL,
    z INTEGER NOT NULL,
    CONSTRAINT pk_goals PRIMARY KEY (world, x, y, z),
    CONSTRAINT fk_goal_level FOREIGN KEY (level) REFERENCES parkour_level(level)
);
