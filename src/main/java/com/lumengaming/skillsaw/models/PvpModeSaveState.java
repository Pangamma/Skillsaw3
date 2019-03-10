/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.models;

import java.util.UUID;
import org.bukkit.GameMode;

/**
 *
 * @author prota
 */
public class PvpModeSaveState {
    public UUID uuid;
    public GameMode originalMode;
    public int Kills;
    public int Deaths;
}
