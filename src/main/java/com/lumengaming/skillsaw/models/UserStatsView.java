/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.models;

import com.google.gson.annotations.JsonAdapter;
import com.lumengaming.skillsaw.utility.json.UUIDAdapter;
import java.util.HashMap;
import java.util.UUID;

/**
 * Used for determining permissions
 * @author prota
 */
public class UserStatsView{
    public String username = null;
    @JsonAdapter(UUIDAdapter.class)
    public UUID uuid = null;
    
    public int U_Redstone = 0;
    public int U_Organics = 0;
    public int U_PixelArt = 0;
    public int U_Architecture = 0;
    public int U_Terraforming = 0;
    public int U_Vehicles = 0;
    public int U_RepLevel = 0;
    public boolean U_IsStaff = false;
    public boolean U_IsInstructor = false;
    public int U_MaxSkill = 0;
    public int U_SkillSum = 0;
    public double U_StaffRep = 0;
    public double U_NRep = 0;
    public int U_ActivityPerWeek;
    public int U_ActivityPerMonth;
    public int U_ActivityPerTwoWeeks;
    public int U_ActivityTotal;
    public int U_VotesPerDay;
    public int U_VotesPerWeek;
    public int U_VotesPerMonth;
    
    public UserStatsView() {
    }

    public HashMap<String, ? extends Comparable> getMetricValues(){
        HashMap<String,Comparable> vals = new HashMap<>();
        
        vals.put(MetricType.U_Redstone.getKey(), U_Redstone);
        vals.put(MetricType.U_Organics.getKey(), U_Organics);
        vals.put(MetricType.U_PixelArt.getKey(), U_PixelArt);
        vals.put(MetricType.U_Architecture.getKey(), U_Architecture);
        vals.put(MetricType.U_Terraforming.getKey(), U_Terraforming);
        vals.put(MetricType.U_Vehicles.getKey(), U_Vehicles);
        
        vals.put(MetricType.U_SkillSum.getKey(), U_SkillSum);
        vals.put(MetricType.U_MaxSkill.getKey(), U_MaxSkill);
        
        vals.put(MetricType.U_RepLevel.getKey(), U_RepLevel);
        vals.put(MetricType.U_StaffRep.getKey(),U_StaffRep);
        vals.put(MetricType.U_NRep.getKey(),U_NRep);
        
        vals.put(MetricType.U_IsInstructor.getKey(), U_IsInstructor);
        vals.put(MetricType.U_IsStaff.getKey(), U_IsStaff);
        
        vals.put(MetricType.U_ActivityPerMonth.getKey(),U_ActivityPerMonth);
        vals.put(MetricType.U_ActivityPerTwoWeeks.getKey(),U_ActivityPerTwoWeeks);
        vals.put(MetricType.U_ActivityPerWeek.getKey(),U_ActivityPerWeek);
        vals.put(MetricType.U_ActivityTotal.getKey(),U_ActivityTotal);
        
        vals.put(MetricType.U_VotesPerMonth.getKey(),U_VotesPerMonth);
        vals.put(MetricType.U_VotesPerWeek.getKey(),U_VotesPerWeek);
        vals.put(MetricType.U_VotesPerDay.getKey(),U_VotesPerDay);
        return vals;
    } 
}
