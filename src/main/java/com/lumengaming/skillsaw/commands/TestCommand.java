/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.models.XLocation;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;

/**
 *
 * @author prota
 */
public class TestCommand extends BungeeCommand{

    public TestCommand(BungeeMain plugin) {
        super(plugin, "Test", null);
    }

    private static XLocation xloc;
    @Override
    public void execute(BungeePlayer csw, String[] args) {
        if (args.length > 0){
            csw.sendMessage("args=1");
            plugin.getSender().getPlayerLocation(csw.p(), (loc) -> {
                csw.sendMessage(loc.toTeleportCommand());
                xloc = loc;
            });
        }else{
            plugin.getSender().setLocation(csw.p(), xloc, b -> {
                csw.sendMessage("Teleported?"+b);
            });
        }
    }
    
}
