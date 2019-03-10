/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.utility;

import com.lumengaming.skillsaw.models.ColorType;
import com.lumengaming.skillsaw.models.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

/**
 *
 * @author prota
 */
public class SH extends AbstractHelper{
    
    /* Returns null if not found. */
    public static Player getPlayer(User u){
        if (u != null && u.getUniqueId() != null){
            Player p = Bukkit.getPlayer(u.getUniqueId());
            return p;
        }
        return null;
    }

    public static void broadcast(String dMessage_gotten) {
//        if (Bukkit.getPlayer("Pangamma") != null){
//            Bukkit.getPlayer("Pangamma").sendMessage(dMessage_gotten);
//        }
    }
    
    public static void removeGlowColor(LivingEntity p) {
        String key = p instanceof Player ? p.getName() : p.getUniqueId().toString();
        Team t = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(key);
        if (t != null) {
            t.removeEntry(key);
        }
        p.setGlowing(false);
    }
    
    public static void setGlowColor(int dv, LivingEntity p) {
        ChatColor c = ColorType.colorFromDataValue(dv);
        setGlowColor(c, p);
    }
    
    public static void setGlowColor(ChatColor c, LivingEntity p) {
        String key = p instanceof Player ? p.getName() : p.getUniqueId().toString();
        Team t = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(key);
        if (t != null) {
            t.removeEntry(key);
        }
        t = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(SH.getTeamName(c));
        if (t != null) {
            t.addEntry(key);
        }
        p.setGlowing(true);
    }
    
    public static String getTeamName(ChatColor c) {
        if (c.isColor()) {
            String s = ("SKSAW_" + c.name()).replace("LIGHT", "LT").replace("DARK", "DRK").replace("PURPLE", "PPL");
            if (s.length() > 16) {
                s = s.substring(0, 15);
            }
            return s;
        }
        return "SKSAW_INVALID";
    }

}
