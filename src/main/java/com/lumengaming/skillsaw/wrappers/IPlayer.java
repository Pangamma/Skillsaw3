/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.wrappers;

import java.util.UUID;
import net.md_5.bungee.api.chat.BaseComponent;

/**
 *
 * @author prota
 */
public interface IPlayer {

    public UUID getUniqueId();

    public String getName();

    public String getDisplayName();

    public boolean hasPlayedBefore();

    public long getFirstPlayed();

    public String getIpv4();

    public void sendMessage(BaseComponent... message);

    public void sendMessage(String legacyText);

    public void setDisplayName(String displayName);
    
    public boolean isPlayer();

    public boolean isValid();

    public boolean hasPermission(String string);

    public boolean isOp();
}
