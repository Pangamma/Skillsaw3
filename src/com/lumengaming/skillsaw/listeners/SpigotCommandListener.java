/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.listeners;

import com.lumengaming.skillsaw.bungee.utility.CText;
import com.lumengaming.skillsaw.SpigotMain;
import com.lumengaming.skillsaw.spigot.Options;
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
        if (Options.IsPluginsCommandOverrideEnabled){
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
                Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
                BaseComponent[] legacy = CText.legacy("§7Plugins (§a"+plugins.length+"§7): ");
                
                int maxLengthPerMessage;
                for(int i = 0; i < plugins.length; i++){
                    Plugin plug = plugins[i];
                    String name = plug.getName();
                    PluginDescriptionFile desc = plug.getDescription();
                    String website = desc.getWebsite();
                    String version = desc.getVersion();
                    boolean isEnabled = plug.isEnabled();
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


                        if (desc.getAuthors().size() > 0){
                            cb  .append("\nAuthor(s): ").color(ChatColor.GRAY)
                                .append(String.join(",", desc.getAuthors())).color(ChatColor.GREEN);
                        }

                        if (website != null && website.length() > 5){
                            cb  .append("\nWebsite: ").color(ChatColor.GRAY)
                                .append("Click to Copy").color(ChatColor.GREEN);
                        }
                        
                        hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, cb.create());
                    }
                    
                    {
                        BaseComponent[] cbPluginName = new ComponentBuilder(name)
                            .color(isEnabled ? ChatColor.GREEN : ChatColor.RED).create();
                        
                        for(BaseComponent bc : cbPluginName){
                            bc.setHoverEvent(hover);
                            if (website != null && website.length() > 5){ 
                                bc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, website));
                            }
                        }
                        
                        if (i < plugins.length -1){
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
}
