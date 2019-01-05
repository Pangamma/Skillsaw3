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
import java.util.Optional;
import java.util.stream.Stream;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author prota
 */
public class TpDenyCommand extends BungeeCommand{

    public TpDenyCommand(BungeeMain plugin) {
        super(plugin, "tpdeny", null, "tpadeny");
        super.addSyntax(null, true, true, "/tpdeny", "Deny a teleport");
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {
        Stream<ExpireMap.ExpireMapHeapNode<String, TPRequest>> query = plugin.getTeleportRequests().minHeap.stream().filter(x -> 
            (x.val.getType() == TPRequest.TpaType.TPA &&  x.val.getTo().getUniqueId().equals(cs.getUniqueId()))
            ||
            (x.val.getType() == TPRequest.TpaType.TPAHERE &&  x.val.getFrom().getUniqueId().equals(cs.getUniqueId()))
        );
        
        Optional<ExpireMap.ExpireMapHeapNode<String, TPRequest>> findFirst = query.findFirst();
        if (!findFirst.isPresent()) {
            cs.sendMessage("§cYou don't have any teleport requests right now.");
            return;
        }
        TPRequest req = plugin.getTeleportRequests().remove(findFirst.get().key);
        if (req == null){
            cs.sendMessage("How is this even possible?");
            return;
        }
        
        TPRequest.TpaType type = req.getType();
        if (type == TPRequest.TpaType.TPAHERE){            
            req.getTo().sendMessage("§4"+cs.getDisplayName()+" §crejected your teleport request.");
            req.getFrom().sendMessage("§cRejected §4"+req.getTo().getDisplayName()+"§c's teleport request.");
        }else if (type == TPRequest.TpaType.TPA){
                req.getFrom().sendMessage("§4"+cs.getDisplayName()+" §crejected your teleport request.");
                req.getTo().sendMessage("§cRejected §4"+req.getFrom().getDisplayName()+"§c's teleport request.");
        }else{
            cs.sendMessage("§cThe server is very confused right now.");
        }
    }
}
