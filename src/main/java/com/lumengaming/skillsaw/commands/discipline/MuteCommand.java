package com.lumengaming.skillsaw.commands.discipline;

import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.models.MutedPlayer;
import com.lumengaming.skillsaw.service.DataService;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.util.HashSet;
import net.md_5.bungee.api.CommandSender;

public class MuteCommand extends BungeeCommand {

  public MuteCommand(BungeeMain plugin) {
    super(plugin, "mute", null);
  }

  public static void printHelp(IPlayer cs) {
    cs.sendMessage(CText.hoverText("§c/softmute <player> [# seconds]", "Soft mute.\nThe player will be the only\none seeing their messages."));
    cs.sendMessage(CText.hoverText("§c/smute <player> [# seconds]", "Soft mute.\nThe player will be the only\none seeing their messages."));
    cs.sendMessage(CText.hoverText("§c/mute <player>", "Mute for 5 minutes."));
    cs.sendMessage(CText.hoverText("§c/mute <player> -1", "Mute until next server restart.\nSomething something\nsomething"));
    cs.sendMessage(CText.hoverText("§c/unmute <player>", "Unmute the player."));
    cs.sendMessage(CText.hoverText("§c/unmute *", "Unmute all players."));
    cs.sendMessage(CText.hoverText("§c/mutelist", "List muted players\n/muted is an alias"));
  }

  @Override
  public Iterable<String> onTabCompleteBeforeFiltering(CommandSender cs, String[] args) {
    HashSet<String> set = new HashSet<>();
    if (args.length == 1) {
      set.add("*");
      set.addAll(this.getOnlinePlayerNames());
    } else if (args.length == 2) {
      set.add("-1");
      set.add("<# seconds>");
    }
    return set;
  }

  @Override
  public void execute(BungeePlayer cs, String[] args) {

    if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.MUTE)) {
      return;
    }

    DataService system = plugin.getApi();

    try {
      if (args.length < 1) {
        printHelp(cs);
      }

      String name = args[0];
      system.getOfflineUserByNameOrDisplayName(name, (u) -> {
        if (u == null) {
          cs.sendMessage("§cCould not find a user by that name.");
          return;
        }

        if (system.isMuted(u.getUniqueId())) {
          cs.sendMessage("§c" + u.getName() + " is already muted.");
          return;
        }

        long seconds = args.length == 2 ? Long.parseLong(args[1]) : 300;
        MutedPlayer mp = new MutedPlayer(u.getUniqueId(), u.getName(), true, seconds);
        system.addMutedPlayer(mp);
        if (u.p() != null) {
          u.p().sendMessage("§cYou have been muted for " + mp.getTotalMuteTimeStr() + ".");
        }
        cs.sendMessage("§a" + u.getName() + " was muted for " + mp.getTotalMuteTimeStr() + ".");
      });
    } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
      printHelp(cs);
    }
  }

}
