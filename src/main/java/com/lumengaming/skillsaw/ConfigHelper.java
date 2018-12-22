/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw;

import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.ProxyServer;

/**
 *
 * @author prota
 */
public class ConfigHelper {
    public static File DATA_FOLDER = 
        ProxyServer.getInstance().getPluginManager().getPlugin("Skillsaw3").getDataFolder()
       // Bukkit.getServer().getPluginManager().getPlugin("Skillsaw3").getDataFolder()
            ;
    
    public static String LoadJson(String fileName){
        File file = new File(DATA_FOLDER, "plugin-info.json");
        Path path = file.toPath();
        Charset cs = Charset.defaultCharset();
        if (Charset.isSupported("UTF-16")){
            cs = Charset.forName("UTF-16");
        }
        
        String json = null;
        try {
            if (!file.exists()){
                file.createNewFile();
            }
            
            byte[] data = Files.readAllBytes(path);
            json = new String(data, cs);
        } catch (IOException ex) {
            Logger.getLogger(ConfigHelper.class.getName()).log(Level.SEVERE, "Failed to load config '"+fileName+"'", ex);
        }
        return json;
    }
    
    public static boolean SaveJson(Object o, String fileName){
        try {
            File file = new File(DATA_FOLDER, "plugin-info.json");
            Path path = file.toPath();
            
            Charset cs = Charset.defaultCharset();
            if (Charset.isSupported("UTF-16")){
                cs = Charset.forName("UTF-16");
            }
            
            if (!file.exists()){
                file.createNewFile();
            }
            
            String json = new GsonBuilder().setPrettyPrinting().create().toJson(o, o.getClass());
            Files.write(path, json.getBytes(cs), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(ConfigHelper.class.getName()).log(Level.SEVERE, "Failed to save config '"+fileName+"'", ex);
            return false;
        }
    }
}
