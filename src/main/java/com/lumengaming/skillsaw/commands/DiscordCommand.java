/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.config.Options;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import java.util.HashSet;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

/**
 *
 * @author prota
 */
public class DiscordCommand extends BungeeCommand {

  public DiscordCommand(BungeeMain plugin) {
    super(plugin, "discord", null);
    super.addSyntax(null, false, false, "/discord", "Show the discord server link.");
  }

  @Override
  public void execute(BungeePlayer csw, String[] args) {
    BaseComponent[] legacy = CText.legacy("§f" + Options.Get().Discord.InviteLink);
    CText.applyEvent(legacy, new ClickEvent(ClickEvent.Action.OPEN_URL, Options.Get().Discord.InviteLink));
    CText.applyEvent(legacy, new HoverEvent(HoverEvent.Action.SHOW_TEXT, CText.legacy("Click to open")));
    csw.sendMessage(legacy);
  }

  @Override
  public Iterable<String> onTabCompleteBeforeFiltering(CommandSender cs, String[] args) {
    return new HashSet<>();
  }

}
