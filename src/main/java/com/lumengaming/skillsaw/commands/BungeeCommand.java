/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author prota
 */
public abstract class BungeeCommand extends Command {

    protected final BungeeMain plugin;

    public BungeeCommand(BungeeMain plugin, String name, String permission, String... aliases) {
        super(name, permission, aliases);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender csw, String[] args) {
        BungeePlayer cs = new BungeePlayer(csw);
        this.execute(cs, args);
    }

    public abstract void execute(BungeePlayer csw, String[] args);
}
