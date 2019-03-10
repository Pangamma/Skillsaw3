/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.SpigotMain;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import com.lumengaming.skillsaw.wrappers.SpigotPlayer;

/**
 *
 * @author prota
 */
public class SpigotTest2Command extends SpigotCommand{

    public SpigotTest2Command(SpigotMain plugin) {
        super(plugin);
    }


    @Override
    public void execute(SpigotPlayer cs, String[] args) {
//        Player p = (Player) cs.getRaw();
//        Location loc = p.getLocation();
//        EnderDragon ent = (EnderDragon) loc.getWorld().spawnEntity(loc, EntityType.ENDER_DRAGON);
//        ent.playEffect(EntityEffect.DEATH);
//        ent.setHealth(0.0);
     }

    @Override
    public void printHelp(IPlayer cs) {
    }
}
