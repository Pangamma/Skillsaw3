/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw;

import com.lumengaming.skillsaw.wrappers.IPlayer;


/**
 *
 * @author prota
 */
public interface ISkillsaw {
//    public DataService getService();
    public void runTaskAsynchronously(Runnable runnable);
    public void runTask(Runnable runnable);

    public void playVillagerSound(IPlayer p);

    public void playLevelUpEffect(IPlayer p, String reputation_Level_Increased, String aCongratulations_Your_total2_Reputation_L);

    public void playLevelDownEffect(IPlayer p, String cYour_total_4Reputation_Levelc_has_decrea);
}
