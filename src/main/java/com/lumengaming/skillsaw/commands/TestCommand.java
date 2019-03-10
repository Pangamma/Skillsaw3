/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.commands;

import com.google.gson.Gson;
import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;

/**
 *
 * @author prota
 */
public class TestCommand extends BungeeCommand{

    public TestCommand(BungeeMain plugin) {
        super(plugin, "Test", null);
    }

    @Override
    public void execute(BungeePlayer csw, String[] args) {
        plugin.getApi().getGlobalStats(false, (s) -> {
            String json = new Gson().toJson(s);
            csw.sendMessage(json);
        });
        
    }
}
