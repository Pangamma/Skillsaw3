/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.models;

import java.util.HashMap;


public class GlobalStatsView {
    public int G_ActivityPerWeek;
    public int G_ActivityPerMonth;
    public int G_ActivityPerTwoWeeks;
    public int G_VotesPerDay;
    public int G_VotesPerWeek;
    public int G_VotesPerMonth;

    public GlobalStatsView(){}
    public GlobalStatsView(int G_ActivityPerWeek, int G_ActivityPerMonth, int G_ActivityPerTwoWeeks, int G_VotesPerDay, int G_VotesPerWeek, int G_VotesPerMonth) {
        this.G_ActivityPerWeek = G_ActivityPerWeek;
        this.G_ActivityPerMonth = G_ActivityPerMonth;
        this.G_ActivityPerTwoWeeks = G_ActivityPerTwoWeeks;
        this.G_VotesPerDay = G_VotesPerDay;
        this.G_VotesPerWeek = G_VotesPerWeek;
        this.G_VotesPerMonth = G_VotesPerMonth;
    }
    
    public HashMap<String, ? extends Comparable> getMetricValues(){
        HashMap<String,Comparable> vals = new HashMap<>();
        vals.put(MetricType.G_ActivityPerMonth.getKey(), G_ActivityPerMonth);
        vals.put(MetricType.G_ActivityPerTwoWeeks.getKey(), G_ActivityPerTwoWeeks);
        vals.put(MetricType.G_ActivityPerWeek.getKey(), G_ActivityPerWeek);
        
        vals.put(MetricType.G_VotesPerMonth.getKey(), G_VotesPerMonth);
        vals.put(MetricType.G_VotesPerWeek.getKey(), G_VotesPerWeek);
        vals.put(MetricType.G_VotesPerDay.getKey(), G_VotesPerDay);
        return vals;
    } 
}
