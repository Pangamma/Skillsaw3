package com.lumengaming.skillsaw.commands.chat;

import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.config.Options;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.C;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import net.md_5.bungee.api.chat.BaseComponent;

public class MeeCommand extends BungeeCommand {

    public MeeCommand(BungeeMain plugin) {
        super(plugin, "mee", null, "me");
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {

        if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.MEE, true)) {
            return;
        }

        if (!cs.isPlayer()) {
            cs.sendMessage(C.ERROR_PLAYERS_ONLY);
            return;
        }

        String msg = String.join(" ", args);
        User u = plugin.getDataService().getUser(cs.getUniqueId());
        if (u == null) {
            cs.sendMessage(C.ERROR_TRY_AGAIN_LATER_COMMAND);
            return;
        }

        if (u.getSpeakingChannel().equalsIgnoreCase("1") && !Options.Get().ChatSystem.IsMeAllowedOnMainChannel) {
            cs.sendMessage("§cPlease don't use /me on the main channel. Thanks!");
            return;
        }

        BaseComponent[] txt = CText.merge(CText.legacy("§f* "), u.getNameForChat());
        txt = CText.merge(txt, CText.legacy(u.getChatColor() + " " + msg));

        plugin.getDataService().sendMessageToChannel(cs.getName(), u.getSpeakingChannel(), txt);

    }

}
