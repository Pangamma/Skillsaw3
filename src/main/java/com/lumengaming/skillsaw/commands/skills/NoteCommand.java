package com.lumengaming.skillsaw.commands.skills;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.models.RepType;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.C;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.util.HashSet;
import net.md_5.bungee.api.CommandSender;

public class NoteCommand extends BungeeCommand {

  public NoteCommand(BungeeMain plugin) {
    super(plugin, "note", null);
    super.addSyntax(Permissions.REP_NOTE, false, false, "/note <name> <reason>", "Add a note about a player.");
  }

  private void printHelp(IPlayer cs) {
    cs.sendMessage("§c/note <player> <message>");
  }

  @Override
  public Iterable<String> onTabCompleteBeforeFiltering(CommandSender cs, String[] args) {
    HashSet<String> set = new HashSet<>();

    switch (args.length) {
      case 1:
        set.addAll(this.getOnlinePlayerNames());
        break;
      case 2:
        set.add("<message>");
        break;
      default:
        break;
    }

    return set;
  }

  @Override
  public void execute(BungeePlayer cs, String[] args) {

    try {
      if (Permissions.USER_HAS_PERMISSION(cs, Permissions.REP_NOTE, true)) {
        if (cs.isPlayer() == false) {
          cs.sendMessage(C.ERROR_PLAYERS_ONLY);
          return;
        }

        String targName = args[0];
        plugin.getApi().getOfflineUserByNameOrDisplayName(targName, (target) -> {
          if (target == null) {
            cs.sendMessage(C.ERROR_P_NOT_FOUND);
            return;
          }

          final User issuer = plugin.getApi().getUser(cs.p().getUniqueId());

          if (issuer == null) {
            cs.sendMessage(C.ERROR_TRY_AGAIN_LATER_COMMAND);
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
