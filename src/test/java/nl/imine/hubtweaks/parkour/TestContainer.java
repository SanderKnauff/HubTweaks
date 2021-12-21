package nl.imine.hubtweaks.parkour;

import org.testcontainers.containers.MariaDBContainer;

public class TestContainer {

    public static final MariaDBContainer MARIA_DB = (MariaDBContainer) new MariaDBContainer("mariadb:10")
        .withDatabaseName("hub")
        .withEnv("MARIADB_ROOT_PASSWORD", "root");

    static {
        MARIA_DB.start();
    }

}
