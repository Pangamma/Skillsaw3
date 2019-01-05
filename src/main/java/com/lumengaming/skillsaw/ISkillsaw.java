/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw;

import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.util.UUID;


/**
 *
 * @author prota
 */
public interface ISkillsaw {
//    public DataService getService();
    public void runTask(Runnable runnable);
    public void runTaskAsynchronously(Runnable runnable);
    public void runTaskLater(Runnable runnable, long ticks);

    public void playVillagerSound(IPlayer p);

    public void playLevelUpEffect(IPlayer p, String reputation_Level_Increased, String aCongratulations_Your_total2_Reputation_L);

    public void playLevelDownEffect(IPlayer p, String cYour_total_4Reputation_Levelc_has_decrea);
    
    public void broadcast(String legacyText);

    public IPlayer getPlayer(UUID uuid);
}
