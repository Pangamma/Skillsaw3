/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.common;

import java.util.UUID;

public interface IPlayer {
    public String getName();
    public UUID getUniqueId();

    public String getDisplayName();

    public boolean hasPlayedBefore();

    public long getFirstPlayed();

    public String getIpv4(); // getAddress().getAddress().getHostAddress();
}
