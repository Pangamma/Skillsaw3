/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.models;

import java.util.UUID;

/**
 *
 * @author prota
 */
public class PlayerServerPingInfo {
    public String Username;
    public UUID Uuid;
    public String IpAddress;
    public String HostName;

    public PlayerServerPingInfo(String Username, UUID Uuid, String IpAddress, String HostName) {
        this.Username = Username;
        this.Uuid = Uuid;
        this.IpAddress = IpAddress;
        this.HostName = HostName;
    }
}
