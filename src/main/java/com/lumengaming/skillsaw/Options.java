/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.lumengaming.skillsaw.models.SkillType;
import com.lumengaming.skillsaw.models.User;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Idk. Sometimes I just disagree with Java's naming conventions. Properties are great, alright? C# is bae.
 * @author prota
 */
public class Options
{
    //<editor-fold defaultstate="collapsed" desc="Properties">
    @SerializedName("is-chat-enabled")
    public boolean IsChatEnabled;
    
    @SerializedName("is-enhanced-plugin-list-enabled")
    public boolean IsEnhancedPluginListEnabled = true;
    
    @SerializedName("review-list")
    public ReviewListOptions ReviewList = new ReviewListOptions();
    
    @SerializedName("strings")
    public StringOptions Strings = new StringOptions();
    
    @SerializedName("rep-system")
    public ReputationOptions RepSystem = new ReputationOptions();
    
    @SerializedName("data.mysql")
    public MysqlOptions Mysql = new MysqlOptions();
    
//    
//    
//    public DataService dataService;
//    public boolean enableMysql;
//    public boolean enableSqlite;
//    public String dataStorageFormat;
//    public ArrayList<SkillType> skillTypes;
//    public boolean isChatEnabled;
//	public boolean isPermissionSystemEnabled;
    
    //</editor-fold>

    public ArrayList<SkillType> getSkillTypes() {
        ArrayList<SkillType> skillTypes = new ArrayList<>();
        skillTypes.add(new SkillType(
            "redstone", // key (for DB) 
            "Redstone", // list-name
            0, // default-level
            0, // min-level
            10,// max-level
            5, // min-instruct-level
            "&cR"+SkillType.LEVEL_VAR_STR, // title-format-short
            "&cRedstonerT"+SkillType.LEVEL_VAR_STR // title-format-long
            ));
        
        skillTypes.add(new SkillType(
            "organics", // key (for DB) 
            "Organics", // list-name
            0, // default-level
            0, // min-level
            10,// max-level
            5, // min-instruct-level
            "&9O"+SkillType.LEVEL_VAR_STR, // title-format-short
            "&9OrganicsT"+SkillType.LEVEL_VAR_STR // title-format-long
            ));
        
        skillTypes.add(new SkillType(
            "pixelart", // key (for DB) 
            "PixelArt", // list-name
            0, // default-level
            0, // min-level
            10,// max-level
            5, // min-instruct-level
            "&aP"+SkillType.LEVEL_VAR_STR, // title-format-short
            "&aPixelArtistT"+SkillType.LEVEL_VAR_STR // title-format-long
            ));
        
        skillTypes.add(new SkillType(
            "architecture", // key (for DB) 
            "Architecture", // list-name
            0, // default-level
            0, // min-level
            10,// max-level
            5, // min-instruct-level
            "&2A"+SkillType.LEVEL_VAR_STR, // title-format-short
            "&2ArchitectT"+SkillType.LEVEL_VAR_STR // title-format-long
            ));
        
        skillTypes.add(new SkillType(
            "terraforming", // key (for DB) 
            "Terraforming", // list-name
            0, // default-level
            0, // min-level
            10,// max-level
            5, // min-instruct-level
            "&dT"+SkillType.LEVEL_VAR_STR, // title-format-short
            "&dTerraformerT"+SkillType.LEVEL_VAR_STR // title-format-long
            ));
            
        skillTypes.add(new SkillType(
            "vehicles", // key (for DB) 
            "Vehicles", // list-name
            0, // default-level
            0, // min-level
            10,// max-level
            5, // min-instruct-level
            "&3N"+SkillType.LEVEL_VAR_STR, // title-format-short
            "&3NavigatorT"+SkillType.LEVEL_VAR_STR // title-format-long
            ));
        
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
    public static class ReputationOptions{
        @SerializedName("enabled")
        public boolean IsEnabled = true;
        
        @SerializedName("max-natural-reps-per-time-period")
        public int MaxNaturalRepsPerTimePeriod = 6;
        
        @SerializedName("hours-per-time-period")
        public int HoursPerTimePeriod = 6;
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
    
    public static class StringOptions{
        @SerializedName("repped-natural-issuer")
        public String nRepIssuerMessage_012 = "&aGave %amount% rep to %target% for :&2 %reason%";
        
        @SerializedName("repped-natural-target")
        public String nRepTargetMessage_012 = "&a%issuer% just gave you %amount% rep. Reason: %reason%";
        
        @SerializedName("repped-staff-issuer")
        public String sRepIssuerMessage_012 = "&aGave %amount% s-rep to %target% for :&2 %reason%";
        
        @SerializedName("repped-staff-target")
        public String sRepTargetMessage_012 = "&a%issuer% just gave you %amount% staff rep. Reason: %reason%";
        
        @SerializedName("repped-fix-issuer")
        public String xRepIssuerMessage_012 = "&aGave %amount% x-rep to %target% for :&2 %reason%";
        
        @SerializedName("repped-fix-target")
        public String xRepTargetMessage_01 = "&a%issuer% just fixed your rep. Reason: %reason%";
        
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
    }
    //</editor-fold>
    
}
