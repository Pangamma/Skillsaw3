package com.lumengaming.skillsaw.listeners;

import com.lumengaming.skillsaw.SpigotMain;
import com.lumengaming.skillsaw.config.SpigotOptions;
import com.lumengaming.skillsaw.models.PvpModeSaveState;
import com.lumengaming.skillsaw.utility.C;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class SpigotPlayerListener implements Listener{
    private final SpigotMain plugin;

    public SpigotPlayerListener(SpigotMain aThis) {
        this.plugin = aThis;
    }
    
    @EventHandler
    public void onPlayerHurt(org.bukkit.event.entity.EntityDamageByEntityEvent e){
        if (e.getDamager() == null) return;
        if (e.getCause() != DamageCause.ENTITY_EXPLOSION) return;
        if (e.getDamager().getCustomName() == null) return;
        if (e.getDamager().getCustomName().equals(C.CH_CompositeEffect)){
            e.setCancelled(true); // cancel hurt from firework effects
        }
    }
    
    @EventHandler
    public void onFrostWalker(EntityBlockFormEvent e){
        if (e.getEntity() instanceof Player){
            Player p = (Player)e.getEntity();
            if (p.getGameMode() == GameMode.CREATIVE){
                e.setCancelled(true);
            }
        }
    }
    
        @EventHandler
    public void onSneakHideShowItemNBT(PlayerToggleSneakEvent e){
        if (e.isSneaking()) return;
        
        Player p = e.getPlayer();
        PlayerInventory inv = p.getInventory();
        ItemStack is = inv.getItemInMainHand();
        if (is == null || is.getType() == Material.AIR) return;
        if (p.getGameMode() != GameMode.CREATIVE) return;
        if (!inv.contains(Material.EMERALD_ORE)) return;
        
        if (!is.hasItemMeta()) return;
        ItemMeta meta = is.getItemMeta();
        boolean areAttrsHidden = meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES);
        if (meta instanceof Damageable){
            Damageable dmg = (Damageable) meta;
            if (dmg.getDamage() != 0){
                p.sendMessage(dmg.getDamage()+" = damage");
            }
        }
        if (areAttrsHidden){
            meta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            p.sendMessage("§aShowing item attributes for item in hand.");
        }else{
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            p.sendMessage("§aHiding item attributes for item in hand.");
        }
        
        is.setItemMeta(meta);
        inv.setItemInMainHand(is);
        
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="PVP MODE">
//    
//    
//    @EventHandler(ignoreCancelled = true)
//    public void onFrameBrake(HangingBreakByEntityEvent e){
//        Bukkit.broadcastMessage(e.getRemover().getName());
//        Bukkit.broadcastMessage(e.getRemover().getType().name());
//    }
//    
//    @EventHandler(ignoreCancelled = true)
//    public void onGunsHittingItemFrames(EntityDamageByEntityEvent e){
//        Bukkit.broadcastMessage("§bHello?2"+e.getCause()+"."+e.getDamager().getType());
//        Bukkit.broadcastMessage("§bHello?3"+e.getDamager().getType()+"."+e.getDamager().getCustomName());
//        if (e.getEntity() instanceof Player){
//            Bukkit.broadcastMessage("Hello?");
//        }
//    }
//    @EventHandler(ignoreCancelled = true)
//    public void onFrameBrake(HangingBreakEvent e) {
//        if (e.getCause() == HangingBreakEvent.RemoveCause.ENTITY 
//            || e.getCause() == HangingBreakEvent.RemoveCause.EXPLOSION) {
//            e.setCancelled(true);
//            Bukkit.broadcastMessage("BOOM="+e.getCause().name());
//        }
//    }
    
    @EventHandler
    public void onPlayerHurtPlayer(org.bukkit.event.entity.EntityDamageByEntityEvent e){
        if (e.getDamager() == null) return;
        if (!(e.getDamager() instanceof Player)) return;
        if (e.getEntity()== null) return;
        if (!(e.getEntity() instanceof Player)) return;
        if (SpigotOptions.Get().PvpMode.IsEnabled == false) return;
        
        if (!plugin.isPvpModeEnabled(e.getDamager().getUniqueId())){ 
            e.getDamager().sendMessage("§cYou must enable /pvpmode before you can PVP.");
            e.setCancelled(true);
            return;
        }
        
        if (!plugin.isPvpModeEnabled(e.getEntity().getUniqueId())){
            e.getDamager().sendMessage("§cThe other player must be in PVP mode before you can PVP! /pvpm");
            e.setCancelled(true); // cancel hurt from firework effects
            return;
        }
    }
        
    @EventHandler
    public void onPlayerShootPlayer(org.bukkit.event.entity.ProjectileHitEvent e){
        if (e.getEntity() == null) return;
        if (e.getEntity().getShooter() == null) return;
        if (!(e.getEntity().getShooter()  instanceof Player)) return;
        
        if (e.getHitEntity()== null) return;
        if (!(e.getHitEntity() instanceof Player)) return;
        if (SpigotOptions.Get().PvpMode.IsEnabled == false) return;
//        
//        if (!plugin.isPvpModeEnabled(((Player)e.getEntity().getShooter()).getUniqueId())){ 
//            ((Player)e.getEntity().getShooter()).sendMessage("§cYou must enable /pvpmode before you can PVP.");
//            e.setC
//            return;
//        }
//        
//        if (!plugin.isPvpModeEnabled(e.getHitEntity().getUniqueId())){
//            ((Player)e.getEntity().getShooter()).sendMessage("§cThe other player must be in PVP mode before you can PVP! /pvpm");
//            e.setCancelled(true); // cancel hurt from firework effects
//            return;
//        }
    }
    
    @EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent e){
        plugin.removePvpPlayer(e.getPlayer());
    }
    
    @EventHandler
    public void onGamemodeChange(org.bukkit.event.player.PlayerGameModeChangeEvent e){
        if (e.isCancelled()) return;
        PvpModeSaveState state = plugin.getPvpModeSaveStates().get(e.getPlayer().getUniqueId());
        if (state != null){
            if (e.getNewGameMode() != GameMode.SURVIVAL){
                plugin.removePvpPlayer(e.getPlayer());
            }
        }
    }
    
    //</editor-fold>
    
}
