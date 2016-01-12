package nl.imine.hubtweaks.util;

import java.util.logging.Logger;
import nl.imine.hubtweaks.HubTweaks;

public class Log {

    private static final Logger logger = HubTweaks.getInstance().getLogger();

    public static void severe(String message) {
        logger.severe(message);
    }

    public static void warning(String message) {
        logger.warning(message);
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void config(String message) {
        logger.config(message);
    }

    public static void fine(String message) {
        logger.fine(message);
    }

    public static void finer(String message) {
        logger.finer(message);
    }

    public static void finest(String message) {
        logger.finest(message);
    }
}
