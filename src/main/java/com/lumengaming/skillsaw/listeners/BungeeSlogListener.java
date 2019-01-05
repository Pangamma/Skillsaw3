package com.lumengaming.skillsaw.listeners;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.Options;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.Constants;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.utility.SharedUtility;
import java.util.ArrayList;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author Taylor
 */
public class BungeeSlogListener implements Listener {

	private final BungeeMain plugin;

	public BungeeSlogListener(BungeeMain plug){
		this.plugin = plug;
	}

	@EventHandler
	public void onChat(final ChatEvent e){
        if (e.isCancelled()) return;
		if (!(e.getSender() instanceof ProxiedPlayer)){
			return;
		}
        
        if (!e.isCommand() || e.getMessage().toLowerCase().startsWith("/ch:")) 
            return;
        
		User chatter = plugin.getApi().getUser(((ProxiedPlayer) e.getSender()).getUniqueId());
        
		ProxiedPlayer sender = (ProxiedPlayer) e.getSender();
        ArrayList<User> users = plugin.getApi().getOnlineUsersReadOnly();
        for(User user : users){
            if (user == null) {
                continue;
            }

            if (!user.isSlogging()){
                continue;
            }

            ProxiedPlayer p = (ProxiedPlayer) user.getRawPlayer();
            if (!user.getSlogSettings().IsGlobal){
                if (p != null && p.getServer() != null && p.getServer().getInfo().getName()
                    .equalsIgnoreCase(sender.getServer().getInfo().getName())){
                }else{
                    continue; // Continue if non global and user is on diff server
                }
            }
            
            if (chatter.getActivityScore() <= user.getSlogSettings().ShowOnlyIfActivityBelow){
                user.sendMessage(CText.hoverTextSuggest("§6§o[Slog] "+sender.getName()+" "+e.getMessage(), "Click to copy", e.getMessage()));
            }
        }
    }
}
