/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.bridge;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.lumengaming.skillsaw.SpigotMain;
import com.lumengaming.skillsaw.common.Constants;
import com.lumengaming.skillsaw.models.XLocation;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.meta.FireworkMeta;

/**
 *
 * @author prota
 */
public class SpigotMessageListener implements org.bukkit.plugin.messaging.PluginMessageListener {

    private final SpigotMain plugin;
    private final Gson gson;

    public SpigotMessageListener(SpigotMain p) {
        this.plugin = p;
        this.gson = new Gson();
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        Bukkit.getScheduler().runTask(plugin, () -> {
        Bukkit.broadcastMessage("§cZ0");
        });
        Bukkit.broadcastMessage("§cZ");
        if (!channel.equals(Constants.CH_RootChannel)) {
            return;
        }
        Bukkit.broadcastMessage("§cY");
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            String subchannel = in.readUTF();
        Bukkit.broadcastMessage("§cX="+subchannel);
            switch (subchannel) {
                case "MaSuitePlayerLocation":
                case Constants.CH_GetPlayerLocation:
                    onGetLocation(player, in);
                    break;
                case Constants.CH_SetPlayerLocation:
                    onSetLocation(player, in);
                    break;
                case Constants.CH_PlaySoundForPlayer:
                    onPlaySoundForPlayer(player, in);
                    break;
                case Constants.CH_CompositeEffect:
                    onCompositeEffectForPlayer(player, in);
                    break;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //<editor-fold defaultstate="collapsed" desc="UTILITY">
    private void SendBoolean(Player player, ByteArrayDataOutput out, long key, boolean value) {
        out.writeLong(key);
        out.writeBoolean(true);
        player.sendPluginMessage(plugin, Constants.CH_RootChannel, out.toByteArray());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="GetLocation">
    private void onGetLocation(Player player, DataInputStream in) throws IOException {
        GetPlayerLocationRequest req = new GetPlayerLocationRequest().FromBytes(in);
        Player p = Bukkit.getPlayer(req.UUID);

        
                p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1F, 1F);
                FireworkEffect fwe = org.bukkit.FireworkEffect.builder().withColor(Color.SILVER).withColor(Color.RED).withColor(Color.RED).with(FireworkEffect.Type.BALL_LARGE).withColor(Color.YELLOW).build();
                ArrayList<Firework> fwList = new ArrayList<>();
                fwList.add(p.getWorld().spawn(p.getLocation(), Firework.class));
                fwList.add(p.getWorld().spawn(p.getLocation().subtract(1, 0, -1), Firework.class));
                fwList.add(p.getWorld().spawn(p.getLocation().subtract(1, 0, 1), Firework.class));
                fwList.add(p.getWorld().spawn(p.getLocation().subtract(-1, 0, 1), Firework.class));
                fwList.add(p.getWorld().spawn(p.getLocation().subtract(-1, 0, -1), Firework.class));
                fwList.add(p.getWorld().spawn(p.getLocation().subtract(0, 1, 0), Firework.class));
                for (Firework fw : fwList) {
                    FireworkMeta data = (FireworkMeta) fw.getFireworkMeta();
                    data.addEffects(fwe);
                    data.setPower(0);
                    fw.setFireworkMeta(data);
                    fw.setCustomName(Constants.CH_CompositeEffect);
                }
        Location loc;
        if (p == null) {
            loc = Bukkit.getWorlds().get(0).getSpawnLocation();
        } else {
            loc = p.getLocation();
        }

        XLocation xLoc = new XLocation();
        xLoc.X = loc.getX();
        xLoc.Y = loc.getY();
        xLoc.Z = loc.getZ();
        xLoc.Yaw = loc.getYaw();
        xLoc.Pitch = loc.getPitch();
        xLoc.World = loc.getWorld().getName();
        xLoc.Server = req.ServerName;

        byte[] data = new GetPlayerLocationResponse(req.Key, xLoc).ToBytes();
        player.sendPluginMessage(plugin, Constants.CH_RootChannel, data);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="SetLocation">
    private void onSetLocation(Player player, DataInputStream in) throws IOException {
        SetPlayerLocationRequest req = new SetPlayerLocationRequest().FromBytes(in);
        Player p = Bukkit.getPlayer(req.UUID);

        if (p == null) {
            byte[] data = new BooleanResponse(Constants.CH_SetPlayerLocation, req.Key, false).ToBytes();
            player.sendPluginMessage(plugin, Constants.CH_RootChannel, data);
            return;
        }

        if (req.Loc == null) {
            byte[] data = new BooleanResponse(Constants.CH_SetPlayerLocation, req.Key, false).ToBytes();
            player.sendPluginMessage(plugin, Constants.CH_RootChannel, data);
            return;
        }

        World w = Bukkit.getWorld(req.Loc.World);
        if (w == null) {
            byte[] data = new BooleanResponse(Constants.CH_SetPlayerLocation, req.Key, false).ToBytes();
            player.sendPluginMessage(plugin, Constants.CH_RootChannel, data);
            return;
        }

        Location loc = new Location(w, req.Loc.X, req.Loc.Y, req.Loc.Z, req.Loc.Yaw, req.Loc.Pitch);
        p.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);

        byte[] data = new BooleanResponse(Constants.CH_SetPlayerLocation, req.Key, true).ToBytes();
        player.sendPluginMessage(plugin, Constants.CH_RootChannel, data);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="PlaySound">
    private void onPlaySoundForPlayer(Player player, DataInputStream in) throws IOException {
        long key = in.readLong();
        String uuid = in.readUTF();
        Player p = Bukkit.getPlayer(UUID.fromString(uuid));
        String soundName = in.readUTF();

        if (p == null) {
            byte[] data = new BooleanResponse(Constants.CH_PlaySoundForPlayer, key, false).ToBytes();
            player.sendPluginMessage(plugin, Constants.CH_RootChannel, data);
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

        byte[] data = new BooleanResponse(Constants.CH_PlaySoundForPlayer, key, true).ToBytes();
        player.sendPluginMessage(plugin, Constants.CH_RootChannel, data);
    }
    //</editor-fold>

    private void onPlayEffect(Player player, DataInputStream in, ByteArrayDataOutput out) throws IOException {
        Player p = Bukkit.getPlayer(UUID.fromString(in.readUTF()));
        if (p == null) {
            return;
        }
        Location loc = p.getLocation();
        out.writeUTF(Constants.CH_GetPlayerLocation);
        out.writeUTF(String.valueOf(p.getUniqueId()));
        out.writeUTF(loc.getWorld().getName());
        out.writeDouble(loc.getX());
        out.writeDouble(loc.getY());
        out.writeDouble(loc.getZ());
        out.writeFloat(loc.getYaw());
        out.writeFloat(loc.getPitch());
        player.sendPluginMessage(plugin, Constants.CH_RootChannel, out.toByteArray());
    }

    private void onCompositeEffectForPlayer(Player player, DataInputStream in) throws IOException {
        Bukkit.broadcastMessage("AAA");
        PlayCompositeEffectRequest req = new PlayCompositeEffectRequest().FromBytes(in);
        Player p = Bukkit.getPlayer(req.UUID);
        if (p == null) {
            return;
        }
        Bukkit.broadcastMessage("B");
        switch (req.Type) {
            case Hmmm:
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, SoundCategory.NEUTRAL, 1, 1);
                break;
            case Scold:
            case LevelDown:
                p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                break;
            case LevelUp:
        Bukkit.broadcastMessage("C");
                p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1F, 1F);
                FireworkEffect fwe = org.bukkit.FireworkEffect.builder().withColor(Color.SILVER).withColor(Color.RED).withColor(Color.RED).with(FireworkEffect.Type.BALL_LARGE).withColor(Color.YELLOW).build();
                ArrayList<Firework> fwList = new ArrayList<>();
                fwList.add(p.getWorld().spawn(p.getLocation(), Firework.class));
                fwList.add(p.getWorld().spawn(p.getLocation().subtract(1, 0, -1), Firework.class));
                fwList.add(p.getWorld().spawn(p.getLocation().subtract(1, 0, 1), Firework.class));
                fwList.add(p.getWorld().spawn(p.getLocation().subtract(-1, 0, 1), Firework.class));
                fwList.add(p.getWorld().spawn(p.getLocation().subtract(-1, 0, -1), Firework.class));
                fwList.add(p.getWorld().spawn(p.getLocation().subtract(0, 1, 0), Firework.class));
                for (Firework fw : fwList) {
                    FireworkMeta data = (FireworkMeta) fw.getFireworkMeta();
                    data.addEffects(fwe);
                    data.setPower(0);
                    fw.setFireworkMeta(data);
                    fw.setCustomName(Constants.CH_CompositeEffect);
                }
                break;
            case NamePling:
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                break;
            case ReviewListUpdated:
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                break;
        }
    }
}
