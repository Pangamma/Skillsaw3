/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.listeners;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.config.Options;
import com.lumengaming.skillsaw.utility.CText;
import java.util.HashSet;
import java.util.UUID;
import net.md_5.bungee.api.AbstractReconnectHandler;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author prota
 */
public class BungeeWrongHostListener implements Listener{
    private final BungeeMain plugin;
    
    public BungeeWrongHostListener(final BungeeMain plugin) {
        this.plugin = plugin;
    }
    
    private HashSet<UUID> loggingIn = new HashSet<>();
    
    @EventHandler
    public void onWrongHost(final PostLoginEvent e) {
        loggingIn.add(e.getPlayer().getUniqueId());
    }
    
    @EventHandler
    public void onActuallyConnectingToWrongHost(final ServerConnectEvent e) {
        ProxiedPlayer p = e.getPlayer();
        if (!loggingIn.contains(p.getUniqueId())){
            return;
        }
        loggingIn.remove(p.getUniqueId());
        String hostName = 
        e.getPlayer().getPendingConnection().getVirtualHost().getHostName();
        Options.ForcedHostOption match = null;
        for(Options.ForcedHostOption fo : Options.Get().ForcedHosts){
            if ("*".equalsIgnoreCase(fo.Host)){
                if (match == null){
                    match = fo;
                }
            }else if (hostName.equalsIgnoreCase(fo.Host)){
                match = fo;
            }
        }
        
        if (match != null){
            if (match.MessageToPlayerOnJoin != null && !match.MessageToPlayerOnJoin.isEmpty()){
                p.sendMessage(CText.legacy(match.MessageToPlayerOnJoin.replace("&", "§")));
            }
            if (match.ServerToConnectTo != null && !match.ServerToConnectTo.isEmpty()){
                ServerInfo si = plugin.getProxy().getServerInfo(match.ServerToConnectTo);
                if (si != null){
                    if (!e.getTarget().getName().equals(si.getName())){
                        e.setTarget(si);
                    }
                }
            }
        }
    }
}
