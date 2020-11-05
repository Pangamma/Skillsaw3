package com.lumengaming.skillsaw.commands.skills;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.models.RepType;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;

public class StaffRepLogCommand extends AbstractViewLogCommand{

    public StaffRepLogCommand(BungeeMain plugin) {
        super(plugin, "sreplog", "staffreplog");
        super.addSyntax(Permissions.VIEWLOGS_STAFF_REP, false, false, "/sreplog [target]", "View "+RepType.StaffRep.name() + "(s) given to a player.");
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {
        super.execute(cs, "sreplog", args);
    }
}
