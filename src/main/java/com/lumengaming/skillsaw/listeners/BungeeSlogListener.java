package com.lumengaming.skillsaw.listeners;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.common.AsyncCallback;
import com.lumengaming.skillsaw.config.Options;
import com.lumengaming.skillsaw.models.PlayerServerPingInfo;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.ExpireMap;
import com.lumengaming.skillsaw.utility.ExpireMap.ExpireMapHeapNode;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 *
 * @author Taylor
 */
public class BungeeSlogListener implements Listener {

	private final BungeeMain plugin;

    private final ExpireMap<String, PlayerServerPingInfo> pingCache = new ExpireMap<>();
    
    private final HashMap<String, String> motdMap = new HashMap<>();
    
	public BungeeSlogListener(BungeeMain plug){
		this.plugin = plug;
	}
    
    
	@EventHandler
	public void onCustomMOTD(final ChatEvent e){
        if (e.getMessage().startsWith("/pingspy ")){
            String str = e.getMessage().replace("/pingspy ","");
            String ip = str.substring(0, str.indexOf(" "));
            String msg = str.substring(ip.length()+1);
            
            if (!ip.contains(".")){
                PlayerServerPingInfo match = null;
                for(ExpireMapHeapNode<String, PlayerServerPingInfo> node : pingCache.minHeap){
                    if (node.val.Username != null){
                        if (node.val.Username.contains(ip)){
                            if (match == null) match = node.val;
                            else if (match.Username.length() > node.val.Username.length()){
                                match = node.val;
                            }
                        }
                    }
                }
                if (match != null){
                    ip = match.IpAddress;
                }
            }
            msg = msg.replace("&", "§");
            motdMap.put(ip, msg);
            ((ProxiedPlayer) e.getSender()).sendMessage("SET MOTD FOR '"+ip+"' to '"+msg+"'");
            e.setCancelled(true);
        }
    }
    
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onProxyPingMOTD(final ProxyPingEvent e){
        PendingConnection conn = e.getConnection();
        if (conn == null) return;
        
        final String ipPlayer;
        final String hostServer;
        InetSocketAddress vHost = conn.getVirtualHost();
        if (vHost != null){
            hostServer = vHost.getHostString();
        }else{
            return;
        }
        
        InetSocketAddress addr = conn.getAddress();
        if (addr != null){
            ipPlayer = addr.getHostString();
        }else{
            return;
        }
        
        for(Options.ForcedHostOption opt : Options.Get().ForcedHosts){
            if (opt.IsCustomMotdEnabled){
                if (opt.Host.equalsIgnoreCase(hostServer)){
                    ServerPing ping = e.getResponse();
                    ping.setDescription(opt.MOTD);
                    e.setResponse(ping);
                    break;
                }
            }
        }
        
        if (motdMap.containsKey(ipPlayer)){
            ServerPing ping = e.getResponse();
            ping.setDescription(motdMap.get(ipPlayer));
            e.setResponse(ping);
        }
        
    }
    
	@EventHandler
	public void onProxyPing(final ProxyPingEvent e){
        PendingConnection conn = e.getConnection();
        if (conn == null) return;
        
        final String ipPlayer;
        final String hostServer;
        InetSocketAddress vHost = conn.getVirtualHost();
        if (vHost != null){
            hostServer = vHost.getHostString();
        }else{
            return;
        }
        
        InetSocketAddress addr = conn.getAddress();
        if (addr != null){
            ipPlayer = addr.getHostString();
        }else{
            return;
        }
        
        AsyncCallback<PlayerServerPingInfo> infoCallback = (PlayerServerPingInfo t) -> {
            ArrayList<User> users = plugin.getApi().getOnlineUsersReadOnly();
            String identifier = t.Username;
            if (identifier == null) identifier = t.IpAddress;
                
            for(User user : users){
                if (user == null) {
                    continue;
                }
                
                if (!user.isSlogging()){
                    continue;
                }

                if (user.getSlogSettings().PingSpy == 0){
                    continue;
                }
                if (user.getSlogSettings().PingSpy == 1 && t.Username == null){
                    continue;
                }
                
                user.sendMessage("§6§o[PingSpy] "+identifier+" pinged "+t.HostName+".");
            }
        };
        
        
        if (pingCache.contains(ipPlayer)){
            PlayerServerPingInfo info = pingCache.get(ipPlayer);
            info.HostName = hostServer;
            infoCallback.doCallback(info);
        }else{
            plugin.getApi().getOfflineUsersByIP(ipPlayer, (ArrayList<User> us) ->{
                if (!us.isEmpty()){
                    User uPreferred = us.get(0);
                    PlayerServerPingInfo pi = new PlayerServerPingInfo(uPreferred.getName(), uPreferred.getUniqueId(), ipPlayer, hostServer);
                    pingCache.putWithBumpableCache(ipPlayer, pi, Duration.ofMinutes(5), null);
                    infoCallback.doCallback(pi);
                    
                    for(User u : us){
                        u.setLastPingHost(hostServer);
                        u.setLastPingTime(System.currentTimeMillis());
                        plugin.getApi().saveUser(u,true);
                    }
                    
                }else{
                    PlayerServerPingInfo pi = new PlayerServerPingInfo(null, null, ipPlayer, hostServer);
                    pingCache.put(ipPlayer, pi, Duration.ofMinutes(5), null);
                    infoCallback.doCallback(pi);
                }
            });
        }
        
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
