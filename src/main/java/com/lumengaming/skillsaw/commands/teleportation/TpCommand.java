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
public class TpCommand extends BungeeCommand{

    public TpCommand(BungeeMain plugin) {
        super(plugin, "tp", null, "teleport");
        super.addSyntax(Permissions.TELEPORT_SELF, true, false, "/tp <target>", "Teleport to the player.");
        super.addSyntax(Permissions.TELEPORT_OTHERS, true, false, "/tp <source> <target>", "Teleport to the player.");
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {
        //<editor-fold defaultstate="collapsed" desc="Boiler Plate">
        if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.TELEPORT_SELF, true)){
            return;
        }
        
        if (!cs.isPlayer()){
            cs.sendMessage(Constants.ERROR_PLAYERS_ONLY);
            return;
        }
        
        if (args.length == 0){
            printHelp2(cs);
            return;
        }
        
        User _target = null;
        User _source = null;
        
        if (args.length == 1){
            _source = plugin.getApi().getUser(cs.getUniqueId());
            _target = plugin.getApi().getUserBestOnlineMatch(args[0]);
        }else if (args.length == 2){
            _source = plugin.getApi().getUserBestOnlineMatch(args[0]);
            _target = plugin.getApi().getUserBestOnlineMatch(args[1]);
            
            if (_source != null && !_source.getName().equals(cs.getName())){
                if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.TELEPORT_OTHERS, true)){
                    return;
                }
            }
        }
        
        if (_source == null){
            cs.sendMessage(Constants.ERROR_TRY_AGAIN_LATER_COMMAND);
            return;
        }
        
        if (_target == null){
            cs.sendMessage(Constants.ERROR_P_NOT_FOUND);
            return;
        }
        //</editor-fold>
        
        cs.sendMessage(Constants.MSG_PROCESSING);
        final User target = _target;
        final User source = _source;
        plugin.getSender().getPlayerLocation((ProxiedPlayer) target.getRawPlayer(), (loc) -> {
            plugin.getSender().setLocation((ProxiedPlayer) source.getRawPlayer(), loc, (b)->{
                if (cs.getName().equals(source.getName())){
                    cs.sendMessage("§eteleported to "+target.getName());
                }else{
                    cs.sendMessage("§e"+source.getName()+" teleported to "+target.getName());
                    source.sendMessage("§e"+cs.getName()+" teleported you to "+target.getName());
                }
                target.sendMessage("§e"+source.getName()+" teleported to your location.");
//                }
            });
        });
        
    }
    
}
