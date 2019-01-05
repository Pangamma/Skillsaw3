/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.models;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author prota
 */
public class SlogSettings {
    @SerializedName("enabled")
    public boolean IsEnabled = false;
    
    @SerializedName("is-global")
    public boolean IsGlobal = true;
    
    @SerializedName("filter-out-activity-above")
    public short ShowOnlyIfActivityBelow = Short.MAX_VALUE;
    
    public SlogSettings reset(){
        this.IsEnabled = false;
        this.IsGlobal = true;
        this.ShowOnlyIfActivityBelow = Short.MAX_VALUE;
        return this;
    }
    
}
