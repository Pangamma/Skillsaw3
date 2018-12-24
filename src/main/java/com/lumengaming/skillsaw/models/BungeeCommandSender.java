/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.models;

import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.common.ICommandSender;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author prota
 */
public class BungeeCommandSender implements ICommandSender{
    
    private final CommandSender cs;
    public BungeeCommandSender(CommandSender cs){
        this.cs = cs;
    }
    
    @Override
    public boolean hasPermission(String node) {
        return cs.hasPermission(node);
    }

    @Override
    public void sendMessage(String string) {
        cs.sendMessage(CText.legacy(string));
    }

    @Override
    public boolean isOp() {
        return false;
    }

    @Override
    public void sendMessage(BaseComponent[] msg) {
         if (this.isPlayer()){
            cs.sendMessage(msg);
        }else{
            String s = "";
            for(BaseComponent bc : msg){
                s  += bc.toLegacyText();
            }
            s = ChatColor.stripColor(s);
            BaseComponent[] legacy = CText.legacy(s);
            cs.sendMessage(legacy);
        }
//        cs.sendMessage(msg);
    }

    @Override
    public boolean isPlayer() {
        return (cs instanceof ProxiedPlayer);
    }
    
    public ProxiedPlayer getPlayer() {
        return isPlayer() ? (ProxiedPlayer) cs : null;
    }
}
