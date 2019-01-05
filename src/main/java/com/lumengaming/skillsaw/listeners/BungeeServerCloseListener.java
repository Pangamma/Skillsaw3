/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.listeners;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.Options;
import com.lumengaming.skillsaw.utility.BH;
import net.md_5.bungee.api.AbstractReconnectHandler;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author prota
 */
public class BungeeServerCloseListener implements Listener{
    private final BungeeMain plugin;
    
    public BungeeServerCloseListener(final BungeeMain plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onServerKickEvent(final ServerKickEvent ev) {
        final String reason = BaseComponent.toLegacyText(ev.getKickReasonComponent());
        for(String kickReason : Options.Get().ServerClosePlayerMover.KickReasonBlacklist){
            if (reason.contains(kickReason)){
                return;
            }
        }
        
        //<editor-fold defaultstate="collapsed" desc="KickedFROM">
        ServerInfo kickedFrom = null;
        if (ev.getPlayer().getServer() != null) {
            kickedFrom = ev.getPlayer().getServer().getInfo();
        }
        else if (this.plugin.getProxy().getReconnectHandler() != null) {
            kickedFrom = this.plugin.getProxy().getReconnectHandler().getServer(ev.getPlayer());
        }
        else {
            kickedFrom = AbstractReconnectHandler.getForcedHost(ev.getPlayer().getPendingConnection());
            if (kickedFrom == null) {
                kickedFrom = ProxyServer.getInstance().getServerInfo(ev.getPlayer().getPendingConnection().getListener().getServerPriority().get(0));
            }
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Kicked To">
        ServerInfo kickTo = null;
        String kickToName = Options.Get().ServerClosePlayerMover.ReconnectServer;
        if (kickToName != null && !kickToName.isEmpty()){
            kickTo = ProxyServer.getInstance().getServerInfo(kickToName);
            if (kickTo != null && kickedFrom != null && kickTo.equals(kickedFrom)){
                kickTo = null;
            }
        }
        
        int mostPlayers = -1;
        if (kickTo == null && kickedFrom != null){
            for(ServerInfo si : ProxyServer.getInstance().getServers().values()){
                if (si.canAccess(ev.getPlayer())){
                    if (!si.equals(kickedFrom)){
                        if (si.getPlayers().size() > mostPlayers) {
                            mostPlayers = si.getPlayers().size();
                            kickTo = si;
                        }
                    }
                }
            }
        }
        
        
        if (kickedFrom != null && kickedFrom.equals(kickTo)) {
            return;
        }
        
        if (kickTo == null){
            return;
        }
        
        final String[] moveMsg = Options.Get().Strings.ServerClosingMoveMessage.replace("%reason%", reason).split("\n");
        ev.setCancelled(true);
        ev.setCancelServer(kickTo);
        if (moveMsg.length != 1 || !moveMsg[0].equals("")) {
            for (final String line2 : moveMsg) {
                ev.getPlayer().sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', line2)));
            }
        }
    }
}
