/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.config;

import com.google.gson.Gson;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.lumengaming.skillsaw.models.BooleanAnswer;
import com.lumengaming.skillsaw.models.SkillType;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.ColorCodeAdapter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Idk. Sometimes I just disagree with Java's naming conventions. Properties are great, alright? C# is bae.
 * @author prota
 */
public class Options
{
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
    
    @SerializedName("review-list")
    public ReviewListOptions ReviewList = new ReviewListOptions();
    
    @SerializedName("strings")
    public StringOptions Strings = new StringOptions();
    
    @SerializedName("rep-system")
    public ReputationOptions RepSystem = new ReputationOptions();
    
    @SerializedName("chat-system")
    public ChatSystemOptions ChatSystem = new ChatSystemOptions();
    
    @SerializedName("data.mysql")
    public MysqlOptions Mysql = new MysqlOptions();
    
    @SerializedName("teleport-system")
    public TeleportOptions Teleport = new TeleportOptions();
    
    @SerializedName("server-close-player-mover")
    public ServerClosingPlayerMoverOptions ServerClosePlayerMover = new ServerClosingPlayerMoverOptions();
    
    @SerializedName("discord")
    public DiscordOptions Discord = new DiscordOptions();
    
    public ArrayList<ForcedHostOption> ForcedHosts = new ArrayList<>();
    
    //</editor-fold>

    public ArrayList<SkillType> getSkillTypes() {
        ArrayList<SkillType> skillTypes = new ArrayList<>();
        skillTypes.add(SkillType.Redstone);
        skillTypes.add(SkillType.Organics);
        skillTypes.add(SkillType.PixelArt);
        skillTypes.add(SkillType.Architecture);
        skillTypes.add(SkillType.Terraforming);
        skillTypes.add(SkillType.Vehicles);
        
//        skillTypes.add(new SkillType(
//            "decoration", // key (for DB) 
//            "Decoration", // list-name
//            0, // default-level
//            0, // min-level
//            10,// max-level
//            5, // min-instruct-level
//            "&5D"+SkillType.LEVEL_VAR_STR, // title-format-short
//            "&5DecoratorT"+SkillType.LEVEL_VAR_STR // title-format-long
//            ));
        
        return skillTypes;
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="SubClasses">
    
    public static class ForcedHostOption{
        
        @SerializedName("host")
        public String Host = "*";
        
        @SerializedName("message-to-player-on-join")
        public String MessageToPlayerOnJoin = "";
        
        @SerializedName("server-to-connect-to")
        public String ServerToConnectTo = "";
        
        @JsonAdapter(ColorCodeAdapter.class)
        @SerializedName("custom-motd")
        public String MOTD = "Hello human!\ntry a snack!";
        
        @SerializedName("enable-custom-motd")
        public boolean IsCustomMotdEnabled = false;
    }
    
    public static class DiscordOptions{
        @SerializedName("enabled")
        public boolean IsEnabled = true;
        
        @SerializedName("invite-link")
        public String InviteLink = "https://discord.gg/QrvqAv2";
    }
    
    public static class ServerClosingPlayerMoverOptions{
        @SerializedName("enabled")
        public boolean IsEnabled = true;
        
        @SerializedName("reconnect-server")
        public String ReconnectServer = "";
        
        @SerializedName("blacklisted-kick-reasons")
        public ArrayList<String> KickReasonBlacklist = new ArrayList<>();
        @SerializedName("whitelisted-kick-reasons")
        public ArrayList<String> KickReasonWhitelist = new ArrayList<>();
    }
    
    public static class ReputationOptions{
        @SerializedName("enabled")
        public boolean IsEnabled = true;
        
        @SerializedName("max-natural-reps-per-time-period")
        public int MaxNaturalRepsPerTimePeriod = 6;
        
        @SerializedName("hours-per-time-period")
        public int HoursPerTimePeriod = 6;
    }
        
    public static class TeleportOptions{
        @SerializedName("enabled")
        public boolean IsEnabled = true;
        
        @SerializedName("default-tpalock-value")
        public BooleanAnswer DefaultTpaLockValue = BooleanAnswer.Yes;
    }
    
    public static class ReviewListOptions{
        @SerializedName("enabled")
        public boolean IsEnabled = true;
        
        @SerializedName("max-entries-in-list")
        public int MaxEntriesInPublicReviewList = 15;
        
        @SerializedName("minutes-required-between-reposts")
        public double MinutesRequiredBetweenReposts = 60;
    }
    
    public static class MysqlOptions{
        
        @SerializedName("enabled")
        public boolean IsEnabled = true;
        public String Host = "127.0.0.1";
        public int Port = 3306;
        public boolean UseSSL = false;
        public String User = "YourUsername";
        public String Pass = "Password";
        public String Database = "skillsaw";
    }
    
    public static class ChatSystemOptions {

        @SerializedName("enabled")
        public boolean IsEnabled = true;
        
        @SerializedName("ignore-list-limit")
        public int MaxIgnoreListSize = 10;
        
        @SerializedName("allow-me-on-main-channel")
        public boolean IsMeAllowedOnMainChannel = true;
        
        @SerializedName("max-short-title-length")
        public int MaxShortTitleLength = 8;
        
        public ChatSystemOptions() {
        }
    }
    
    public static class StringOptions{
        @SerializedName("repped-natural-issuer")
        @JsonAdapter(ColorCodeAdapter.class)
        public String nRepIssuerMessage_012 = "&aGave %amount% rep to %target% for :&2 %reason%";
        
        @SerializedName("repped-natural-target")
        @JsonAdapter(ColorCodeAdapter.class)
        public String nRepTargetMessage_012 = "&a%issuer% just gave you %amount% rep. Reason: %reason%";
        
        @SerializedName("repped-staff-issuer")
        @JsonAdapter(ColorCodeAdapter.class)
        public String sRepIssuerMessage_012 = "&aGave %amount% s-rep to %target% for :&2 %reason%";
        
        @SerializedName("repped-staff-target")
        @JsonAdapter(ColorCodeAdapter.class)
        public String sRepTargetMessage_012 = "&a%issuer% just gave you %amount% staff rep. Reason: %reason%";
        
        @SerializedName("repped-fix-issuer")
        @JsonAdapter(ColorCodeAdapter.class)
        public String xRepIssuerMessage_012 = "&aGave %amount% x-rep to %target% for :&2 %reason%";
        
        @SerializedName("repped-fix-target")
        @JsonAdapter(ColorCodeAdapter.class)
        public String xRepTargetMessage_01 = "&a%issuer% just fixed your rep. Reason: %reason%";
        
        @SerializedName("server-closing-move-message")
        public String ServerClosingMoveMessage = "&7Moved to another server. Reason: %reason%";
        
        /** Replaces isuer, target, amount, and reason variables with the inputs. **/
        public String compileMessageFormat(String format, String issuer, String target, double amount, String reason){
            return format
                    .replace("%issuer%", issuer)
                    .replace("%target%", target)
                    .replace("%amount%", ""+ User.round(amount))
                    .replace("%reason%", reason);
        }
    }
    
    public static class IssuerTargetOptions{
        @SerializedName("target")
        public String Target;
        @SerializedName("issuer")
        public String Issuer;

        public IssuerTargetOptions(){}
        public IssuerTargetOptions(String Issuer, String Target) {
            this.Target = Target;
            this.Issuer = Issuer;
        }
    }
    
    //</editor-fold>
    
    
    
    //<editor-fold defaultstate="collapsed" desc="CORE methods">
    private static final String fileName = "config.json";
    /** 
     * Java sucks compared to C#. I miss my properties!
     * 
     * @return Options
     */
    public static Options Get(){
        if (_get == null){
            _get = Options.Load();
        }
        return _get;
    }
    private static Options _get = null;
    
    public static Options Save(){
        Options o = Get();
        ConfigHelper.SaveJson(o, fileName);
        return _get;
    }
    
    public static Options Load(){
        String json = ConfigHelper.LoadJson(fileName);
        try{
            Gson gson = new Gson();
            if (json != null){
                Options fromJson = gson.fromJson(json, Options.class);
                _get = fromJson;
            }
        }catch(Exception ex){
            Logger.getLogger(ConfigHelper.class.getName()).log(Level.SEVERE, "Failed to load config '"+fileName+"'", ex);
        }
        if (_get == null){
            _get = new Options();
        }
        return _get; 
    }


    public Options() {
        {
            ForcedHostOption fho = new ForcedHostOption();
            fho.Host = "*";
            fho.MessageToPlayerOnJoin = "&4Hey! You're using the wrong server IP. You should switch to using &cplay.woolcity.net:25565&4.";
            fho.ServerToConnectTo = "hub";
            this.ForcedHosts.add(fho);
        }
        {
            ForcedHostOption fho = new ForcedHostOption();
            fho.Host = "play.woolcity.net";
            fho.MessageToPlayerOnJoin = null;
            fho.ServerToConnectTo = "hub";
            this.ForcedHosts.add(fho);
        }
    }
    //</editor-fold>

    
}
