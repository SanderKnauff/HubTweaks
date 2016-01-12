/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.imine.hubtweaks.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Sander
 */
public class TestCmd extends Command {

    public TestCmd(){
        super("TestCmd");
    }
    
    @Override
    public boolean execute(CommandSender cs, String string, String[] strings) {
        if(cs instanceof Player){
            ((Player) cs).sendMessage("LolCmd");
        } else {
            System.out.println("LolnoCmd");
        }
        return true;
    }
    
}
