/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.bridge;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.common.AsyncEmptyCallback;
import com.lumengaming.skillsaw.common.AsyncCallback;
import com.lumengaming.skillsaw.utility.BagMap;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.Constants;
import com.lumengaming.skillsaw.models.XLocation;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.packet.Title;

public class BungeeSender implements Listener {

    private final BungeeMain plugin;
    private final Gson gson;
    private final BagMap<Object> map = new BagMap<>();

    public BungeeSender(BungeeMain main) {
        this.plugin = main;
        this.gson = new Gson();
    }

    @EventHandler
    public void onMessageEvent(PluginMessageEvent e) throws IOException {
        if (!e.getTag().equals(Constants.CH_RootChannel)) {
            return;
        }

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));
        String subchannel = in.readUTF();
        switch (subchannel) {
            case Constants.CH_GetPlayerLocation:
                _getPlayerLocation(in);
                break;
            case Constants.CH_SetPlayerLocation:
                _setLocation(in);
                break;
            case Constants.CH_PlaySoundForPlayer:
                _playSoundForPlayer(in);
                break;
            default:
                break;
        }
    }

    //<editor-fold defaultstate="collapsed" desc="GetLocation">
    public void getPlayerLocation(ProxiedPlayer p, AsyncCallback<XLocation> callback) {
        long key = 0;
        try {
            ServerInfo info = p.getServer().getInfo();
            String serverId = info.getName();
            key = this.map.push(callback);
            byte[] data = new GetPlayerLocationRequest(key, p.getUniqueId().toString(), serverId).ToBytes();
            p.getServer().sendData(Constants.CH_RootChannel, data);
        } catch (IOException ex) {
            Logger.getLogger(BungeeSender.class.getName()).log(Level.SEVERE, null, ex);
            AsyncCallback<XLocation> pop = (AsyncCallback<XLocation>) this.map.pop(key);
            pop.doCallback(null);
        }
    }

    private void _getPlayerLocation(DataInputStream in) throws IOException {
        GetPlayerLocationResponse data = new GetPlayerLocationResponse().FromBytes(in);
        AsyncCallback<XLocation> pop = (AsyncCallback<XLocation>) this.map.pop(data.Key);
        if (pop != null) {
            pop.doCallback(data.Loc);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="SetLocation">
    public void setLocation(ProxiedPlayer p, XLocation loc, AsyncCallback<Boolean> callback) {
        try {
            String serverName = p.getServer().getInfo().getName();
            ServerInfo si = ProxyServer.getInstance().getServers().get(loc.Server);
            if (si == null) {
                plugin.getLogger().log(Level.WARNING, "Requested server (" + loc.Server + ") does not exist.");
                callback.doCallback(false);
                return;
            }
            
            if (!serverName.equalsIgnoreCase(loc.Server)) {
                p.connect(si);
            }
            
            long key = this.map.push(callback);
            byte[] data = new SetPlayerLocationRequest(key, p.getUniqueId(), loc).ToBytes();
//            ByteArrayDataOutput out = ByteStreams.newDataOutput();
//            out.writeUTF(Constants.CH_SetPlayerLocation);
//            out.writeLong(key);
//            out.writeUTF(p.getUniqueId().toString());
//            String json = gson.toJson(loc);
//            out.writeUTF(json);
            p.getServer().sendData(Constants.CH_RootChannel, data);
        } catch (IOException ex) {
            Logger.getLogger(BungeeSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void _setLocation(DataInputStream in) throws IOException {
        BooleanResponse rsp = new BooleanResponse().FromBytes(in);
        long key = rsp.Key;
        Boolean b = rsp.Value;
        AsyncCallback<Boolean> pop = (AsyncCallback<Boolean>) this.map.pop(key);
        if (pop != null) {
            pop.doCallback(b);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="SetLocation">
    public void playSoundForPlayer(ProxiedPlayer p, String soundName, AsyncCallback<Boolean> callback) {
        try {
            String serverName = p.getServer().getInfo().getName();
            long key = this.map.push(callback);
            byte[] data = new PlaySoundForPlayerRequest(key, p.getUniqueId().toString(), soundName).ToBytes();
            p.getServer().sendData(Constants.CH_RootChannel, data);
        } catch (IOException ex) {
            Logger.getLogger(BungeeSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void _playSoundForPlayer(DataInputStream in) throws IOException {
        long key = in.readLong();
        Boolean b = in.readBoolean();
        AsyncCallback<Boolean> pop = (AsyncCallback<Boolean>) this.map.pop(key);
        if (pop != null) {
            pop.doCallback(b);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="CompositeEffect">
    public void doTitle(ProxiedPlayer p,String titleText, String subTitle, String sendInChat) {
        net.md_5.bungee.api.Title title = ProxyServer.getInstance().createTitle();
        title.title(CText.legacy(titleText))
            .subTitle(CText.legacy(subTitle))
            .send(p);
        if (sendInChat != null) p.sendMessage(CText.legacy(sendInChat));
    }
    
    public void doLevelUpEffect(ProxiedPlayer p,String subTitle, String sendInChat, AsyncCallback<Boolean> callback) {
        this.doCompositeEffect(p, CompositeEffectType.LevelUp, callback);
        net.md_5.bungee.api.Title title = ProxyServer.getInstance().createTitle();
        title.title(new ComponentBuilder("Level-Up!").color(ChatColor.DARK_GREEN).create())
            .subTitle(new ComponentBuilder(subTitle).color(ChatColor.GRAY).create())
            .send(p);
        if (sendInChat != null) p.sendMessage(CText.legacy(sendInChat));
    }

    public void doLevelDownEffect(ProxiedPlayer p, String subTitle, String sendInChat, AsyncCallback<Boolean> callback) {
        this.doCompositeEffect(p, CompositeEffectType.LevelDown, callback);
        net.md_5.bungee.api.Title title = ProxyServer.getInstance().createTitle();
        title.title(CText.legacy("§4Level-Down")).subTitle(CText.legacy("§7"+subTitle)).send(p);
        if (sendInChat != null) p.sendMessage(CText.legacy(sendInChat));
    }

    public void doScoldEffect(ProxiedPlayer p, String titleText, String subTitle, AsyncCallback<Boolean> callback) {
        this.doCompositeEffect(p, CompositeEffectType.Scold, callback);
        net.md_5.bungee.api.Title title = ProxyServer.getInstance().createTitle();
        title.title(CText.legacy("§4"+titleText)).subTitle(CText.legacy("§7"+subTitle)).send(p);
    }

    public void doHmmmEffect(ProxiedPlayer p, AsyncCallback<Boolean> callback) {
        this.doCompositeEffect(p, CompositeEffectType.Hmmm, callback);
    }

    public void doNameMentionedEffect(ProxiedPlayer p, AsyncCallback<Boolean> callback) {
        this.doCompositeEffect(p, CompositeEffectType.NamePling, callback);
    }

    private void doCompositeEffect(ProxiedPlayer p, CompositeEffectType t, AsyncCallback<Boolean> c){
        try {
            long key = this.map.push(c);
            byte[] data = new PlayCompositeEffectRequest(key, p.getUniqueId(), t).ToBytes();
            p.sendData(Constants.CH_RootChannel, data);
            this.map.pop(key);
            c.doCallback(true);
        } catch (IOException ex) {
            Logger.getLogger(BungeeSender.class.getName()).log(Level.SEVERE, null, ex);
            c.doCallback(false);
        }
    }
    
    public void doReviewListUpdatedEffect(AsyncEmptyCallback callback) {
        Collection<ProxiedPlayer> players = ProxyServer.getInstance().getPlayers();
        long key = this.map.push(callback);

        try {
            for (ProxiedPlayer p : players) {
                byte[] data = new PlayCompositeEffectRequest(key, p.getUniqueId(), CompositeEffectType.ReviewListUpdated).ToBytes();
                p.sendData(Constants.CH_RootChannel, data);
            }
        } catch (IOException ex) {
            Logger.getLogger(BungeeSender.class.getName()).log(Level.SEVERE, null, ex);
        }

        AsyncEmptyCallback pop = (AsyncEmptyCallback) this.map.pop(key);
        if (pop != null) {
            pop.doCallback();
        }
    }
    //</editor-fold>
}
