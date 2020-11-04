package com.lumengaming.skillsaw.commands.chat;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.C;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import java.util.HashSet;
import net.md_5.bungee.api.CommandSender;

/**
 *
 * @author Taylor
 */
public class WhisperCommand extends BungeeCommand {

  public WhisperCommand(BungeeMain plug) {
    super(plug, "whisper", null, "msg", "m", "tell", "w", "message");
    super.addSyntax(null, false, false, "/w <username> <message>", "Whisper a private message to someone.");
  }

  @Override
  public Iterable<String> onTabCompleteBeforeFiltering(CommandSender arg0, String[] args) {
    HashSet<String> set = new HashSet<>();
    if (args.length == 1) {
      set.addAll(this.getOnlinePlayerNames());
    }
    return set;
  }

  @Override
  public void execute(BungeePlayer cs, String[] args) {

    if (!cs.isPlayer()) {
      cs.sendMessage(C.ERROR_PLAYERS_ONLY);
      return;
    }

    try {
      User cSender = plugin.getApi().getUser(cs.getUniqueId());
      User cTarget = plugin.getApi().getUserBestOnlineMatch(args[0]);
      if (cTarget == null) {
        cs.sendMessage(C.ERROR_P_NOT_FOUND);
        return;
      }

      if (cSender == null) {
        cs.sendMessage(C.ERROR_TRY_AGAIN_LATER_COMMAND);
        return;
      }

      if (cTarget.isIgnoringPlayer(cSender.getName())) {
        cs.sendMessage(C.ERROR_P_IGNORING_YOU);
        return;
      }

      if (cSender.isIgnoringPlayer(cTarget.getName())) {
        cs.sendMessage(C.ERROR_P_YOU_ARE_IGNORING);
        return;
      }

      String s = "";
      for (int i = 1; i < args.length; i++) {
        s += args[i] + " ";
      }
      s = s.trim();

      if (cSender != null) {
        cSender.setLastWhispered(cTarget.getUniqueId());
      }

      if (cTarget != null) {
        cTarget.setLastWhispered(cSender.getUniqueId());
      }

      cTarget.sendMessage(CText.merge(CText.hoverTextSuggest("§7From " + cSender.getName() + ": ", "Click to reply", "/msg " + cSender.getName() + " "), CText.legacy("§7" + s)));
      cSender.sendMessage(CText.merge(CText.hoverTextSuggest("§7To " + cTarget.getName() + ": ", "Click to message again", "/msg " + cTarget.getName() + " "), CText.legacy("§7" + s)));
    } catch (ArrayIndexOutOfBoundsException ex) {
      cs.sendMessage(CText.legacy("§c/msg <player> <message>"));
    }
  }

}
