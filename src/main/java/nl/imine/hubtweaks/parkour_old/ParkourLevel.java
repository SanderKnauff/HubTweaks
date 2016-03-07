/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.imine.hubtweaks.parkour_old;

import org.bukkit.DyeColor;

/**
 *
 * @author Sander
 */
public class ParkourLevel {

    private int level;
    private DyeColor color;

    public ParkourLevel(int level, DyeColor color) {
        this.level = level;
        this.color = color;
    }

    public int getLevel() {
        return this.level;
    }

    public DyeColor getColor() {
        return this.color;
    }

    @Override
    public String toString() {
        return this.level + " || " + this.color.toString();
    }

    public boolean equals(ParkourLevel plvl) {
        return (this.getColor().equals(plvl.getColor()) && this.getLevel() == plvl.getLevel());
    }
}
