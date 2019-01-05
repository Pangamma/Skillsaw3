package com.lumengaming.skillsaw.commands.chat;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.Constants;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.utility.SharedUtility;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ChatColorCommand extends BungeeCommand {

    public ChatColorCommand(BungeeMain plugin) {
        super(plugin, "chatcolor", null, "cc");
    }

    private void printHelp(IPlayer cs) {
        cs.sendMessage("§c/chatcolor &2");
        if (Permissions.USER_HAS_PERMISSION(cs, Permissions.CHAT_COLOR_FORMATTNG)) {
            cs.sendMessage("§c/chatcolor &2&n");
            cs.sendMessage("§c/chatcolor &2&L");
            cs.sendMessage("§c/chatcolor &L");
        }
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {
        boolean hasColorBasic = Permissions.USER_HAS_PERMISSION(cs, Permissions.CHAT_COLOR_BASIC, false);
        boolean hasColorFormatting = Permissions.USER_HAS_PERMISSION(cs, Permissions.CHAT_COLOR_FORMATTNG, false);
        boolean hasColorBlack = Permissions.USER_HAS_PERMISSION(cs, Permissions.CHAT_COLOR_BLACK, false);

        if (!(hasColorBasic || hasColorFormatting)) {
            cs.sendMessage(Permissions.TELL_USER_PERMISSION_THEY_LACK(Permissions.CHAT_COLOR_BASIC.node + "' or '" + Permissions.CHAT_COLOR_FORMATTNG.node));
            return;
        }

        if (!cs.isPlayer()) {
            cs.sendMessage(Constants.ERROR_PLAYERS_ONLY);
            return;
        }

        User user = plugin.getDataService().getUser(cs.getUniqueId());
        if (user == null) {
            cs.sendMessage(Constants.ERROR_TRY_AGAIN_LATER_COMMAND);
            return;
        }

        try {
            String prefix = args[0].replace('&', '§');
            if (!ChatColor.stripColor(prefix).replace("§", "").isEmpty()) {
                cs.sendMessage("§cColor codes only. Thanks.");
            }
            prefix = SharedUtility.removeColorCodes(prefix, hasColorFormatting, hasColorBasic, hasColorBlack);
            prefix = prefix.replace("&", "");
            user.setChatColor(prefix);
            cs.sendMessage("§aChanged your chat color.");
            plugin.getDataService().saveUser(user);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
            printHelp(cs);
        }
    }

}
