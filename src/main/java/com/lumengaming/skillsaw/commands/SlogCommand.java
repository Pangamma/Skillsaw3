/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.models.SlogSettings;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;

/**
 *
 * @author prota
 */
public class SlogCommand extends BungeeCommand{

    public SlogCommand(BungeeMain plugin) {
        super(plugin, "sslog", null, "slog");
        super.addSyntax(Permissions.SLOG, true, false, "/slog", "Get slog help.");
        super.addSyntax(Permissions.SLOG, true, true, "/slog off", "Disable slog");
        super.addSyntax(Permissions.SLOG, true, true, "/slog -g", "Show slog for all servers.");
        super.addSyntax(Permissions.SLOG, true, true, "/slog -s", "Only show slog for the curret server.");
        super.addSyntax(Permissions.SLOG, true, true, "/slog -a<50", "Only show slog users with \nactivity score below \nthe specified amount.");
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {
        if (args.length == 0) { super.printHelp2(cs); return; }
        User u = plugin.getApi().getUser(cs.getUniqueId());
        SlogSettings slog = u.getSlogSettings();
        slog.reset();
        slog.IsEnabled = true;
        for(int i = 0; i < args.length; i++){
            if (args[i].equalsIgnoreCase("-g")){
                slog.IsGlobal = true;
            }else if (args[i].equalsIgnoreCase("-s")){
                slog.IsGlobal = false;
            }else if (args[i].equalsIgnoreCase("-a<")){
                slog.ShowOnlyIfActivityBelow = Short.parseShort(args[i+1]);
            }else if (args[i].toLowerCase().startsWith("-a<")){
                slog.ShowOnlyIfActivityBelow = Short.parseShort(args[i].replace("-a<",""));
            }else if (args[i].equalsIgnoreCase("off") || args[i].equalsIgnoreCase("0") || args[i].equalsIgnoreCase("false")){
                slog.IsEnabled = false;
            }
        }
        
        plugin.getApi().saveUser(u);
        
        if (slog.IsEnabled == false){
            u.sendMessage("§6§oNo longer slogging.");
        }else{
            String s = "§6§oHappy slogging! :D ";
            s += (slog.IsGlobal) ? "=> [Global]" : "=> [Server]";
            if (slog.ShowOnlyIfActivityBelow != Short.MAX_VALUE){
                s += " [Activity < "+slog.ShowOnlyIfActivityBelow+"]";
            }
             u.sendMessage(s);
        }
    }
    
}
