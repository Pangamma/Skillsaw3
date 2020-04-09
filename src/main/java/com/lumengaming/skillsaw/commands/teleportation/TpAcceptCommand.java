/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.commands.teleportation;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.models.TPRequest;
import com.lumengaming.skillsaw.models.XLocation;
import com.lumengaming.skillsaw.utility.ExpireMap;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Stream;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author prota
 */
public class TpAcceptCommand extends BungeeCommand {

  public TpAcceptCommand(BungeeMain plugin) {
    super(plugin, "tpaccept", null, "tpaaccept");
    super.addSyntax(null, true, true, "/tpaccept", "Accept a teleport");
  }

  @Override
  public Iterable<String> onTabCompleteBeforeFiltering(CommandSender cs, String[] args) {
    return new HashSet<>();
  }

  @Override
  public void execute(BungeePlayer cs, String[] args) {
    Stream<ExpireMap.ExpireMapHeapNode<String, TPRequest>> query = plugin.getTeleportRequests().minHeap.stream().filter(x
            -> (x.val.getType() == TPRequest.TpaType.TPA && x.val.getTo().getUniqueId().equals(cs.getUniqueId()))
            || (x.val.getType() == TPRequest.TpaType.TPAHERE && x.val.getFrom().getUniqueId().equals(cs.getUniqueId()))
    );

    Optional<ExpireMap.ExpireMapHeapNode<String, TPRequest>> findFirst = query.findFirst();
    if (!findFirst.isPresent()) {
      cs.sendMessage("§cYou don't have any teleport requests right now.");
      return;
    }
    TPRequest req = plugin.getTeleportRequests().remove(findFirst.get().key);
    if (req == null) {
      cs.sendMessage("How is this even possible?");
      return;
    }

    TPRequest.TpaType type = req.getType();
    if (type == TPRequest.TpaType.TPAHERE) {
      XLocation loc = req.getLoc();
      if (loc == null) {
        cs.sendMessage("§cUmmm. Yeah, nevermind. Something went wrong with that teleport.");
        return;
      }

      req.getTo().sendMessage("§2" + cs.getDisplayName() + " §aaccepted your teleport request.");
      req.getFrom().sendMessage("§aAccepted§2 " + req.getTo().getDisplayName() + "§a's teleport request.");
      plugin.getSender().setLocation(cs.p(), loc, (x) -> {
        if (!x) {
          req.getFrom().sendMessage("§cTeleport failed. Can't connect to the target server.");
          req.getTo().sendMessage("§cTeleport failed. Can't connect to the target server.");
        }
      });
    } else if (type == TPRequest.TpaType.TPA) {
      plugin.getSender().getPlayerLocation(cs.p(), (loc) -> {
        if (loc == null) {
          cs.sendMessage("§cUmmm. Yeah, nevermind. Something went wrong with that teleport.");
          return;
        }

        req.getFrom().sendMessage("§2" + cs.getDisplayName() + " §aaccepted your teleport request.");
        req.getTo().sendMessage("§aAccepted§2 " + req.getFrom().getDisplayName() + "§a's teleport request.");
        plugin.getSender().setLocation((ProxiedPlayer) req.getFrom().getRaw(), loc, (x) -> {
          if (!x) {
            req.getFrom().sendMessage("§cTeleport failed.");
            req.getTo().sendMessage("§cTeleport failed.");
          }
        });
      });
    } else {
      cs.sendMessage("§cThe server is very confused right now.");
    }
  }
}
