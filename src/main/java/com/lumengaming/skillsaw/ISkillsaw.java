/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw;


/**
 *
 * @author prota
 */
public interface ISkillsaw {
//    public DataService getService();
    public void runTaskAsynchronously(Runnable runnable);
    public void runTask(Runnable runnable);
}
