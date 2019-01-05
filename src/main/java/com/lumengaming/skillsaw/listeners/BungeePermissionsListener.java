/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.listeners;

import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;

/**
 *
 * @author prota
 */
public class BungeePermissionsListener implements Listener{
    
    public void onCheck(PermissionCheckEvent e){
        if (e.hasPermission()) return;
//        if (e.getSender().setPermission(null, true))
        
        // Operators:
        
        // variables:
        /*
        u:
            REDSTONE
            ORGANICS
            PIXELART
            ARCHITECTURE
            TERRAFORMING
            VEHICLES
            REP_LEVEL
            VOTES_PER_DAY
            VOTES_PER_WEEK
            VOTES_PER_MONTH
            VOTES_TOTAL
            ACTIVITY_PER_WEEK
            ACTIVITY_PER_MONTH
        s:
            VOTES_PER_DAY
            VOTES_PER_WEEK
            VOTES_PER_MONTH
            VOTES_TOTAL 
            ACTIVITY_PER_WEEK
            ACTIVITY_PER_MONTH
        
        
        */
    }
}
