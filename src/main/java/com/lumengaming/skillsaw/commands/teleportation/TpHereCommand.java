/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.commands.teleportation;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.Constants;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author prota
 */
public class TpHereCommand extends BungeeCommand{

    public TpHereCommand(BungeeMain plugin) {
        super(plugin, "tphere", null, "teleporthere");
        super.addSyntax(Permissions.TELEPORT_OTHERS, true, false, "/tphere <target>", "Teleport player to you.");
        super.addSyntax(Permissions.TELEPORT_OTHERS, true, true, "/tphere *", "Teleport all players to you.");
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {
        if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.TELEPORT_OTHERS, true)){
            return;
        }
        
        if (!cs.isPlayer()){
            cs.sendMessage(Constants.ERROR_PLAYERS_ONLY);
            return;
        }
        
        if (args.length != 1){
            printHelp2(cs);
            return;
        }
        
        User target =  plugin.getApi().getUser(cs.getUniqueId());
        
        
        if (args[0].equals("*")){
            cs.sendMessage(Constants.MSG_PROCESSING);
            plugin.getSender().getPlayerLocation((ProxiedPlayer) target.getRawPlayer(), (loc) -> {                         
                cs.sendMessage("§eAll players teleported to you.");
                for(User source : plugin.getApi().getOnlineUsersReadOnly()){
                    plugin.getSender().setLocation((ProxiedPlayer) source.getRawPlayer(), loc, (b)->{
                        if (cs.getName().equals(source.getName())){
                            cs.sendMessage("§eteleported to "+target.getName());
                        }else{
                            source.sendMessage("§e"+cs.getName()+" teleported you to "+target.getName());
                        }
                    });
                }
            });
        }else{
            final User source = plugin.getApi().getUserBestOnlineMatch(args[0]);
            if (source == null){
                cs.sendMessage(Constants.ERROR_P_NOT_FOUND);
                return;
            }
            
            cs.sendMessage(Constants.MSG_PROCESSING);
            plugin.getSender().getPlayerLocation((ProxiedPlayer) target.getRawPlayer(), (loc) -> {
                plugin.getSender().setLocation((ProxiedPlayer) source.getRawPlayer(), loc, (b)->{
                    source.sendMessage("§e"+cs.getName()+" teleported you to their location.");
                    target.sendMessage("§e"+source.getName()+" teleported to your location.");
                });
            });
        }
    }
    
}
