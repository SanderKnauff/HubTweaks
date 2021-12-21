CREATE TABLE pvp_spawn (
    world VARCHAR(255) NOT NULL,
    x DOUBLE NOT NULL,
    y DOUBLE NOT NULL,
    z DOUBLE NOT NULL,
    CONSTRAINT pk_pvp_spawn PRIMARY KEY (world, x, y, z)
);
