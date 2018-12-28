/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.wrappers;

import com.lumengaming.skillsaw.utility.BungeeHelper;
import com.lumengaming.skillsaw.utility.CText;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author prota
 */
public class BungeePlayer implements IPlayer {
    private final CommandSender cs;
    public BungeePlayer(CommandSender cs){
        this.cs = cs;
    }
    
    public ProxiedPlayer p(){
        if (cs instanceof ProxiedPlayer){
            return (ProxiedPlayer)cs;
        }
        return null;
    }
    
    public CommandSender cs(){
        return cs;
    }

    @Override
    public void setDisplayName(String name) {
       if (this.isPlayer()){
           this.p().setDisplayName(name);
       }
    }

    @Override
    public UUID getUniqueId() {
        if (this.isPlayer()){
            return this.p().getUniqueId();
        }else{
            throw new UnsupportedOperationException("Console users do not have UUIDs.");
        }
    }

    @Override
    public String getName() {
        return cs.getName();
    }

    @Override
    public String getDisplayName() {
        if (!this.isPlayer()){
            return cs.getName();
        }else{
            return p().getDisplayName();
        }
    }

    @Override
    public boolean hasPlayedBefore() {
        // you need to get this info from SQL. It is the only way.
        return false;
    }

    /**
     * Unable to get ACTUAL first time played from proxy. USE AT YOUR OWN RISK.
     * Returns current time in millis.
     * @return 
     */
    @Override
    public long getFirstPlayed() {
        return System.currentTimeMillis();
    }

    @Override
    public String getIpv4() {
        if (isPlayer()){
            return p().getAddress().getAddress().getHostAddress();
        }else{
            return "127.0.0.1";
        }
    }

    @Override
    public void sendMessage(BaseComponent... message) {
        if (p() == null) return;
        if (this.isPlayer()){
            p().sendMessage(message);
        }else{
            p().sendMessage(CText.stripColors(message));
        }
    }

    @Override
    public void sendMessage(String legacyText) {
        if (p() == null) return;
        if (!isPlayer()) legacyText = ChatColor.stripColor(legacyText);
        p().sendMessage(CText.legacy(legacyText));
    }

    @Override
    public boolean isPlayer() {
        return cs instanceof ProxiedPlayer;
    }

    @Override
    public boolean isValid() {
        return p().isConnected();
    }

    @Override
    public boolean hasPermission(String string) {
        if (isPlayer() && p() != null && this.isValid())
            return p().hasPermission(string);
        else
            return !isPlayer(); // console can do anything
    }

    @Override
    public boolean isOp() {
        return isPlayer() == false; // Console == OP.
    }

    @Override
    public Object getRaw() {
        return this.cs;
    }
}
