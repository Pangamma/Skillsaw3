/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.config;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.lumengaming.skillsaw.models.GlobalStatsView;
import com.lumengaming.skillsaw.models.MetricType;
import com.lumengaming.skillsaw.models.Range;
import com.lumengaming.skillsaw.models.UserStatsView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author prota
 */
public class PermissionsConfig{
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
//    @SerializedName("is-enhanced-plugin-list-enabled")
//    public boolean IsEnhancedPluginListEnabled = true;
    //</editor-fold>

    
    //<editor-fold defaultstate="collapsed" desc="SubClasses">
        public static class PermissionSet{
            
            @SerializedName("context-key")
            public String ContextKey = "context.key";
            
            @SerializedName("rules")
            public HashMap<String, Range<? extends Comparable>> Rules = new HashMap<>();
            
            // All will be considered TRUE if present.
            public ArrayList<String> nodes = new ArrayList<String>();
            
            public PermissionSet(){
                for(MetricType mt : MetricType.values()){
                    Rules.put(mt.getKey(), mt.getDefaultRange());
                }
            }
            
            public boolean isPassing(UserStatsView userView, GlobalStatsView globalView){
                HashMap<String, ? extends Comparable> vals = globalView.getMetricValues();
                HashMap<String, ? extends Comparable> vals2 = userView.getMetricValues();
                
                for(String key : Rules.keySet()){
                    Range<? extends Comparable> rule = Rules.get(key);
                    Comparable globalVal = vals.get(key);
                    
                    if (globalVal != null){
                        if (!rule.isInRangeGeneric(globalVal)){
                            return false;
                        }
                    }
                    
                    Comparable userVal = vals2.get(key);
                    if (userVal != null){
                        if (!rule.isInRangeGeneric(userVal)){
                            return false;
                        }
                    }
                }
                
                return true;
            }
        }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CORE methods">
    private static final String fileName = "permissions.json";
    /** 
     * Java sucks compared to C#. I miss my properties!
     * 
     * @return Options
     */
    public static PermissionsConfig Get(){
        if (_get == null){
            _get = PermissionsConfig.Load();
        }
        return _get;
    }
    private static PermissionsConfig _get = null;
    
    public static PermissionsConfig Save(){
        PermissionsConfig o = Get();
        ConfigHelper.SaveJson(o, fileName);
        return _get;
    }
    
    public static PermissionsConfig Load(){
        String json = ConfigHelper.LoadJson(fileName);
        try{
            Gson gson = ConfigHelper.getGson();
            if (json != null){
                PermissionsConfig fromJson = gson.fromJson(json, PermissionsConfig.class);
                _get = fromJson;
            }
        }catch(Exception ex){
            Logger.getLogger(PermissionsConfig.class.getName()).log(Level.SEVERE, "Failed to load config '"+fileName+"'", ex);
        }
        if (_get == null){
            _get = new PermissionsConfig();
        }
        return _get; 
    }


    public PermissionsConfig() {
    }
    //</editor-fold>
    
}
