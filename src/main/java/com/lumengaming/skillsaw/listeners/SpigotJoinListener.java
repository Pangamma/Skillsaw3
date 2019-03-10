/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.listeners;

import com.lumengaming.skillsaw.SpigotMain;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author prota
 */
public class SpigotJoinListener implements Listener {
    private final SpigotMain plugin;

    public SpigotJoinListener(SpigotMain plugin){
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage("");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage("");
    }
    
    @EventHandler
    public void onKickTooManyItems(PlayerKickEvent e){
        String reason = e.getReason();
        if (reason.toLowerCase().contains("is larger than protocol maximum")){
            if (reason.toLowerCase().contains("compressed packet")){
                UUID uuid = e.getPlayer().getUniqueId();
                Bukkit.broadcastMessage("SHIZAM");
                System.out.println("SHIZAMMMM");
            }
        }
    }
}
