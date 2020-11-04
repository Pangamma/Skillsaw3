package com.lumengaming.skillsaw.commands.skills;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.models.RepType;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;

public class ViewLogCommand extends AbstractViewLogCommand {

    public ViewLogCommand(BungeeMain plugin) {
        super(plugin, "viewlog");
        super.addSyntax(Permissions.VIEWLOGS_ALL_REP, false, false, "/viewlog [target]", "View all reputation and notes given to a player.");
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {
        super.execute(cs, "replog", args);
    }
}
