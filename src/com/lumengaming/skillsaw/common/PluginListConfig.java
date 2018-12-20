/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.common;

import com.google.gson.Gson;
import com.lumengaming.skillsaw.SpigotMain;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author prota
 */
public class PluginListConfig extends AbstractJsonConfig<PluginListConfig>{
    public HashMap<String, PluginDescriptionNode> Plugins = new HashMap<String, PluginDescriptionNode>();
    private final SpigotMain plugin;
    
    public PluginListConfig(SpigotMain plugin){
        super(PluginListConfig.class);
        this.plugin = plugin;
    }
    
    public static PluginListConfig fromJson(String json){
        Gson gson = new Gson();
        PluginListConfig obj = gson.fromJson(json, PluginListConfig.class);
        return obj;
    }
    
    public String toJson(){
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }

    @Override
    public String getFilePath() {
        return plugin.getDataFolder()+File.pathSeparator+"plugin-info.json";
    }

    @Override
    protected boolean onLoad(PluginListConfig fromJson) {
        this.Plugins = fromJson.Plugins;
        Plugin[] plugins = plugin.getServer().getPluginManager().getPlugins();
        for(Plugin plug : plugins){
            String key = plug.getName();
            PluginDescriptionNode node = new PluginDescriptionNode();
            PluginDescriptionNode get = this.Plugins.get(key);
            node.Name = get != null ? get.Name : key;
            node.Text = get != null ? get.Text : "";
            node.URL =  get != null ? get.URL : "";
        }
        return true;
    }
    
    private class PluginDescriptionNode{
        public String Name;
        public String URL;
        public String Text;
    }
}
