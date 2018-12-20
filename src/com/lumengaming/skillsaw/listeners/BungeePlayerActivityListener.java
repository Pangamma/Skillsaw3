package com.lumengaming.skillsaw.listeners;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.bungee.models.ActivityRecord;
import com.lumengaming.skillsaw.bungee.utility.CText;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author Taylor
 */
public class BungeePlayerActivityListener implements Listener {

    private final BungeeMain plugin;
    private final TreeSet<ActivityRecord> records = new TreeSet<>();
    private final TreeMap<UUID, String> userStates = new TreeMap<>();
    private ScheduledTask activityLogTask;

    public BungeePlayerActivityListener(BungeeMain plug) {
        this.plugin = plug;
    }

    public void onEnable() {
//        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
//            records.add(new ActivityRecord(p.getUniqueId(), p.getServer().getInfo().getName()));
//        }
//        this.activityLogTask = ProxyServer.getInstance().getScheduler().schedule(plugin, () -> {
//            synchronized (records) {
//                for (ActivityRecord record : records) {
//                    plugin.getService().logActivity(record.getPlayerUUID(), record.getServerName(), record.isAfk());
//                }
//                records.clear();
//                for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
//                    records.add(new ActivityRecord(p.getUniqueId(), p.getServer().getInfo().getName()));
//                }
//            }
//        }, 1, 12, TimeUnit.MINUTES);
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

    @EventHandler
    public void onAnyPlayerMessage(final net.md_5.bungee.api.event.ChatEvent e) {
        if (!(e.getSender() instanceof ProxiedPlayer)) {
            return;
        }
        ProxiedPlayer p = (ProxiedPlayer) e.getSender();
        synchronized (records) {
            if (e.getMessage().equals("/lumencloud debug")) {
                p.sendMessage("recordSize:" + records.size());
                p.sendMessage("statesSize:" + this.userStates.size());
            }
            for (ActivityRecord r : records) {
                if (r.getPlayerUUID().equals(p.getUniqueId())) {
                    r.setIsAfk(false);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onProxyQuit(final net.md_5.bungee.api.event.PlayerDisconnectEvent e) {
        if (e.getPlayer() != null) {
            ProxiedPlayer p = e.getPlayer();
            synchronized (this.userStates) {
                if (this.userStates.containsKey(p.getUniqueId())) {
                    this.userStates.remove(p.getUniqueId());
                    BaseComponent[] txt = CText.legacy("§e" + e.getPlayer().getName() + " §eleft §ethe §egame.");
                    for (ProxiedPlayer plr : e.getPlayer().getServer().getInfo().getPlayers()) {
                        plr.sendMessage(txt);
                    }
                }
            }

        }
    }

    @EventHandler
    public void onProxyJoin(final net.md_5.bungee.api.event.ServerConnectedEvent e) {
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
		
        synchronized (this.userStates) {
            if (!this.userStates.containsKey(p.getUniqueId())) {
                this.userStates.put(p.getUniqueId(), "Joining->");
            }
        }

    }

    @EventHandler
    public void onServerSwitch(final net.md_5.bungee.api.event.ServerSwitchEvent e) {
//        ProxiedPlayer p = e.getPlayer();
//        synchronized (this.userStates) {
//            if (this.userStates.containsKey(p.getUniqueId())) {
//                if ("Joining->".equals(this.userStates.get(p.getUniqueId()))) {
//                    this.userStates.put(p.getUniqueId(), "joined");
//					
//					Collection<ProxiedPlayer> players = BungeeCord.getInstance().getPlayers();
//					
//					if (plugin.getService().hasPlayedBefore(p.getUniqueId())){
//						for (ProxiedPlayer plr : players) {
//							plr.sendMessage("§e" + p.getName() + " §ejoined §ethe §egame §e("+p.getServer().getInfo().getName()+")");
//						}
//					}else{
//						for (ProxiedPlayer plr : players) {
//							plr.sendMessage("§a" + p.getName() + " §ajoined §athe §agame §afor §athe §afirst §atime! §a("+p.getServer().getInfo().getName()+")");
//						}
//					}
//                    //p.sendMessage("§e" + p.getName() + " §ejoined §ethe §egame");	// For some reason this doesn't work on the proxy join event.
//                    return;
//                }
//            }
//        }
//
//        String destServerName = p.getServer().getInfo().getName();
//        String srcServerName = "?";
//
//        synchronized (records) {
//            for (ActivityRecord r : records) {
//                if (r.getPlayerUUID().equals(p.getUniqueId())) {
//                    srcServerName = r.getServerName();
//                    r.setServerName(p.getServer().getInfo().getName());
//                    break;
//                }
//            }
//        }
//        Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();
//        if (!"?".equals(srcServerName) && !"?".equals(destServerName)) {
//            BaseComponent[] txt = CText.legacy("§e" + p.getName() + " §eswitched §eto §ethe §e'" + destServerName + "' §eserver with §6/server §6" + destServerName);
//            BaseComponent[] txt2 = CText.legacy("§e" + p.getName() + " §ejoined §efrom §ethe §e'" + srcServerName + "' §eserver");
//            for (ServerInfo si : servers.values()) {
//                if (si.getName().equals(srcServerName)) {
//                    Collection<ProxiedPlayer> players = si.getPlayers();
//                    for (ProxiedPlayer plr : players) {
//                        if (!plr.getName().equals(p.getName())) {
//                            plr.sendMessage(txt);
//                        }
//                    }
//                } else if (si.getName().equals(destServerName)) {
//                    Collection<ProxiedPlayer> players = si.getPlayers();
//                    for (ProxiedPlayer plr : players) {
//                        plr.sendMessage(txt2);
//                    }
//                }
//            }
//        }
    }

}
