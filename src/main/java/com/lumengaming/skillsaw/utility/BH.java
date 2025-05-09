package com.lumengaming.skillsaw.utility;

import com.lumengaming.skillsaw.models.User;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author prota
 */
public class BH extends AbstractHelper {
    
    /* Returns null if not found. */
    public static ProxiedPlayer getPlayer(User u){
        if (u != null && u.getUniqueId() != null){
            ProxiedPlayer p = ProxyServer.getInstance().getPlayer(u.getUniqueId());
            return p;
        }
        return null;
    }
    
    public static void broadcast(String legacyText) {
//        ProxyServer.getInstance().broadcast(CText.legacy(legacyText));
        if (ProxyServer.getInstance().getPlayer("Pangamma") != null){
            ProxyServer.getInstance().getPlayer("Pangamma").sendMessage(CText.legacy(legacyText));
        }
    }
}
