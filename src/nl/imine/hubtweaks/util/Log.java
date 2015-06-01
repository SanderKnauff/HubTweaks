package nl.imine.hubtweaks.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import nl.imine.hubtweaks.HubTweaks;

public class Log {

    private static final Logger logger = HubTweaks.getInstance().getLogger();

    public static void severe(String Message) {
        logger.log(Level.SEVERE, Message);
    }

    public static void warning(String Message) {
        logger.log(Level.WARNING, Message);
    }

    public static void info(String Message) {
        logger.log(Level.INFO, Message);
    }

    public static void config(String Message) {
        logger.log(Level.CONFIG, Message);
    }

    public static void fine(String Message) {
        logger.log(Level.FINE, Message);
    }

    public static void finer(String Message) {
        logger.log(Level.FINER, Message);
    }

    public static void finest(String Message) {
        logger.log(Level.FINEST, Message);
    }
}
