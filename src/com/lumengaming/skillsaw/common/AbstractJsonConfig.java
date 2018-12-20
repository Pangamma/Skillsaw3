/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.common;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.ProxyServer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author prota
 */
public abstract class AbstractJsonConfig<T> {
    public abstract String getFilePath();
    protected Class<T> clz;
    public AbstractJsonConfig(Class<T> t){
        this.clz = t;
    }
    
    public T Save(){
        try {
            //<editor-fold defaultstate="collapsed" desc="set up config">
            String filePath = getFilePath();
            Path path = Paths.get(filePath);
            
            Charset cs = Charset.defaultCharset();
            if (Charset.isSupported("UTF-16")){
                cs = Charset.forName("UTF-16");
            }
            
            String json = new Gson().toJson(this, this.getClass());
            Files.write(path, json.getBytes(cs), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            return (T) this;
        } catch (IOException ex) {
            Logger.getLogger(AbstractJsonConfig.class.getName()).log(Level.SEVERE, null, ex);
            return (T) this;
        }
    }
    
    public T Load(){
        String filePath = getFilePath();
        Path path = Paths.get(filePath);
        Charset cs = Charset.defaultCharset();
        if (Charset.isSupported("UTF-16")){
            cs = Charset.forName("UTF-16");
        }
        
        try {
            byte[] data = Files.readAllBytes(path);
            String json = new String(data, cs);
            Gson gson = new Gson();
            T fromJson = gson.fromJson(json, this.clz);
            this.onLoad(fromJson);
        } catch (IOException ex) {
            Logger.getLogger(AbstractJsonConfig.class.getName()).log(Level.SEVERE, null, ex);
            this.onLoad(null);
        }
        return (T) this;
    }

    protected abstract boolean onLoad(T fromJson);
}
