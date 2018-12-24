package com.lumengaming.skillsaw.listeners;

import com.lumengaming.skillsaw.SpigotMain;
import com.lumengaming.skillsaw.utility.Constants;
import org.bukkit.Bukkit;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class SpigotPlayerListener implements Listener{
    private final SpigotMain plugin;

    public SpigotPlayerListener(SpigotMain aThis) {
        this.plugin = aThis;
    }
    
    @EventHandler
    public void onPlayerHurt(org.bukkit.event.entity.EntityDamageByEntityEvent e){
        if (e.getDamager() == null) return;
        if (e.getCause() != DamageCause.ENTITY_EXPLOSION) return;
        if (e.getDamager().getCustomName().equals(Constants.CH_CompositeEffect)){
            e.setCancelled(true); // cancel hurt from firework effects
        }
    }
}
