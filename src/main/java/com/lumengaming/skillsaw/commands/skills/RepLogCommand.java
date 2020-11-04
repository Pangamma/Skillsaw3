package com.lumengaming.skillsaw.commands.skills;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.models.RepType;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;

public class RepLogCommand extends AbstractViewLogCommand {

  public RepLogCommand(BungeeMain plugin) {
    super(plugin, "replog");
    super.addSyntax(Permissions.VIEWLOGS_NATURAL_REP, false, false, "/replog [target]", "View " + RepType.NaturalRep.name() + "(s) given to a player.");
  }

  @Override
  public void execute(BungeePlayer cs, String[] args) {
    super.execute(cs, "replog", args);
  }
}
