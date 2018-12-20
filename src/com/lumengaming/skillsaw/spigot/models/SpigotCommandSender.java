/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.spigot.models;

import com.lumengaming.skillsaw.common.ICommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author prota
 */
public class SpigotCommandSender implements ICommandSender{
    
    private final CommandSender cs;
    public SpigotCommandSender(CommandSender cs){
        this.cs = cs;
    }

    @Override
    public boolean hasPermission(String node) {
        return cs.hasPermission(node);
    }

    @Override
    public void sendMessage(String string) {
        cs.sendMessage(string);
    }

    @Override
    public boolean isOp() {
        return cs.isOp();
    }

    @Override
    public void sendMessage(BaseComponent[] msg) {
        cs.spigot().sendMessage(msg);
    }

    @Override
    public boolean isPlayer() {
        return cs instanceof Player;
    }
    
    public Player getPlayer() {
        return isPlayer() ? (Player) cs : null;
    }
}
