
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.models;

import com.google.gson.Gson;
import java.text.MessageFormat;

/**
 *
 * @author prota
 */
public class XLocation {

    public double X;
    public double Y;
    public double Z;
    public float Yaw;
    public float Pitch;
    public String World;
    public String Server;
    
    
    @Override
    public String toString() {
        return "s:"+Server + " w:" + World + " xyz:(" + X + ", " + Y + ", " + Z
            + ") yaw:" + Math.round(Yaw)
            + ", pitch:" + Math.round(Pitch);
    }   
    
    public String toJson() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }
    
    public String toTeleportCommand() 
    {
        String msg = MessageFormat.format("s:{0} w:{1} {2} {3} {4} yaw:{5} pitch:{6}", Server, World, X, Y, Z, Yaw, Pitch);
        return msg;
    }
}
