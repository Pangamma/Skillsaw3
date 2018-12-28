/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.Options;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

/**
 *
 * @author prota
 */
public class DiscordCommand extends BungeeCommand{

    public DiscordCommand(BungeeMain plugin) {
        super(plugin, "discord", null);
    }

    @Override
    public void execute(BungeePlayer csw, String[] args) {
        BaseComponent[] legacy = CText.legacy("§aHere is a link to our discord server! " + Options.Get().DiscordInviteLink);
        CText.applyEvent(legacy, new ClickEvent(ClickEvent.Action.OPEN_URL, Options.Get().DiscordInviteLink));
        CText.applyEvent(legacy, new HoverEvent(HoverEvent.Action.SHOW_TEXT, CText.legacy("Click to copy")));
        csw.sendMessage(legacy);
    }
    
}
