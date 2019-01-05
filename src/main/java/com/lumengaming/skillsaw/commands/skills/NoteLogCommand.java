package com.lumengaming.skillsaw.commands.skills;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;

public class NoteLogCommand extends AbstractViewLogCommand{

    public NoteLogCommand(BungeeMain plugin) {
        super(plugin, "notes", "notelog");
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {
        super.execute(cs, "notes", args);
    }
}
