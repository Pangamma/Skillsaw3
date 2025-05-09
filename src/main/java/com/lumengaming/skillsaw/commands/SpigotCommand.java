/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.SpigotMain;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import com.lumengaming.skillsaw.wrappers.SpigotPlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author prota
 */
public abstract class SpigotCommand implements CommandExecutor {

    protected final SpigotMain plugin;

    public SpigotCommand(SpigotMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender csw, org.bukkit.command.Command arg1, String arg2, String[] args) {
        SpigotPlayer cs = new SpigotPlayer(csw);
        
        try{
            this.execute(cs, args);
            return true;
        }catch(ArrayIndexOutOfBoundsException | NumberFormatException ex){
            printHelp(cs);
        }
        
        return false;
    }
    
    public abstract void execute(SpigotPlayer cs, String[] args);

    public abstract void printHelp(IPlayer cs);
     
}
