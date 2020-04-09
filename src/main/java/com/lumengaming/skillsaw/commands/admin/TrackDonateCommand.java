/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.commands.admin;

import com.lumengaming.skillsaw.commands.*;
import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.C;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import java.util.HashSet;
import net.md_5.bungee.api.CommandSender;

/**
 *
 * @author prota
 */
public class TrackDonateCommand extends BungeeCommand{

    public TrackDonateCommand(BungeeMain plugin) {
        super(plugin, "trackdonate", null,"trackdonation", "track-donate","track-donation");
        this.addSyntax(Permissions.TRACK_DONATION, true, false, "/trackdonation cost::username::package_name", "For tracking donations from the shops");
    }

    @Override
    public void execute(BungeePlayer csw, String[] args) {
        if (!Permissions.USER_HAS_PERMISSION(csw, Permissions.TRACK_DONATION, true)){
            return;
        }
        
        //{cost}::{player}::{package_name}
        String[] reArgs = String.join(" ", args).split("::");
        double cost = Double.parseDouble(reArgs[0]);
        String username = reArgs[1];
        String packageNm = reArgs[2];
        
        csw.sendMessage(C.MSG_PROCESSING);
        plugin.getApi().logDonation(username, packageNm, cost, () -> {
            csw.sendMessage("§aDonation tracked. Cost="+cost+", user="+username+", package="+packageNm);
            plugin.broadcast("§aHuzzah! §f"+username+"§a has donated §f$"+cost+"§a to the server and has unlocked some perks for themselves. :D");
            User u = plugin.getApi().getUserBestOnlineMatch(username);
            if (u != null && u.p() != null){
                u.sendMessage("§aYou have unlocked the §f"+packageNm+"§a perk. Congratulations!");
            }
        });
    }

  @Override
  public Iterable<String> onTabCompleteBeforeFiltering(CommandSender arg0, String[] arg1) {
    return new HashSet<>();
  }
}
