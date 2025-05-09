package com.lumengaming.skillsaw.commands.skills;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.models.RepType;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;

public class XRepLogCommand extends AbstractViewLogCommand{

    public XRepLogCommand(BungeeMain plugin) {
        super(plugin, "xreplog");
        super.addSyntax(Permissions.VIEWLOGS_REP_FIX, true, false, "/xreplog [target]", "View "+RepType.XRep.name() + "(s) given to a player.");
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {
        super.execute(cs, "xreplog", args);
    }
}
