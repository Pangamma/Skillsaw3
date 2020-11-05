/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.commands.teleportation;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.models.TPRequest;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.C;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author prota
 */
public class TpaHereCommand extends BungeeCommand {

  public TpaHereCommand(BungeeMain plugin) {
    super(plugin, "tpahere", null, "teleporthere");
    super.addSyntax(Permissions.TPA_HERE, false, false, "/tpahere <target>", "Request another to teleport to you.");
    super.addSyntax(Permissions.TPA_HERE, false, true, "/tpahere *", "Request all players teleport to you.");
  }

  @Override
  public Iterable<String> onTabCompleteBeforeFiltering(CommandSender arg0, String[] arg1) {
    if (arg1.length != 1) {
      return new HashSet<>();
    }
    Set<String> set = this.getOnlinePlayerNames();
    set.add("*");
    return set;
  }

  @Override
  public void execute(BungeePlayer cs, String[] args) {
    if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.TPA_HERE, true)) {
      return;
    }

    if (!cs.isPlayer()) {
      cs.sendMessage(C.ERROR_PLAYERS_ONLY);
      return;
    }

    if (args.length != 1) {
      printHelp2(cs);
      return;
    }

    User to = plugin.getApi().getUser(cs.getUniqueId());

    BaseComponent[] p1 = CText.legacy("§e" + to.getName() + " wants to teleport you to their location! Accept their request? ");
    BaseComponent[] p2 = CText.hoverTextForce("§6/tpaccept§e, ", "Accept the request", "/tpaccept");
    BaseComponent[] p3 = CText.hoverTextForce("§6/tpdeny", "Reject the request", "/tpdeny");
    BaseComponent[] promptForFromUser = CText.merge(p1, p2, p3);

    if (args[0].equals("*")) {
      cs.sendMessage(C.MSG_PROCESSING);
      plugin.getSender().getPlayerLocation((ProxiedPlayer) to.getRawPlayer(), (loc) -> {
        cs.sendMessage("§eSent a request to all players on the server asking them if they would like to teleport to you. Request will be alive for a 2 minutes.");
        for (User from : plugin.getApi().getOnlineUsersReadOnly()) {
          TPRequest req = new TPRequest(from.p(), to.p(), loc, TPRequest.TpaType.TPAHERE);
          if (!plugin.getTeleportRequests().contains(req.getKey())) {
            plugin.getTeleportRequests().put(req.getKey(), req, Duration.ofMinutes(2), (expired) -> {
              cs.sendMessage("§cYour teleport request to " + from.getName() + " has expired.");
              from.sendMessage("§cYour teleport request from " + cs.getName() + " has expired.");
            });
            from.sendMessage(promptForFromUser);
          }
        }
      });
    } else {
      final User from = plugin.getApi().getUserBestOnlineMatch(args[0]);
      if (from == null) {
        cs.sendMessage(C.ERROR_P_NOT_FOUND);
        return;
      }

      cs.sendMessage(C.MSG_PROCESSING);
      plugin.getSender().getPlayerLocation((ProxiedPlayer) to.getRawPlayer(), (loc) -> {
        TPRequest req = new TPRequest(from.p(), to.p(), loc, TPRequest.TpaType.TPAHERE);
        if (!plugin.getTeleportRequests().contains(req.getKey())) {
          cs.sendMessage("§eSent a request to §6" + from.getName() + "§e asking them if they would like to teleport to you.");
          plugin.getTeleportRequests().put(req.getKey(), req, Duration.ofMinutes(1), (expired) -> {
            cs.sendMessage("§cYour teleport request to  §4" + from.getName() + "§c has expired.");
            from.sendMessage("§cYour teleport request from  §4" + cs.getName() + "§c has expired.");
          });
          from.sendMessage(promptForFromUser);
        } else {
          cs.sendMessage("§cA request to §6" + from.getName() + "§e already exists. Please wait until that one expires.");
        }
      });
    }
  }

}
