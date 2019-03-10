/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.bridge;

import com.google.common.io.ByteArrayDataOutput;
import com.google.gson.Gson;
import com.lumengaming.skillsaw.SpigotMain;
import com.lumengaming.skillsaw.common.AsyncCallback;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.C;
import com.lumengaming.skillsaw.models.XLocation;
import com.lumengaming.skillsaw.utility.ExpireMap;
import com.lumengaming.skillsaw.utility.SH;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.meta.FireworkMeta;

/**
 *
 * @author prota
 */
public class SpigotSender implements org.bukkit.plugin.messaging.PluginMessageListener, Listener {

    private final SpigotMain plugin;
    private final Gson gson;

    public SpigotSender(SpigotMain p) {
        this.plugin = p;
        this.gson = new Gson();
    }
    
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(C.CH_RootChannel)) {
            return;
        }
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            String subchannel = in.readUTF(); 
            in = new DataInputStream(new ByteArrayInputStream(message));
            switch (subchannel) {
                case C.CH_GetPlayerLocation:
                    onGetLocation(player, in);
                    break;
                case C.CH_SetPlayerLocation:
                    onSetLocation(player, in);
                    break;
                case C.CH_PlaySoundForPlayer:
                    onPlaySoundForPlayer(player, in);
                    break;
                case C.CH_CompositeEffect:
                    onCompositeEffectForPlayer(player, in);
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Failed to read message from the proxy.", ex);
        }
    }

    public void getUser(UUID uuid, AsyncCallback<User> callback){
        
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="GetLocation">
    private void onGetLocation(Player player, DataInputStream in) throws IOException {
        GetPlayerLocationRequest req = new GetPlayerLocationRequest().FromBytes(in);
        Player p = Bukkit.getPlayer(req.UUID);
        if (p == null) {
        SH.broadcast("§dPNull");
            return;
        }

        Location loc = p.getLocation();
        XLocation xLoc = new XLocation();
        xLoc.X = loc.getX();
        xLoc.Y = loc.getY();
        xLoc.Z = loc.getZ();
        xLoc.Yaw = loc.getYaw();
        xLoc.Pitch = loc.getPitch();
        xLoc.World = loc.getWorld().getName();
        xLoc.Server = req.ServerName;

        SH.broadcast("§d"+xLoc.toJson());
        byte[] data = new GetPlayerLocationResponse(req.Key, xLoc).ToBytes();
        player.sendPluginMessage(plugin, C.CH_RootChannel, data);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="SetLocation">

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        UUID key = e.getPlayer().getUniqueId();
        final XLocation val = pendingLocationMap.remove(key);
        if (val != null){
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                World w = Bukkit.getWorld(val.World);
                if (w == null){
                    e.getPlayer().sendMessage("§cNo world found by the name of '§4"+val.World+"§c.");
                    return;
                }
            
                Location loc = new Location(w, val.X, val.Y, val.Z, val.Yaw, val.Pitch);
                boolean isSuccess = e.getPlayer().teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
            }, 100);
        }
    }

    private final ExpireMap<UUID, XLocation> pendingLocationMap = new ExpireMap<>();
    private void onSetLocation(Player player, DataInputStream in) throws IOException {
        SetPlayerLocationRequest req = new SetPlayerLocationRequest().FromBytes(in);
        Player p = Bukkit.getPlayer(req.UUID);
        pendingLocationMap.put(req.UUID, req.Loc, Duration.ofMinutes(3));

        if (p == null) {
            byte[] data = new BooleanResponse(C.CH_SetPlayerLocation, req.Key, false).ToBytes();
            player.sendPluginMessage(plugin, C.CH_RootChannel, data);
            return;
        }
        
        if (req.Loc == null) {
            byte[] data = new BooleanResponse(C.CH_SetPlayerLocation, req.Key, false).ToBytes();
            player.sendPluginMessage(plugin, C.CH_RootChannel, data);
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Location passed to set location method was null.");
            return;
        }
        
        World w = Bukkit.getWorld(req.Loc.World);
        if (w == null) {
            byte[] data = new BooleanResponse(C.CH_SetPlayerLocation, req.Key, false).ToBytes();
            player.sendPluginMessage(plugin, C.CH_RootChannel, data);
            return;
        }

        Location loc = new Location(w, req.Loc.X, req.Loc.Y, req.Loc.Z, req.Loc.Yaw, req.Loc.Pitch);
        boolean teleport = p.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
        
        if (teleport){
            pendingLocationMap.remove(req.UUID);
        }

        byte[] data = new BooleanResponse(C.CH_SetPlayerLocation, req.Key, true).ToBytes();
        player.sendPluginMessage(plugin, C.CH_RootChannel, data);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="PlaySound">
    private void onPlaySoundForPlayer(Player player, DataInputStream in) throws IOException {
        long key = in.readLong();
        String uuid = in.readUTF();
        Player p = Bukkit.getPlayer(UUID.fromString(uuid));
        String soundName = in.readUTF();

        if (p == null) {
            byte[] data = new BooleanResponse(C.CH_PlaySoundForPlayer, key, false).ToBytes();
            player.sendPluginMessage(plugin, C.CH_RootChannel, data);
            return;
        }

        Sound sound = null;
        for (Sound s : Sound.values()) {
            if (s.name().equalsIgnoreCase(soundName)) {
                sound = s;
                break;
            }
        }

        // TODO: Supply music option or alternatives if needed.
        if (sound == null) {
            p.playSound(p.getLocation(), soundName, SoundCategory.BLOCKS, 1, 1);
        } else {
            p.playSound(p.getLocation(), sound, SoundCategory.BLOCKS, 1, 1);
        }

        byte[] data = new BooleanResponse(C.CH_PlaySoundForPlayer, key, true).ToBytes();
        player.sendPluginMessage(plugin, C.CH_RootChannel, data);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Effects">
    private void onPlayEffect(Player player, DataInputStream in, ByteArrayDataOutput out) throws IOException {
        Player p = Bukkit.getPlayer(UUID.fromString(in.readUTF()));
        if (p == null) {
            return;
        }
        Location loc = p.getLocation();
        out.writeUTF(C.CH_GetPlayerLocation);
        out.writeUTF(String.valueOf(p.getUniqueId()));
        out.writeUTF(loc.getWorld().getName());
        out.writeDouble(loc.getX());
        out.writeDouble(loc.getY());
        out.writeDouble(loc.getZ());
        out.writeFloat(loc.getYaw());
        out.writeFloat(loc.getPitch());
        player.sendPluginMessage(plugin, C.CH_RootChannel, out.toByteArray());
    }

    private void onCompositeEffectForPlayer(Player player, DataInputStream in) throws IOException {
        PlayCompositeEffectRequest req = new PlayCompositeEffectRequest().FromBytes(in);
        Player p = Bukkit.getPlayer(req.UUID);
        if (p == null) {
            return;
        }
        byte[] data;
        switch (req.Type) {
            case Hmmm:
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, SoundCategory.NEUTRAL, 1, 1);
                data = new BooleanResponse(C.CH_CompositeEffect, req.Key, true).ToBytes();
                player.sendPluginMessage(plugin, C.CH_RootChannel, data);
                break;
            case Scold:
            case LevelDown:
                p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5F, 1);
                data = new BooleanResponse(C.CH_CompositeEffect, req.Key, true).ToBytes();
                player.sendPluginMessage(plugin, C.CH_RootChannel, data);
                break;
            case LevelUp:
                p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 0.5F, 1F);
                FireworkEffect fwe = org.bukkit.FireworkEffect.builder().withColor(Color.SILVER).withColor(Color.RED).withColor(Color.RED).with(FireworkEffect.Type.BALL_LARGE).withColor(Color.YELLOW).build();
                ArrayList<Firework> fwList = new ArrayList<>();
//                fwList.add(p.getWorld().spawn(p.getLocation(), Firework.class));
                fwList.add(p.getWorld().spawn(p.getLocation().subtract(2, 0, -2), Firework.class));
                fwList.add(p.getWorld().spawn(p.getLocation().subtract(2, 0, 2), Firework.class));
                fwList.add(p.getWorld().spawn(p.getLocation().subtract(-2, 0, 2), Firework.class));
                fwList.add(p.getWorld().spawn(p.getLocation().subtract(-2, 0, -2), Firework.class));
//                fwList.add(p.getWorld().spawn(p.getLocation().subtract(0, 1, 0), Firework.class));
                for (Firework fw : fwList) {
                    FireworkMeta fwdata = (FireworkMeta) fw.getFireworkMeta();
                    fwdata.addEffects(fwe);
                    fwdata.setPower(0);
                    fw.setFireworkMeta(fwdata);
                    fw.setCustomName(C.CH_CompositeEffect);
                }
                data = new BooleanResponse(C.CH_CompositeEffect, req.Key, true).ToBytes();
                player.sendPluginMessage(plugin, C.CH_RootChannel, data);
                break;
            case NamePling:
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                data = new BooleanResponse(C.CH_CompositeEffect, req.Key, true).ToBytes();
                player.sendPluginMessage(plugin, C.CH_RootChannel, data);
                break;
            case ReviewListUpdated:
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                data = new BooleanResponse(C.CH_CompositeEffect, req.Key, true).ToBytes();
                player.sendPluginMessage(plugin, C.CH_RootChannel, data);
                break;
        }
    }
    //</editor-fold>
}
