/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.imine.hubtweaks.parkour_old;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import nl.imine.hubtweaks.HubTweaks;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Sander
 */
public class ParkourConfig {

    public static final String CONFIGPATH = HubTweaks.getInstance().getDataFolder().getPath() + File.separator
            + "Parkour" + File.separator;

    public static void getLevels(Parkour parkour) {
        if (!new File(ParkourConfig.CONFIGPATH).exists()) {
            new File(ParkourConfig.CONFIGPATH).mkdir();
        }
        File f = new File(ParkourConfig.CONFIGPATH + "Levels.yml");
        try {
            f.createNewFile();
        } catch (IOException e) {
            System.err.println("Cannot create file || " + e.getMessage());
        }
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(f);
            for (String s : config.getKeys(false)) {
                ConfigurationSection cs = config.getConfigurationSection(s);
                parkour.addLevel(new ParkourLevel(Integer.valueOf(s), DyeColor.valueOf(cs.getString("Color"))));
            }
        } catch (FileNotFoundException e) {
            System.err.println("Exception finding file: " + f.getPath() + " || " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Exception opening file: " + f.getPath() + " || " + e.getMessage());
        } catch (InvalidConfigurationException e) {
            System.err.println("Exception reading ymlfile: " + f.getPath() + " || " + e.getMessage());
        }
    }

    public static void getPlayers(Parkour parkour) {
        System.out.println("Loading Parkour");
        if (!new File(ParkourConfig.CONFIGPATH).exists()) {
            new File(ParkourConfig.CONFIGPATH).mkdir();
        }
        if (!new File(ParkourConfig.CONFIGPATH + "Players").exists()) {
            new File(ParkourConfig.CONFIGPATH + "Players").mkdir();
        }
        for (File f : new File(ParkourConfig.CONFIGPATH + "Players").listFiles()) {
            YamlConfiguration config = new YamlConfiguration();
            try {
                config.load(f);
                parkour.addPlayer(
                        new ParkourPlayer(f.getName().split("\\.")[0], parkour.getLevel(config.getString("Level"))));
            } catch (FileNotFoundException e) {
                System.err.println("Exception finding file: " + f.getPath() + " || " + e.getMessage());
                break;
            } catch (IOException e) {
                System.err.println("Exception opening file: " + f.getPath() + " || " + e.getMessage());
                break;
            } catch (InvalidConfigurationException e) {
                System.err.println("Exception reading ymlfile: " + f.getPath() + " || " + e.getMessage());
                break;
            }
        }
    }
}
