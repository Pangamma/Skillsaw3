/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw;

import com.lumengaming.skillsaw.Options.MysqlOptions;
import com.lumengaming.skillsaw.bridge.BungeeSender;
import com.lumengaming.skillsaw.commands.*;
import com.lumengaming.skillsaw.listeners.BungeeChatListener;
import com.lumengaming.skillsaw.listeners.BungeePlayerActivityListener;
import com.lumengaming.skillsaw.service.DataService;
import com.lumengaming.skillsaw.service.MySqlDataRepository;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

/**
 *
 * @author prota
 */
public class BungeeMain extends Plugin implements ISkillsaw{
    
    private final BungeeSender sender = new BungeeSender(this);
    private DataService dataService;
    private BungeePlayerActivityListener activityListener;

    @Override
    public void onEnable(){
        if (!this.getDataFolder().exists()){
            this.getDataFolder().mkdir();
        }
        Options.Load();
        Options.Save();
        
        if (Options.Get().Mysql.IsEnabled){
            MysqlOptions opt = Options.Get().Mysql;
            this.dataService = new DataService(this, new MySqlDataRepository(this, opt.Host, opt.Port, opt.User, opt.Pass, opt.Database, false));
        }
        
        if (this.dataService.onEnable()){
            for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                this.dataService.loginUser(new BungeePlayer(p), (u) -> {});
            }
        }else{
            java.util.logging.Logger.getLogger(BungeeSender.class.getName()).log(Level.SEVERE, "Failed to load plugin.");
            return;
        }
        
        
        
        
		this.getProxy().getPluginManager().registerListener(this, sender);
        this.activityListener = new BungeePlayerActivityListener(this);
        this.activityListener.onEnable();
		this.getProxy().getPluginManager().registerListener(this, this.activityListener);
		this.getProxy().getPluginManager().registerListener(this, new BungeeChatListener(this));
        if (Options.Get().RepSystem.IsEnabled){
            this.getProxy().getPluginManager().registerCommand(this, new NoteCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new NaturalRepCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new StaffRepCommand(this));
            this.getProxy().getPluginManager().registerCommand(this, new XRepCommand(this));
        }
    }
    
    @Override
    public void onDisable(){
        this.activityListener.onDisable();
		this.getProxy().getPluginManager().unregisterCommands(this);
        this.getProxy().getPluginManager().unregisterListeners(this);
        
        this.dataService.onDisable();
        this.dataService = null;
        this.getProxy().getScheduler().cancel(this);
    }
    
    public BungeeSender getSender(){
        return this.sender;
    }

    @Override
    public void runTaskAsynchronously(Runnable runnable) {
        ScheduledTask task = ProxyServer.getInstance().getScheduler().runAsync(this, runnable);
    }
    
    @Override
    public void runTask(Runnable runnable) {
        ScheduledTask task = ProxyServer.getInstance().getScheduler().schedule(this, runnable, 0, TimeUnit.MILLISECONDS);
    }

    public DataService getDataService() {
        return this.dataService;
    }

    @Override
    public void playVillagerSound(IPlayer p) {
        BungeePlayer bp = (BungeePlayer) p;
        this.getSender().doHmmmEffect(bp.p(),(b) -> { /* I dun currrr */ });
    }

    @Override
    public void playLevelUpEffect(IPlayer p, String title, String subtitle) {
        BungeePlayer bp = (BungeePlayer) p;
        this.getSender().doLevelUpEffect(bp.p(), title, subtitle, (b) -> {});
    }

    @Override
    public void playLevelDownEffect(IPlayer p, String subtitle) {
        if (p == null) return;
        BungeePlayer bp = (BungeePlayer) p;
        this.getSender().doLevelDownEffect(
            bp.p(),
            subtitle, 
            subtitle, 
            (b) -> {});
    }

    public DataService getService() {
        return this.getDataService();
    }

    @Override
    public void broadcast(String legacyText) {
        this.getProxy().broadcast(CText.legacy(legacyText));
    }

    @Override
    public IPlayer getPlayer(UUID uuid) {
        if (uuid == null) return null;
        ProxiedPlayer player = this.getProxy().getPlayer(uuid);
        if (player == null) return null;
        return new BungeePlayer(player);
    }
}
