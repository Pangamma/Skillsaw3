/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

/**
 * Idk. Sometimes I just disagree with Java's naming conventions. Properties are great, alright? C# is bae.
 * @author prota
 */
public class SpigotOptions
{
    //<editor-fold defaultstate="collapsed" desc="Properties">
    @SerializedName("is-enhanced-plugin-list-enabled")
    public boolean IsEnhancedPluginListEnabled = true;
    //</editor-fold>

    
    //<editor-fold defaultstate="collapsed" desc="SubClasses">
  
    //</editor-fold>
    
    
    
    //<editor-fold defaultstate="collapsed" desc="CORE methods">
    public static File DATA_FOLDER = 
//        ProxyServer.getInstance().getPluginManager().getPlugin("Skillsaw3").getDataFolder()
        Bukkit.getServer().getPluginManager().getPlugin("Skillsaw3").getDataFolder()
            ;
    private static final String fileName = "config.json";
    /** 
     * Java sucks compared to C#. I miss my properties!
     * 
     * @return Options
     */
    public static SpigotOptions Get(){
        if (_get == null){
            _get = SpigotOptions.Load();
        }
        return _get;
    }
    private static SpigotOptions _get = null;
    
    public static SpigotOptions Save(){
        SpigotOptions o = Get();
        ConfigHelper.SaveJson(o, fileName);
        return _get;
    }
    
    public static SpigotOptions Load(){
        String json = ConfigHelper.LoadJson(fileName);
        try{
            Gson gson = new Gson();
            if (json != null){
                SpigotOptions fromJson = gson.fromJson(json, SpigotOptions.class);
                _get = fromJson;
            }
        }catch(Exception ex){
            Logger.getLogger(ConfigHelper.class.getName()).log(Level.SEVERE, "Failed to load config '"+fileName+"'", ex);
        }
        if (_get == null){
            _get = new SpigotOptions();
        }
        return _get; 
    }


    public SpigotOptions() {
    }
    //</editor-fold>

    public static class ChatSystemOptions {

        @SerializedName("enabled")
        public boolean IsEnabled = true;
        
        @SerializedName("ignore-list-limit")
        public int MaxIgnoreListSize = 10;
        
        @SerializedName("allow-me-on-main-channel")
        public boolean IsMeAllowedOnMainChannel = true;
        
        public ChatSystemOptions() {
        }
    }
    
}
