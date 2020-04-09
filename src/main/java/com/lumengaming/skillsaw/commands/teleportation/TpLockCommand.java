package com.lumengaming.skillsaw.commands.teleportation;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.models.BooleanAnswer;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import java.util.HashSet;
import net.md_5.bungee.api.CommandSender;

/**
 * @author Taylor
 */
public class TpLockCommand extends BungeeCommand {

  public TpLockCommand(BungeeMain aThis) {
    super(aThis, "tpalock", null, "tplock", "teleportlock");
    super.addSyntax(Permissions.TPA_LOCK, false, false, "/tpalock yes/no/ask", "Automatically accept or deny\nteleport requests with this\ncommand.");
  }

  @Override
  public Iterable<String> onTabCompleteBeforeFiltering(CommandSender cs, String[] args) {
    HashSet<String> set = new HashSet<>();
    if (args.length == 1) {
      set.add("yes");
      set.add("no");
      set.add("ask");
    }
    return set;
  }

  @Override
  public void execute(BungeePlayer cs, String[] args) {
    if (args.length != 1) {
      printHelp2(cs);
    } else {
      if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.TPA_LOCK, true)) {
        return;
      }

      BooleanAnswer answer = BooleanAnswer.fromArg(args[0]);
      User user = plugin.getApi().getUser(cs.getUniqueId());
      user.setTpaLockState(answer);
      plugin.getApi().saveUser(user);

      switch (answer) {
        case Yes:
          cs.sendMessage("§aFrom now on, you will automatically accept teleport requests.");
          break;
        case No:
          cs.sendMessage("§aFrom now on, you will automatically deny teleport requests.");
          break;
        case Ask:
          cs.sendMessage("§aYou will be asked what to do with teleportation requests from now on.");
          break;
      }

      plugin.getTeleportRequests().removeIf(x -> x.val.isTo(cs.getName()));
    }
  }

}
