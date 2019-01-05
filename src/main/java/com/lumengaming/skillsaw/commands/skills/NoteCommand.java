package com.lumengaming.skillsaw.commands.skills;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.models.RepType;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.Constants;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import com.lumengaming.skillsaw.wrappers.IPlayer;

public class NoteCommand extends BungeeCommand {

    public NoteCommand(BungeeMain plugin) {
        super(plugin, "note", null);
    }

    private void printHelp(IPlayer cs) {
        cs.sendMessage("§c/note <player> <message>");
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {

        try {
            if (Permissions.USER_HAS_PERMISSION(cs, Permissions.REP_NOTE, true)) {
                if (cs.isPlayer() == false) {
                    cs.sendMessage(Constants.ERROR_PLAYERS_ONLY);
                    return;
                }

                String targName = args[0];
                plugin.getApi().getOfflineUserByNameOrDisplayName(targName, (target) -> {
                    if (target == null) {
                        cs.sendMessage(Constants.ERROR_P_NOT_FOUND);
                        return;
                    }

                    final User issuer = plugin.getApi().getUser(cs.p().getUniqueId());

                    if (issuer == null) {
                        cs.sendMessage(Constants.ERROR_TRY_AGAIN_LATER_COMMAND);
                        return;
                    }

                    String msg = "";
                    for (int i = 1; i < args.length; i++) {
                        if (i != 1) {
                            msg += " ";
                        }
                        msg += args[i];
                    }

                    if (msg.length() < 5) {
                        cs.sendMessage("§cYour note isn't long enough. Use more detail.");
                        return;
                    }

                    final String fMessage = msg;
                    plugin.getApi().logRep(issuer, target, 0, RepType.Note, fMessage);
                    cs.sendMessage("§aSuccessfully added a note for '" + target.getName() + "'.");
                });
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            printHelp(cs);
        }
    }

}
