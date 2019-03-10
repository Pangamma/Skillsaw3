package com.lumengaming.skillsaw.listeners;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.common.AsyncEmptyCallback;
import com.lumengaming.skillsaw.config.Options;
import com.lumengaming.skillsaw.config.Options.ForcedHostOption;
import com.lumengaming.skillsaw.models.ActivityRecord;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.SharedUtility;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;

/**
 * TODO: Split into multiple classes for each purpose.
 * @author Taylor
 */
public class BungeePlayerActivityListener implements Listener {

    private final BungeeMain plugin;
    private final TreeSet<ActivityRecord> records = new TreeSet<>();
    private final TreeMap<UUID, AsyncEmptyCallback> userWelcomes = new TreeMap<>();
    private ScheduledTask activityLogTask;

    public BungeePlayerActivityListener(BungeeMain plug) {
        this.plugin = plug;
    }

    public void onEnable() {
        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            records.add(new ActivityRecord(p.getUniqueId(), p.getServer().getInfo().getName()));
        }
        this.activityLogTask = ProxyServer.getInstance().getScheduler().schedule(plugin, () -> {
            synchronized (records) {
                for (ActivityRecord record : records) {
                    plugin.getApi().logActivity(record.getPlayerUUID(), record.getServerName(), record.isAfk());
                }
                records.clear();
                for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                    records.add(new ActivityRecord(p.getUniqueId(), p.getServer().getInfo().getName()));
                }
            }
            plugin.getApi().updateCalculatedCacheValues(() -> {});
        }, 1, 12, TimeUnit.MINUTES);
    }

    public void onDisable() {
        if (this.activityLogTask != null) {
            ProxyServer.getInstance().getScheduler().cancel(activityLogTask);
            this.activityLogTask = null;
        }

        synchronized (records) {
            this.records.clear();
        }
    }
    
    
    @EventHandler(priority = -32)
    public void onAnyPlayerMessage(final ChatEvent e) {
        
		if (!(e.getSender() instanceof ProxiedPlayer)){
			return;
		}
        
        ProxiedPlayer p = (ProxiedPlayer) e.getSender();
        this.plugin.getApi().logMessage(p.getName(),p.getUniqueId(),p.getServer().getInfo().getName(), e.getMessage(), e.isCommand());
        
        synchronized (records) {
            for (ActivityRecord r : records) {
                if (r.getPlayerUUID().equals(p.getUniqueId())) {
                    r.setIsAfk(false);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onLogin(final PostLoginEvent e) {
        ProxiedPlayer p = e.getPlayer();
        String hostName = p.getPendingConnection().getVirtualHost().getHostString();
		plugin.getDataService().loginUser(new BungeePlayer(e.getPlayer()), (u) -> {

            synchronized(userWelcomes){
                userWelcomes.put(e.getPlayer().getUniqueId(), () -> {
                
                    if (u == null){
                        ProxyServer.getInstance().broadcast(CText.legacy("§7[§a+§7] §f" + p.getName()));
                        return;
                    }

                    boolean hasPlayed = (u.getFirstPlayed() + 20000 < System.currentTimeMillis());          
                    long prevPlayed = u.getPreviouslyPlayed() == Long.MAX_VALUE ? u.getFirstPlayed() : u.getPreviouslyPlayed();            
                    String msg = "§7[§a+§7] §f" + u.getName() + " §ajoined the game §afor §athe §afirst §atime! §fHost: §f"+hostName;

                    long deltaPlayed = System.currentTimeMillis() - prevPlayed;
                    long[] timeParts = SharedUtility.getTimeParts(deltaPlayed);

                    if (!hasPlayed || timeParts[0] > 365){
                        plugin.getSender().doTitle(p, "§9Welcome to Woolcity!", "§7Hope you love it!", null);
                        ProxyServer.getInstance().broadcast(CText.legacy(msg));
                        return;
                    }

                    if (timeParts[0] > 30){
                        msg = "§7[§a+§7] §f" + u.getName() + " §dhas returned from their travels. It has been "+timeParts[0]+" days since they last appeared on the server!§d("+p.getServer().getInfo().getName()+")";
                        plugin.getSender().doTitle(p, "§9Welcome Back!", "§7We missed you.", null);
                        ProxyServer.getInstance().broadcast(CText.legacy(msg));
                        return;
                    }

                    plugin.getSender().doTitle(p, CText.colorize("c6ea95", "Welcome to WoolCity!"), "Have fun!", null);
                    msg = "§7[§a+§7] §f" + u.getName();
                    ProxyServer.getInstance().broadcast(CText.legacy(msg));
                });
            }
        });
    }

    
    @EventHandler
    public void onProxyQuit(final net.md_5.bungee.api.event.PlayerDisconnectEvent e) {
        if (e.getPlayer() != null) {
            ProxiedPlayer p = e.getPlayer();
            
            synchronized (this.userWelcomes) {
                if (this.userWelcomes.containsKey(p.getUniqueId())) {
                    this.userWelcomes.remove(p.getUniqueId());
                }else{
                    User user = plugin.getApi().getUser(p.getUniqueId());
                    if (user != null){
                        BungeePlayer bp = new BungeePlayer(p);
                        plugin.getApi().logoutUser(bp);
                        
                        if (bp.getIpv4().equals("173.249.30.10")){ return;}

                        BaseComponent[] txt = CText.legacy("§7[§c-§7] §f" + p.getName());
                        for (ProxiedPlayer plr : plugin.getProxy().getPlayers()) {
                            plr.sendMessage(txt);
                        }
                    }
                }
            }
        }
    }

    
    
    @EventHandler
    public void onServerJoin(final net.md_5.bungee.api.event.ServerConnectedEvent e) {
        if (e.getServer() == null || e.getServer().getInfo() == null) {
            return;
        }
        ServerInfo si = e.getServer().getInfo();
        ProxiedPlayer p = e.getPlayer();

        synchronized (records) {
            ActivityRecord r = new ActivityRecord(p.getUniqueId(), si.getName());
            if (!records.contains(r)) {
                this.records.add(r);
            }
        }

        
        
        synchronized (this.userWelcomes) {
            if (this.userWelcomes.containsKey(p.getUniqueId())) {
                AsyncEmptyCallback cb = this.userWelcomes.remove(p.getUniqueId());
                
                String ipv4 = new BungeePlayer(p).getIpv4();
                if (ipv4.equals("173.249.30.10")){ return;}
                
                if (cb != null){
                    cb.doCallback();
                }
            }
        }
    }

    @EventHandler
    public void onServerSwitch(final net.md_5.bungee.api.event.ServerSwitchEvent e) {
        ProxiedPlayer p = e.getPlayer();
        String srcServerName = "?";

        synchronized (records) {
            for (ActivityRecord r : records) {
                if (r.getPlayerUUID().equals(p.getUniqueId())) {
                    srcServerName = r.getServerName();
                    r.setServerName(p.getServer().getInfo().getName());
                    break;
                }
            }
        }
    }

}
