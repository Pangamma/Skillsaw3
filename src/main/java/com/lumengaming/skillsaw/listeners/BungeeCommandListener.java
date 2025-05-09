/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.listeners;

import com.lumengaming.skillsaw.BungeeMain;
import java.util.Map;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.md_5.bungee.protocol.packet.Chat;

/**
 *
 * @author User
 */
public class BungeeCommandListener implements Listener {
  
  private final BungeeMain plugin;
  
  public BungeeCommandListener(BungeeMain aThis) {
    this.plugin = aThis;
  }
  
  @EventHandler(priority = EventPriority.HIGH)
  public void onCommandPassthrough(final ChatEvent e) {
    if (e.isCommand()) {
      String arg = e.getMessage().split(" ")[0]; // shouldn't ever be empty, right?
      switch (arg.toLowerCase()) {
        case "/tpa":
        case "/tpahere":
        case "/tpaccept":
        case "/tp":
        case "/tphere":
          
          if (e.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) e.getSender();
            Map<String, String> mods = p.getModList();
            if (p.getServer().getInfo().getName().toLowerCase().contains("rlc")) {
              e.setCancelled(true);
              e.getReceiver().unsafe().sendPacket(new Chat(e.getMessage()));
            }
          }
          break;
        default:
          break;
      }
    }
    
  }
}
