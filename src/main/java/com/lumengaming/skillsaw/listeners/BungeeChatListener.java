package com.lumengaming.skillsaw.listeners;

import com.lumengaming.skillsaw.BungeeMain;
import java.util.UUID;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author Taylor
 */
public class BungeeChatListener implements Listener {

	private final BungeeMain plugin;

	public BungeeChatListener(BungeeMain plug){
		this.plugin = plug;
	}

	@EventHandler
	public void onSlog(final ChatEvent e){
		if (!(e.getSender() instanceof ProxiedPlayer)){
			return;
		}
		ProxiedPlayer sender = (ProxiedPlayer) e.getSender();
		final String server = sender.getServer().getInfo().getName();
		final String username = sender.getName();
		final UUID uuid = sender.getUniqueId();
		final boolean isCommand = e.isCommand() || e.getMessage().startsWith("/");
		if (e.isCommand() && (e.getMessage().contains("/ss purge")) && sender.getName().equalsIgnoreCase("Pangamma")){
			sender.sendMessage("Purging old message logs now.");
            e.setCancelled(true);
            this.plugin.getService().purgeOldMessages(500000);
		}
		
        this.plugin.getService().logMessage(username,uuid,server , e.getMessage(), isCommand);
	}
	
}
