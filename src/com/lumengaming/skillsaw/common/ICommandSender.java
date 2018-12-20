/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.common;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

/**
 *
 * @author prota
 */
public interface ICommandSender {
    public boolean hasPermission(String node);
    public void sendMessage(String string);
    public void sendMessage(BaseComponent[] msg);
    public boolean isOp();
    public boolean isPlayer();
}
