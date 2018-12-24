/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.utility;

import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ChatColor;

/**
 *
 * @author prota
 */
public class SharedUtility {
    
    /**
     * [0] days [1] hours [2] minutes [3] seconds [4] millis
     *
     * @param ms
     */
    public static long[] getTimeParts(long ms) {
        long[] t = new long[5];
        t[0] = TimeUnit.MILLISECONDS.toDays(ms);
        t[1] = TimeUnit.MILLISECONDS.toHours(ms - TimeUnit.DAYS.toMillis(t[0]));
        t[2] = TimeUnit.MILLISECONDS.toMinutes(ms - TimeUnit.DAYS.toMillis(t[0]) - TimeUnit.HOURS.toMillis(t[1]));
        t[3] = TimeUnit.MILLISECONDS.toSeconds(ms - TimeUnit.DAYS.toMillis(t[0]) - TimeUnit.HOURS.toMillis(t[1]) - TimeUnit.MINUTES.toMillis(t[2]));
        t[4] = TimeUnit.MILLISECONDS.toSeconds(ms - TimeUnit.DAYS.toMillis(t[0]) - TimeUnit.HOURS.toMillis(t[1]) - TimeUnit.MINUTES.toMillis(t[2]) - TimeUnit.SECONDS.toMillis(t[3]));
        return t;
    }

    /**
     * '1d 5h 23m 22s'
     *
     * @param ms
     * @return
     */
    public static String getTimePartsString(long ms) {
        
        long[] timeParts = getTimeParts(ms);
        String s = "";
        s += timeParts[0] + "d ";
        s += timeParts[1] + "h ";
        s += timeParts[2] + "m ";
        s += timeParts[3] + "s";
        return s;
    }
    
    
    // & --> §
    public static String enableColorCodes(String input){
        String output = input;
        String colorCodes = "abcdef0123456789rlonmk";
        
        for(char c : colorCodes.toCharArray()){
            output = output.replace("&"+c, "§"+c);
        }
        return output;
    }
    
    // § --> &
    public static String disableColorCodes(String input){
        String output = input;
        String colorCodes = "abcdef0123456789rlonmk";
        
        for(char c : colorCodes.toCharArray()){
            output = output.replace("§"+c, "&"+c);
        }
        return output;
    }
}
