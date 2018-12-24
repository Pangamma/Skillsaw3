/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.utility;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.models.User;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author prota
 */
public class BungeeHelper extends AbstractHelper{
    
    /* Returns null if not found. */
    public static ProxiedPlayer getPlayer(User u){
        if (u != null && u.getUniqueId() != null){
            ProxiedPlayer p = ProxyServer.getInstance().getPlayer(u.getUniqueId());
            return p;
        }
        return null;
    }
}
