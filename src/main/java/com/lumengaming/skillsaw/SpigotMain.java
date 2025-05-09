/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw;

import com.lumengaming.skillsaw.config.ConfigHelper;
import com.lumengaming.skillsaw.config.SpigotOptions;
import com.lumengaming.skillsaw.utility.C;
import com.lumengaming.skillsaw.listeners.SpigotCommandListener;
import com.lumengaming.skillsaw.listeners.SpigotJoinListener;
import com.lumengaming.skillsaw.bridge.SpigotSender;
import com.lumengaming.skillsaw.commands.SpigotPvpModeCommand;
import com.lumengaming.skillsaw.commands.SpigotTest2Command;
import com.lumengaming.skillsaw.listeners.SpigotPlayerListener;
import com.lumengaming.skillsaw.models.PvpModeSaveState;
import com.lumengaming.skillsaw.utility.SH;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import com.lumengaming.skillsaw.wrappers.SpigotPlayer;
import java.util.HashMap;
import java.util.UUID;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 *
 * @author prota
 */
public class SpigotMain extends JavaPlugin implements ISkillsaw{
    private SpigotSender pluginListener;
    private HashMap<UUID, PvpModeSaveState> pvpModeSaveStates = new HashMap<>();
    
//    private DataService dataService;
    
    @Override
    public void onEnable(){
        if (!Bukkit.getServer().getPluginManager().getPlugin(this.getName()).getDataFolder().exists()){
            Bukkit.getServer().getPluginManager().getPlugin(this.getName()).getDataFolder().mkdir();
        }
        ConfigHelper.DATA_FOLDER = Bukkit.getServer().getPluginManager().getPlugin("Skillsaw3").getDataFolder();

        SpigotOptions.Load();
        SpigotOptions.Save();
        
        this.pluginListener = new SpigotSender(this);
        getServer().getPluginManager().registerEvents(this.pluginListener, this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, C.CH_RootChannel);
        this.getServer().getMessenger().registerIncomingPluginChannel(this, C.CH_RootChannel, this.pluginListener);
        getServer().getPluginManager().registerEvents(new SpigotCommandListener(this), this);
        getServer().getPluginManager().registerEvents(new SpigotJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new SpigotPlayerListener(this), this);
        
        getCommand("pvpmode").setExecutor(new SpigotPvpModeCommand(this));
        getCommand("test2").setExecutor(new SpigotTest2Command(this));
        
        //<editor-fold defaultstate="collapsed" desc="Enable the scoreboards for team colors">        
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        for (ChatColor c : ChatColor.values()) {
            if (c.isColor()){
                if (sb.getTeam(SH.getTeamName(c)) == null) {
                    Team t = sb.registerNewTeam(SH.getTeamName(c));
                    t.setPrefix(c.toString());
                    t.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
                    t.setAllowFriendlyFire(true);     
                    t.setColor(c);
                }
            }
        }
        //</editor-fold>
    }
    
    @Override
    public void onDisable(){
        //<editor-fold defaultstate="collapsed" desc="Disable the scoreboards for team colors">     
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        for (ChatColor c : ChatColor.values()) {
            if (c.isColor() && sb.getTeam(SH.getTeamName(c)) != null) {
                Team t = sb.getTeam(SH.getTeamName(c));
                t.unregister();
            }
        }
        //</editor-fold>   
        HandlerList.unregisterAll(this);
//        this.dataService.onDisable();
        Bukkit.getScheduler().cancelTasks(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        getServer().getServicesManager().unregisterAll(this);
    }

//    @Override
//    public DataService getService() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

    @Override
    public void runTaskAsynchronously(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this, runnable);
    }

    @Override
    public void runTask(Runnable runnable) {
        Bukkit.getScheduler().runTask(this, runnable);
    }

    @Override
    public void playVillagerSound(IPlayer p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playLevelUpEffect(IPlayer p, String reputation_Level_Increased, String aCongratulations_Your_total2_Reputation_L) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playLevelDownEffect(IPlayer p, String cYour_total_4Reputation_Levelc_has_decrea) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void broadcast(String legacyText) {
        Bukkit.broadcastMessage(legacyText);
    }

    @Override
    public IPlayer getPlayer(UUID uuid) {
        if (uuid == null) return null;
        Player p = Bukkit.getPlayer(uuid);
        if (p == null) return null;
        return new SpigotPlayer(p);
    }
    
    @Override
    public void runTaskLater(Runnable runnable, long ticks) {
        Bukkit.getScheduler().runTaskLater(this, runnable, ticks);
    }

    //<editor-fold defaultstate="collapsed" desc="PVP Mode">
    public HashMap<UUID, PvpModeSaveState> getPvpModeSaveStates() {
        return pvpModeSaveStates;
    }
    
    public boolean isPvpModeEnabled(UUID uuid) {
        return pvpModeSaveStates.containsKey(uuid);
    }
    
    public void removePvpPlayer(Player p){
        if (p == null) return;
        PvpModeSaveState state = pvpModeSaveStates.remove(p.getUniqueId());
        if (state == null) return;
        p.setGameMode(state.originalMode);
        SH.removeGlowColor(p);
        for(Player p2 : p.getWorld().getPlayers()){
            if (p2.isValid()){
                if (p2.getLocation().distanceSquared(p.getLocation()) < 200){
                    if (p2.getUniqueId().equals(p.getUniqueId())){
                        p2.sendMessage("§aRemoved from pvp mode.");
                    }else{
                        p2.sendMessage("§4"+p.getDisplayName()+"§c exited pvp mode.");
                    }
                }
            }
        }
    }
    
    public void addPvpPlayer(Player p){
        PvpModeSaveState state = new PvpModeSaveState();
            state.Deaths = 0;
            state.Kills = 0;
            state.originalMode = p.getGameMode();
            state.uuid = p.getUniqueId();
            pvpModeSaveStates.put(p.getUniqueId(), state);
            
            if (SpigotOptions.Get().PvpMode.IsEnabled){
                SH.setGlowColor(ChatColor.DARK_RED, p); // Sets the team as well.
                if (!SpigotOptions.Get().PvpMode.IsGlowEffectEnabled){
                    p.setGlowing(false);
                }
            }
            
            p.setGameMode(GameMode.SURVIVAL);
            for(Player p2 : p.getWorld().getPlayers()){
                if (p2.isValid()){
                    if (p2.getLocation().distanceSquared(p.getLocation()) < 200){
                        if (p2.getUniqueId().equals(p.getUniqueId())){
                            p2.sendMessage("§aEntered into pvp mode.");
                        }else{
                            p2.sendMessage("§4"+p.getDisplayName()+"§c entered into pvp mode with §4/pvpm.");
                        }
                    }
                }
            }
    }
    //</editor-fold>

    @Override
    public LuckPerms getLuckPermsAPI() {
      LuckPermsProvider.get();
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        
        if (provider != null) {
            LuckPerms api = provider.getProvider();
            return api;
        }
        return null;
    }
}
