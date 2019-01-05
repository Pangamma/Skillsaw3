/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.listeners;

import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.SpigotMain;
import com.lumengaming.skillsaw.SpigotOptions;
import java.util.ArrayList;
import java.util.HashMap;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 *
 * @author prota
 */
public class SpigotCommandListener implements Listener{
    private final SpigotMain plugin;

    public SpigotCommandListener(SpigotMain plug) {
        this.plugin = plug;
    }
	
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlugins(final PlayerCommandPreprocessEvent e) {
        if (SpigotOptions.Get().IsEnhancedPluginListEnabled){
            String scrubbed = (e.getMessage().startsWith("/") ? e.getMessage().substring(1) : e.getMessage()).toLowerCase();
            String[] aliases = new String[]{ "pl","plugins" };
            boolean isMatch = false;
            for(String alias : aliases){
                if (alias.equals(scrubbed)){
                    isMatch = true;
                    break;
                }
            }
            
            if (isMatch){
                Player p = e.getPlayer();
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT,1F,1F); // "Hmmmm"
                e.setCancelled(true);
                
                ArrayList<HashMap<String, String>> plugins = this.getPluginList();
                BaseComponent[] legacy = CText.legacy("§7Plugins (§a"+(plugins.size()+1)+"§7): ");
                
                for(int i = 0; i < plugins.size(); i++){
                    HashMap<String,String> plug = plugins.get(i);
                    String name = plug.get("Name");
                    String desc = plug.get("Description");
                    String website = plug.get("Website");
                    String version = plug.get("Version");
                    String authors = plug.get("Authors");
                    boolean isEnabled = (true+"").equals(plug.get("IsEnabled"));
                    
                    HoverEvent hover = null;
                    {
                        ComponentBuilder cb = new ComponentBuilder(name +" (");
                        cb  
                            .reset()
                            .append(version).color(ChatColor.RED)
                            .append(")").reset()

                            .append("\nStatus: ").color(ChatColor.GRAY)
                            .append(isEnabled ? "Enabled" : "Disabled").color(isEnabled ? ChatColor.GREEN : ChatColor.RED)
                            ;


                        if (authors != null && authors.length() > 0){
                            cb  .append("\nAuthor(s): ").color(ChatColor.GRAY)
                                .append(String.join(",", authors)).color(ChatColor.GREEN);
                        }

                        if (website != null && website.length() > 5){
                            cb  .append("\nWebsite: ").color(ChatColor.GRAY)
                                .append("Click to Copy").color(ChatColor.GREEN);
                        }
                        
                        if (desc != null && desc.length() > 5){
                            cb  .append("\nDesc: ").color(ChatColor.GRAY)
                                .append(desc).color(ChatColor.GREEN);
                        }
                        
                        hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, cb.create());
                    }
                    
                    {
                        BaseComponent[] cbPluginName = new ComponentBuilder(name)
                            .color(isEnabled ? ChatColor.GREEN : ChatColor.RED).create();
                        
                        for(BaseComponent bc : cbPluginName){
                            bc.setHoverEvent(hover);
                            if (website != null && website.length() > 5){ 
                                bc.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, website));
                            }
                        }
                        
                        if (i < plugins.size() -1){
                            cbPluginName = CText.merge(cbPluginName, new ComponentBuilder(", ").color(ChatColor.GRAY).create());
                        }
                    
                        BaseComponent[] tmpLegacy = CText.merge(legacy, cbPluginName);
                        if (new TextComponent(tmpLegacy).toPlainText().length() > 60){
                            p.spigot().sendMessage(legacy);
                            legacy = cbPluginName;
                        }else{
                            legacy = tmpLegacy;
                        }
                    }
                }
                
                p.spigot().sendMessage(legacy);
                p.spigot().sendMessage(CText.legacy("§7§oHover over the plugins for more info"));
            }
        }
    }
    
    private ArrayList<HashMap<String,String>> getPluginList(){
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        ArrayList<HashMap<String,String>> items = new ArrayList<HashMap<String,String>>();
        HashMap<String,String> itemPixelStacker = new HashMap<String,String>();
        itemPixelStacker.put("Name", "PixelStacker");
        itemPixelStacker.put("Website", "https://taylorlove.info/pixelstacker");
        itemPixelStacker.put("Version", "X");
        itemPixelStacker.put("IsEnabled", true+"");
        itemPixelStacker.put("Authors", "Pangamma");
        itemPixelStacker.put("Description", "A super amazing pixelart generator. Better than all other programs. Can generate multi layer pixel art.");
        items.add(itemPixelStacker);
            
        for(int i = 0; i < plugins.length; i++){
            HashMap<String,String> item = new HashMap<String,String>();
            Plugin plug = plugins[i];
            item.put("Name", plug.getName());
            PluginDescriptionFile desc = plug.getDescription();
            if (desc.getWebsite() != null && desc.getWebsite().length() > 1){
                item.put("Website", desc.getWebsite());
            }
            item.put("Version", desc.getVersion());
            item.put("IsEnabled", plug.isEnabled()+"");
            item.put("Authors", String.join(", ", desc.getAuthors()));
            item.put("Description", desc.getDescription());
            items.add(item);
        }
        
        return items;
    }
}
