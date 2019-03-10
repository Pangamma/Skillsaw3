/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.commands.teleportation;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.models.XLocation;
import com.lumengaming.skillsaw.utility.C;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author prota
 */
public class TpPosCommand extends BungeeCommand {

    public TpPosCommand(BungeeMain plugin) {
        super(plugin, "tppos", null, "tploc", "tplocation", "tpposition");
        super.addSyntax(Permissions.TELEPORT_SELF, true, false, "/tploc <x> <y> <z> [world] [server]", "Teleport to the location.");
        super.addSyntax(Permissions.TELEPORT_OTHERS, true, false, "/tploc target <x> <y> <z> [world] [server]", "Teleport player to the location");
        super.addSyntax(Permissions.TELEPORT_SELF, true, false, "/tploc x:<x> y:<y> z:<z> pitch:<pitch> yaw:<yaw> w:world [s:server]", "Teleport to the location");
        super.addSyntax(Permissions.TELEPORT_OTHERS, true, false, "/tploc target:<username> x:<x> y:<y> z:<z> pitch:<pitch> yaw:<yaw> [w:world] [s:server]", "Teleport player to the location");
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {
        //<editor-fold defaultstate="collapsed" desc="Boiler Plate">
        if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.TELEPORT_SELF, true)) {
            return;
        }

        if (args.length == 0) {
            printHelp2(cs);
            return;
        }

        XLocation _target = new XLocation();
        User _source = null;
        ProxiedPlayer p = cs.p();

        boolean isExplicitSyntax = true;
        boolean wasPlayerSearchPerformed = false;
        
        //<editor-fold defaultstate="collapsed" desc="Explicit">
        for (String arg : args) {
            if (arg.toLowerCase().startsWith("x:")) {
                _target.X = Double.parseDouble(arg.substring("x:".length()));
            } else if (arg.toLowerCase().startsWith("y:")) {
                _target.Y = Double.parseDouble(arg.substring("y:".length()));
            } else if (arg.toLowerCase().startsWith("z:")) {
                _target.Z = Double.parseDouble(arg.substring("z:".length()));
            } else if (arg.toLowerCase().startsWith("pitch:")) {
                _target.Pitch = Float.parseFloat(arg.substring("pitch:".length()));
            } else if (arg.toLowerCase().startsWith("yaw:")) {
                _target.Yaw = Float.parseFloat(arg.substring("yaw:".length()));
            } else if (arg.toLowerCase().startsWith("w:")) {
                _target.World = arg.substring("w:".length());
            } else if (arg.toLowerCase().startsWith("s:")) {
                _target.Server = arg.substring("s:".length());
            } else if (arg.toLowerCase().startsWith("target:")) {
                _source = plugin.getApi().getUserBestOnlineMatch(arg);
                wasPlayerSearchPerformed = true;
            } else {
                isExplicitSyntax = false;
            }
        }

        if (isExplicitSyntax){
            if (_source == null){
                if (wasPlayerSearchPerformed){
                    cs.sendMessage(C.ERROR_P_NOT_FOUND);
                    return;
                }
                
                if (!cs.isPlayer()) {
                    cs.sendMessage(C.ERROR_PLAYERS_ONLY);
                    return;
                }
                
                _source = plugin.getApi().getUser(cs.getUniqueId());
                if (_source == null){
                    cs.sendMessage(C.ERROR_P_NOT_FOUND);
                    return;
                }
            }else{
                if (!_source.getName().equalsIgnoreCase(cs.getName())){
                    if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.TELEPORT_OTHERS, true)) {
                        return;
                    }
                }
            }
            
            // Source will never be null based on previous logic.
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(_source.getUniqueId());
            
            if (_target.Server == null){
                _target.Server = player.getServer().getInfo().getName();
            }
            
            if (_target.World != null){
                teleportToLocation(cs, player, _target);
            }else{
                XLocation dst = _target;
                plugin.getSender().getPlayerLocation(p, (xloc) -> {
                    dst.World = xloc.World;
                    teleportToLocation(cs, player, dst);
                });
            }
            
            return;
        }
        
        //</editor-fold>
        
        // If it aint returned yet, we only have these syntaxes to consider:
        // /tploc        <x> <y> <z> [world] [server]
        // /tploc target <x> <y> <z> [world] [server]
        if (!(args.length >= 3 && args.length <= 6)){
            printHelp2(cs);
            return;
        }
//        
//        boolean isArg0Numeric = false;
//        try{
//            Double.parseDouble(args[0]);
//            isArg0Numeric = true;
//        }catch(NumberFormatException ex){
//        }
//        
//        boolean isArg3Numeric = false;
//        try{
//            if (args.length >= 4){
//                Double.parseDouble(args[3]);
//                isArg3Numeric = true;
//            }
//        }catch(NumberFormatException ex){
//        }
//        
//        boolean isFirstArgThePlayerName = //args.length == 6;
//            (args.length != 3 && !isArg0Numeric)
//            || args.length == 6
//            || (args.length >= 4 && isArg3Numeric)
//            ;
//
//        if (isFirstArgThePlayerName){
//            _source = plugin.getApi().getUserBestOnlineMatch(args[0]);
//        }
//        if (_source == null) {
//            isFirstArgThePlayerName = false;
//            _source = plugin.getApi().getUser(cs.getUniqueId());
//        }
//        int i = isFirstArgThePlayerName ? 0 : 1;
//        _source = isFirstArgThePlayerName ? 
//            boolean isPlayerNameTheFirstArg = true;
//        
//        
//        
//        
//        switch (args.length) {
//            //<editor-fold defaultstate="collapsed" desc="/tploc <x> <y> <z> [world] [server]">
//            case 5:
//                if (!cs.isPlayer()) {
//                    cs.sendMessage(C.ERROR_PLAYERS_ONLY);
//                    return;
//                }
//                _source = plugin.getApi().getUser(cs.getUniqueId());
//                _target = new XLocation();
//                _target.X = Double.parseDouble(args[0]);
//                _target.Y = Double.parseDouble(args[1]);
//                _target.Z = Double.parseDouble(args[2]);
//                _target.World = args[3];
//                _target.Server = args[4];
//                break;
//            case 4:
//                if (!cs.isPlayer()) {
//                    cs.sendMessage(C.ERROR_PLAYERS_ONLY);
//                    return;
//                }
//                _source = plugin.getApi().getUser(cs.getUniqueId());
//                _target = new XLocation();
//                _target.X = Double.parseDouble(args[0]);
//                _target.Y = Double.parseDouble(args[1]);
//                _target.Z = Double.parseDouble(args[2]);
//                _target.World = args[3];
//                _target.Server = p.getServer().getInfo().getName();
//                break;
//            case 3:
//                if (!cs.isPlayer()) {
//                    cs.sendMessage(C.ERROR_PLAYERS_ONLY);
//                    return;
//                }
//                _source = plugin.getApi().getUser(cs.getUniqueId());
//                _target = new XLocation();
//                _target.X = Double.parseDouble(args[0]);
//                _target.Y = Double.parseDouble(args[1]);
//                _target.Z = Double.parseDouble(args[2]);
//                _target.Server = p.getServer().getInfo().getName();
//                break;
//            //</editor-fold>
//            //<editor-fold defaultstate="collapsed" desc="/tploc x:<x> y:<y> z:<z> pitch:<pitch> yaw:<yaw> w:world [s:server]">
//
//            case 6:
//                _source = plugin.getApi().getUserBestOnlineMatch(args[0]);
////        super.addSyntax(Permissions.TELEPORT_OTHERS, true, false, "/tploc target <x> <y> <z> [world] [server]", "Teleport player to the location");
//            case 7:
//                if (!cs.isPlayer()) {
//                    cs.sendMessage(C.ERROR_PLAYERS_ONLY);
//                    return;
//                }
//                _source = plugin.getApi().getUser(cs.getUniqueId());
//            //        super.addSyntax(Permissions.TELEPORT_SELF, true, false, "/tploc x:<x> y:<y> z:<z> pitch:<pitch> yaw:<yaw> [w:world] [s:server]", "Teleport to the location");
//            case 8:
//                _source = plugin.getApi().getUserBestOnlineMatch(args[0]);
//            //        super.addSyntax(Permissions.TELEPORT_OTHERS, true, false, "/tploc target:<username> x:<x> y:<y> z:<z> pitch:<pitch> yaw:<yaw> [w:world] [s:server]", "Teleport player to the location");
//        }
//
//        // TODO: CHECK IF WORLD NEEDS TO BE FILLED.
//        if (args.length == 1) {
//            if (!cs.isPlayer()) {
//                cs.sendMessage(C.ERROR_PLAYERS_ONLY);
//                return;
//            }
//
//            _source = plugin.getApi().getUser(cs.getUniqueId());
//            _target = plugin.getApi().getUserBestOnlineMatch(args[0]);
//
//        } else if (args.length == 2) {
//            _source = plugin.getApi().getUserBestOnlineMatch(args[0]);
//            _target = plugin.getApi().getUserBestOnlineMatch(args[1]);
//
//            if (_source != null && !_source.getName().equals(cs.getName())) {
//                if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.TELEPORT_OTHERS, true)) {
//                    return;
//                }
//            }
//        }
//
//        if (_source == null) {
//            cs.sendMessage(C.ERROR_TRY_AGAIN_LATER_COMMAND);
//            return;
//        }
//
//        if (_target == null) {
//            cs.sendMessage(C.ERROR_P_NOT_FOUND);
//            return;
//        }
//        //</editor-fold>
//
//        cs.sendMessage(C.MSG_PROCESSING);
//        final User target = _target;
//        final User source = _source;
//        plugin.getSender().getPlayerLocation((ProxiedPlayer) target.getRawPlayer(), (loc) -> {
//            plugin.getSender().setLocation((ProxiedPlayer) source.getRawPlayer(), loc, (b) -> {
//                if (cs.getName().equals(source.getName())) {
//                    cs.sendMessage("§eteleported to " + target.getName());
//                } else {
//                    cs.sendMessage("§e" + source.getName() + " teleported to " + target.getName());
//                    source.sendMessage("§e" + cs.getName() + " teleported you to " + target.getName());
//                }
//                target.sendMessage("§e" + source.getName() + " teleported to your location.");
////                }
//            });
//        });

    }

    private void teleportToLocation(BungeePlayer cs, ProxiedPlayer player, XLocation dest){
        plugin.getSender().setLocation(player, dest, (x) ->{
            if (x){
                if (cs.getName().equals(player.getName())){
                    cs.sendMessage("§eteleported to "+dest.toJson());
                }else{
                    cs.sendMessage("§e"+player.getName()+" teleported to "+dest.toJson());
                    player.sendMessage("§e"+cs.getName()+" teleported you to "+dest.toJson());
                }
            }else{
                cs.sendMessage("§cFailed to teleport.");
            }
        });
    }
}
