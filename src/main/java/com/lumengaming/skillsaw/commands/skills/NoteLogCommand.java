package com.lumengaming.skillsaw.commands.skills;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.models.RepType;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;

public class NoteLogCommand extends AbstractViewLogCommand {

  public NoteLogCommand(BungeeMain plugin) {
    super(plugin, "notes", "notelog");
    super.addSyntax(Permissions.VIEWLOGS_NOTE, false, false, "/notes [target]", "View " + RepType.Note.name() + "(s) given to a player.");
  }

  @Override
  public void execute(BungeePlayer cs, String[] args) {
    super.execute(cs, "notes", args);
  }
}
