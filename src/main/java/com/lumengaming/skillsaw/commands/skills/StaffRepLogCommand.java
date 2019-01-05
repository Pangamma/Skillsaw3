package com.lumengaming.skillsaw.commands.skills;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;

public class StaffRepLogCommand extends AbstractViewLogCommand{

    public StaffRepLogCommand(BungeeMain plugin) {
        super(plugin, "sreplog", "staffreplog");
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {
        super.execute(cs, "sreplog", args);
    }
}
