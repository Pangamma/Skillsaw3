/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.models;

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
        return Server + " " + World + " (" + X + ", " + Y + ", " + Z
            + ") Yaw=" + Math.round(Yaw)
            + ", Pitch=" + Math.round(Pitch);
    }
}
