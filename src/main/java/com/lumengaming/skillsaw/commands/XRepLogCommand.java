package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;

public class XRepLogCommand extends AbstractViewLogCommand{

    public XRepLogCommand(BungeeMain plugin) {
        super(plugin, "xreplog");
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {
        super.execute(cs, "xreplog", args);
    }
}
