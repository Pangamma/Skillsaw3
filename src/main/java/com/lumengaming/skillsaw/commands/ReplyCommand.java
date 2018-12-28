package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.Constants;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import java.util.UUID;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author Taylor
 */
public class ReplyCommand extends BungeeCommand {

    public ReplyCommand(BungeeMain plug) {
        super(plug, "reply", null, "r");
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {

        if (!cs.isPlayer()) {
            cs.sendMessage(Constants.ERROR_PLAYERS_ONLY);
            return;
        }

        try {
            User cSender = plugin.getApi().getUser(cs.getUniqueId());
            UUID lastWhispered = cSender.getLastWhispered();
            if (lastWhispered == null) {
                cs.sendMessage(CText.legacy("§cThe server is confused and does not recall who you last whispered."));
                return;
            }

            User cTarget = plugin.getApi().getUser(lastWhispered);
            if (cTarget == null) {
                cSender.sendMessage(CText.legacy("§cPlayer is no longer available."));
                return;
            }

            if (cTarget.isIgnoringPlayer(cSender.getName())) {
                cs.sendMessage(Constants.ERROR_P_IGNORING_YOU);
                return;
            }

            if (cSender.isIgnoringPlayer(cTarget.getName())) {
                cs.sendMessage(Constants.ERROR_P_YOU_ARE_IGNORING);
                return;
            }

            cTarget.setLastWhispered(cSender.getUniqueId());
            String s = "";
            for (int i = 0; i < args.length; i++) {
                s += args[i] + " ";
            }
            s = s.trim();

            cTarget.sendMessage(CText.hoverTextSuggest("§7From " + cSender.getName() + ": " + s, "Click to reply", "/msg " + cSender.getName() + " "));
            cSender.sendMessage(CText.hoverTextSuggest("§7To " + cTarget.getName() + ": " + s, "Click to message again", "/msg " + cTarget.getName() + " "));
        } catch (ArrayIndexOutOfBoundsException ex) {
            cs.sendMessage(CText.legacy("§c/msg <player> <message>"));
        }
    }
}
