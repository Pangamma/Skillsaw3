package com.lumengaming.skillsaw.commands.discipline;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.models.MutedPlayer;
import com.lumengaming.skillsaw.service.DataService;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import java.util.HashSet;
import net.md_5.bungee.api.CommandSender;

public class UnmuteCommand extends BungeeCommand {

    

    public UnmuteCommand(BungeeMain plugin) {
        super(plugin, "unmute", null);
    super.addSyntax(Permissions.MUTE, true, false, "/unmute <player>", "Unmute the player.");
    super.addSyntax(Permissions.MUTE, true, false, "/unmute *", "Unmute all players.");
    }

  @Override
  public Iterable<String> onTabCompleteBeforeFiltering(CommandSender arg0, String[] args) {
    HashSet<String> set = new HashSet<>();
    set.add("*");
    set.addAll(this.getOnlinePlayerNames());
    return set;
  }

    @Override
    public void execute(BungeePlayer cs, String[] args) {
        
        if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.MUTE)) {
            return;
        }

        DataService system = plugin.getApi();

        if (args.length < 1) {
            MuteCommand.printHelp(cs);
            return;
        }
        
        if (args[0].equals("*")){
            system.clearMutedPlayers();
            cs.sendMessage("§aUnmuted all players.");
            return;
        }
        
        String name = args[0];
        system.getOfflineUserByNameOrDisplayName(name, (u) -> {
            if (u == null) {
                cs.sendMessage("§cCould not find a user by that name.");
                return;
            }

            MutedPlayer mp = system.removeMutedPlayer(u.getUniqueId());

            if (mp == null) {
                cs.sendMessage("§cThat player isn't muted.");
            } else {
                cs.sendMessage("§aUnmuted '" + u.getName() + "'.");

                if (u.p() != null) {
                    if (!mp.isSoftMute()) {
                        u.sendMessage("§aUnmuted.");
                    } else {
                        u.sendMessage("§7You can talk again. :)");
                    }
                }
            }
        });
    }
}
