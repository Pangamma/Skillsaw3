/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.SpigotMain;
import com.lumengaming.skillsaw.config.SpigotOptions;
import com.lumengaming.skillsaw.models.PvpModeSaveState;
import com.lumengaming.skillsaw.utility.C;
import com.lumengaming.skillsaw.utility.SH;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import com.lumengaming.skillsaw.wrappers.SpigotPlayer;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 *
 * @author prota
 */
public class SpigotPvpModeCommand extends SpigotCommand{
    
    public SpigotPvpModeCommand(SpigotMain main){
        super(main);
    }
    
    @Override
    public void execute(SpigotPlayer cs, String[] args) {
        if (SpigotOptions.Get().PvpMode.IsEnabled == false) return;
        if (cs.isPlayer() == false) { cs.sendMessage(C.ERROR_PLAYERS_ONLY); return; }
        Player p = (Player) cs.getRaw();
        
        HashMap<UUID, PvpModeSaveState> data = plugin.getPvpModeSaveStates();
        boolean isPvpEnabled = data.containsKey(p.getUniqueId());
        if (isPvpEnabled){
            plugin.removePvpPlayer(p);
        }else{
            plugin.addPvpPlayer(p);
        } 
    }

    @Override
    public void printHelp(IPlayer cs) {
        cs.sendMessage("§c/pvpm");
    }
}
