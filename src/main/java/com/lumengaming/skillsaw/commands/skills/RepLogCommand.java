package com.lumengaming.skillsaw.commands.skills;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;

public class RepLogCommand extends AbstractViewLogCommand {

    public RepLogCommand(BungeeMain plugin) {
        super(plugin, "replog");
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {
        super.execute(cs, "replog", args);
    }
}
