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
 * Literally does nothing. Purely used as a placeholder so that the bungeecord
 * plugins are able to provide autocomplete for certain commands like /title.
 * @author prota
 */
public class SpigotDummyCommand extends SpigotCommand{

    public SpigotDummyCommand(SpigotMain plugin) {
        super(plugin);
    }

    @Override
    public void execute(SpigotPlayer cs, String[] args) {
     }

    @Override
    public void printHelp(IPlayer cs) {
    }
}
