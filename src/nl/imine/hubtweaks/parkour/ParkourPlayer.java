/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.imine.hubtweaks.parkour;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import nl.imine.hubtweaks.util.Log;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Sander
 */
public class ParkourPlayer {

    private final String uuid;
    private ParkourLevel level;
    private boolean isBuilding;

    public ParkourPlayer(String uuid, ParkourLevel level) {
        this.uuid = uuid;
        this.level = level;
        this.save();
    }

    public String getUUID() {
        return this.uuid;
    }

    public void setLevel(ParkourLevel level) {
        this.level = level;
    }

    public ParkourLevel getLevel() {
        return level;
    }

    public void setBuilding(boolean isBuilding) {
        this.isBuilding = isBuilding;
    }

    public boolean isBuilding() {
        return isBuilding;
    }

    public void save() {
        File f = new File(ParkourConfig.CONFIGPATH + "Players" + File.separator + uuid + ".yml");
        try {
            f.createNewFile();
        } catch (IOException e) {
            Log.warning("Cannot create file || " + e.getMessage());
        }
        if (this.level != null) {
            YamlConfiguration config = new YamlConfiguration();
            try {
                config.load(f);
                config.set("Level", this.level.getColor().toString());
                config.save(f);
            } catch (FileNotFoundException e) {
                Log.warning("Exception finding file: " + f.getPath() + " || " + e.getMessage());
            } catch (IOException e) {
                Log.warning("Exception opening file: " + f.getPath() + " || " + e.getMessage());
            } catch (InvalidConfigurationException e) {
                Log.warning("Exception saving ymlfile: " + f.getPath() + " || " + e.getMessage());
            }
        }
    }
    
    @Override
    public String toString(){
        return uuid + " || " + this.level.toString();
    }
}
