/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.utility;

import com.lumengaming.skillsaw.models.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author prota
 */
public class SpigotHelper extends AbstractHelper{
    
    /* Returns null if not found. */
    public static Player getPlayer(User u){
        if (u != null && u.getUniqueId() != null){
            Player p = Bukkit.getPlayer(u.getUniqueId());
            return p;
        }
        return null;
    }
}
