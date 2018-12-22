/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Idk. Sometimes I just disagree with Java's naming conventions. Properties are great, alright? C# is bae.
 * @author prota
 */
public class Options
{
    //<editor-fold defaultstate="collapsed" desc="Properties">
    @SerializedName("is-enhanced-plugin-list-enabled")
    public boolean IsEnhancedPluginListEnabled = true;
    
    @SerializedName("review-list")
    public ReviewListOptions ReviewList = new ReviewListOptions();
    
    @SerializedName("strings")
    public StringOptions Strings = new StringOptions();
    
    @SerializedName("rep-system")
    public ReputationOptions RepSystem = new ReputationOptions();
    
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
    
    public static class StringOptions{
        @SerializedName("repped-natural")
        public IssuerTargetOptions nRep = new IssuerTargetOptions("&aGave %amount% rep to %target% for :&2 %reason%", "&a%issuer% just gave you %amount% rep. Reason: %reason%");
        
        @SerializedName("repped-staff")
        public IssuerTargetOptions sRep = new IssuerTargetOptions("&aGave %amount% s-rep to %target% for :&2 %reason%", "&a%issuer% just gave you %amount% staff rep. Reason: %reason%");
        
        @SerializedName("repped-fix")
        public IssuerTargetOptions xRep = new IssuerTargetOptions("&aGave %amount% x-rep to %target% for :&2 %reason%", "&a%issuer% just fixed your rep. Reason: %reason%");
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
