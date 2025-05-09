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
public class LocalizationSettings {
    
    @SerializedName("enabled")
    public boolean IsEnabled = false;
    
    @SerializedName("locale")
    public String Locale = "en";
    
    public LocalizationSettings reset(){
        this.IsEnabled = false;
        this.Locale = "en";
        return this;
    }
}
