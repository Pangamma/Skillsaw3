/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.wrappers;

import java.util.UUID;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author prota
 */
public class SpigotPlayer implements IPlayer {
    private final CommandSender cs;

    public SpigotPlayer(CommandSender p) {
        this.cs = p;
    }

    @Override
    public UUID getUniqueId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        return this.cs.getName();
    }

    @Override
    public String getDisplayName() {
        return this.isPlayer() ? ((Player) cs).getDisplayName() : this.cs.getName();
    }

    @Override
    public boolean hasPlayedBefore() {
        return this.isPlayer() ? ((Player) cs).hasPlayedBefore() : true;
    }

    @Override
    public long getFirstPlayed() {
        return this.isPlayer() ? ((Player) cs).getFirstPlayed() : System.currentTimeMillis();
    }

    @Override
    public String getIpv4() {
        return this.isPlayer() ? ((Player) cs).getAddress().getAddress().getHostAddress() : "127.0.0.1";
    }

    @Override
    public void sendMessage(BaseComponent... message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendMessage(String legacyText) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDisplayName(String displayName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isPlayer() {
        return cs instanceof Player;
    }

    @Override
    public boolean isValid() {
        if (isPlayer()) return ((Player) cs).isValid();
        return true;
    }

    @Override
    public boolean hasPermission(String string) {
        if (isPlayer()) return ((Player) cs).hasPermission(string);
        return true;
    }

    @Override
    public boolean isOp() {
        if (isPlayer()) return ((Player) cs).isOp();
        return true;
    }
    
}
