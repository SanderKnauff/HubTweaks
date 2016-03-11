/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.imine.hubtweaks.parkour;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Sander
 */
public class ParkourPlayer {

	private final String uuid;
	private ParkourLevel level;
	private boolean isBuilding;

	// Not stored between reboots
	private boolean hasReachedTop = false;
	private boolean hasTouchedPlate = false;

	public ParkourPlayer(String uuid, ParkourLevel level) {
		this.uuid = uuid;
		this.level = level;
		this.save();
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(UUID.fromString(uuid));
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
			System.err.println("Cannot create file || " + e.getMessage());
		}
		if (this.level != null) {
			YamlConfiguration config = new YamlConfiguration();
			try {
				config.load(f);
				config.set("Level", this.level.getColor().toString());
				config.save(f);
			} catch (FileNotFoundException e) {
				System.err.println("Exception finding file: " + f.getPath() + " || " + e.getMessage());
			} catch (IOException e) {
				System.err.println("Exception opening file: " + f.getPath() + " || " + e.getMessage());
			} catch (InvalidConfigurationException e) {
				System.err.println("Exception saving ymlfile: " + f.getPath() + " || " + e.getMessage());
			}
		}
	}

	@Override
	public String toString() {
		return uuid + " || " + this.level.toString();
	}

	/**
	 * @return the hasReachedTop
	 */
	public boolean hasReachedTop() {
		return hasReachedTop;
	}

	/**
	 * @param hasReachedTop
	 *            the hasReachedTop to set
	 */
	public void setReachedTop(boolean hasReachedTop) {
		this.hasReachedTop = hasReachedTop;
	}

	/**
	 * @return the hasTouchedPlate
	 */
	public boolean hasTouchedPlate() {
		return hasTouchedPlate;
	}

	/**
	 * @param hasTouchedPlate
	 *            the hasTouchedPlate to set
	 */
	public void setTouchedPlate(boolean hasTouchedPlate) {
		this.hasTouchedPlate = hasTouchedPlate;
	}
}
