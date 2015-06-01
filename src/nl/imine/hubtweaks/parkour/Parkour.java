/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.imine.hubtweaks.parkour;

import java.util.ArrayList;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Sander
 */
public class Parkour {
    
    private static Parkour parkour;
    
    private final ArrayList<ParkourLevel> levels = new ArrayList<>();
    private final ArrayList<ParkourPlayer> players = new ArrayList<>();
        
    public static void init(){
        Parkour.parkour = new Parkour();
        ParkourConfig.getLevels(parkour);
        ParkourConfig.getPlayers(parkour);
    }
    
    public Parkour(){
        ParkourListener.init();
    }
    
    public void addLevel(ParkourLevel level){
        levels.add(level);
    }
    
    public ArrayList<ParkourLevel> getLevels(){
        return this.levels;
    }
    
    public void addPlayer(ParkourPlayer player){
        players.add(player);
    }
        
    public ArrayList<ParkourPlayer> getPlayers(){
        return players;
    }
    
    public ParkourPlayer getPlayer(Player player){
        for(ParkourPlayer p : players){
            if(p.getUUID().equals(player.getUniqueId().toString())){
                return p;
            }
        }
        return null;
    }
    
    public ParkourLevel getLevel(String levelName){
        for(ParkourLevel lvl : levels){
            if(lvl.getColor().toString().equals(levelName)){
                return lvl;
            }
        }
        return new ParkourLevel(-1, DyeColor.BLACK);
    }
    
    public ParkourLevel getLevel(DyeColor levelColor){
        for(ParkourLevel lvl : levels){
            if(lvl.getColor() == levelColor){
                return lvl;
            }
        }
        return new ParkourLevel(-1, DyeColor.BLACK);
    }
    
    public static Parkour getInstance(){
        return Parkour.parkour;
    }
}
