/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw;

import com.lumengaming.skillsaw.bridge.BungeeSender;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.bukkit.Bukkit;

/**
 *
 * @author prota
 */
public class BungeeMain extends Plugin implements ISkillsaw{
    
    private final BungeeSender sender = new BungeeSender(this);
    
    @Override
    public void onEnable(){
        if (!this.getDataFolder().exists()){
            this.getDataFolder().mkdir();
        }
        
		this.getProxy().getPluginManager().registerListener(this, sender);
		this.getProxy().getPluginManager().registerCommand(this, new NaturalRepCommand(this));
    }
    
    @Override
    public void onDisable(){
		this.getProxy().getPluginManager().unregisterCommands(this);
        this.getProxy().getPluginManager().unregisterListeners(this);
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
}
