package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.Constants;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;

public class GlobalCommand extends BungeeCommand {

    public GlobalCommand(BungeeMain plugin) {
        super(plugin, "global", null, "g");
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {

        if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.CHANNEL_GLOBAL, true)) {
            return;
        }
        try {
            String msg = String.join(" ", args);
            if (cs.isPlayer()) {
                User u = plugin.getDataService().getUser(cs.getUniqueId());
                if (u == null) {
                    cs.sendMessage(Constants.ERROR_TRY_AGAIN_LATER_COMMAND);
                    return;
                }
                plugin.broadcast("§f[§eglobal§f][" + u.getDisplayName() + "§f]:§7 " + u.getChatColor() + msg);
            } else {
                plugin.broadcast("§f[§eglobal§f][" + cs.getName() + "§f]:§7 " + msg);
            }

        } catch (ArrayIndexOutOfBoundsException ex) {
            cs.sendMessage("§c/g <message>");
        }
    }

}
