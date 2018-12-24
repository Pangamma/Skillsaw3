/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw;

import com.lumengaming.skillsaw.utility.Constants;
import com.lumengaming.skillsaw.listeners.SpigotCommandListener;
import com.lumengaming.skillsaw.listeners.SpigotJoinListener;
import com.lumengaming.skillsaw.bridge.SpigotMessageListener;
import com.lumengaming.skillsaw.listeners.SpigotPlayerListener;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author prota
 */
public class SpigotMain extends JavaPlugin implements ISkillsaw{
    
//    private DataService dataService;
    
    @Override
    public void onEnable(){
        if (!Bukkit.getServer().getPluginManager().getPlugin(this.getName()).getDataFolder().exists()){
            Bukkit.getServer().getPluginManager().getPlugin(this.getName()).getDataFolder().mkdir();
        }
        Options.Load();
        Options.Save();
        
        getServer().getPluginManager().registerEvents(new SpigotCommandListener(this), this);
        getServer().getPluginManager().registerEvents(new SpigotJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new SpigotPlayerListener(this), this);
        
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, Constants.CH_RootChannel);
        this.getServer().getMessenger().registerIncomingPluginChannel(this, Constants.CH_RootChannel, new SpigotMessageListener(this));
        
    }
    
    @Override
    public void onDisable(){
        HandlerList.unregisterAll(this);
//        this.dataService.onDisable();
        Bukkit.getScheduler().cancelTasks(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

//    @Override
//    public DataService getService() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

    @Override
    public void runTaskAsynchronously(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this, runnable);
    }

    @Override
    public void runTask(Runnable runnable) {
        Bukkit.getScheduler().runTask(this, runnable);
    }

    @Override
    public void playVillagerSound(IPlayer p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playLevelUpEffect(IPlayer p, String reputation_Level_Increased, String aCongratulations_Your_total2_Reputation_L) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playLevelDownEffect(IPlayer p, String cYour_total_4Reputation_Levelc_has_decrea) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
