/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.models;

/**
 * Used for calculating permissions
 * @author prota
 */
public enum MetricType {
    U_Redstone("u-skill-redstone", new Range<Integer>(0,10)),
    U_Organics("u-skill-organics", new Range<Integer>(0,10)),
    U_PixelArt("u-skill-pixelart", new Range<Integer>(0,10)),
    U_Architecture("u-skill-architecture", new Range<Integer>(0,10)),
    U_Terraforming("u-skill-terraforming", new Range<Integer>(0,10)),
    U_Vehicles("u-skill-vehicles", new Range<Integer>(0,10)),
    U_MaxSkill("u-skill-max", new Range<Integer>(0)),
    U_SkillSum("u-skill-sum", new Range<Integer>(0)),
    U_RepLevel("u-rep-level", new Range<Integer>(0)),
    U_IsStaff("u-is-staff", new Range<Boolean>(Boolean.FALSE, Boolean.TRUE)),
    U_IsInstructor("u-is-instructor", new Range<Boolean>(false,true)),
    U_ActivityScore("u-activity-score", new Range<Integer>(0)),
    U_StaffRep("u-rep-staff", new Range<Double>(0D)),
    U_NRep("u-rep-natural", new Range<Double>(0D)),
    U_ActivityPerWeek("u-activity-per-week", new Range<Integer>(0)),
    U_ActivityPerMonth("u-activity-per-month", new Range<Integer>(0)),
    U_ActivityPerTwoWeeks("u-activity-per-two-weeks", new Range<Integer>(0)),
    U_ActivityTotal("u-activity-total", new Range<Integer>(0)),
    U_VotesPerDay("u-votes-per-day", new Range<Integer>(0)),
    U_VotesPerWeek("u-votes-per-week", new Range<Integer>(0)),
    U_VotesPerMonth("u-votes-per-month", new Range<Integer>(0)),
    
    
    G_ActivityPerWeek("g-activity-per-week", new Range<Integer>(0)),
    G_ActivityPerMonth("g-activity-per-month", new Range<Integer>(0)),
    G_ActivityPerTwoWeeks("g-activity-per-two-weeks", new Range<Integer>(0)),
    G_VotesPerDay("g-votes-per-day", new Range<Integer>(0)),
    G_VotesPerWeek("g-votes-per-week", new Range<Integer>(0)),
    G_VotesPerMonth("g-votes-per-month", new Range<Integer>(0))
    ;
    
    private final String key;
    private Range<? extends Comparable<?>> defaultRange;
    
    MetricType(String jsonKey){
        this.key = jsonKey;
    }
    
    MetricType(String jsonKey, Range<? extends Comparable<?>> defaultRange){
        this.key = jsonKey;
        this.defaultRange = defaultRange;
    }

    public String getKey() {
        return key;
    }

    public Range<? extends Comparable<?>> getDefaultRange() {
        return this.defaultRange;
    }

}
