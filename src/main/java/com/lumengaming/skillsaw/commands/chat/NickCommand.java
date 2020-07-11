package com.lumengaming.skillsaw.commands.chat;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.service.DataService;
import com.lumengaming.skillsaw.utility.C;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.utility.SharedUtility;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.util.HashSet;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

/**
 * TODO: make the level 4 thing configurable.
 *
 * @author Taylor Love (Pangamma)
 */
public class NickCommand extends BungeeCommand {

    
    private final DataService dh;

    public NickCommand(BungeeMain p_plugin) {
        super(p_plugin, "nickname", null, "nick");
        this.dh = plugin.getDataService();
    }
    
    
  @Override
  public Iterable<String> onTabCompleteBeforeFiltering(CommandSender arg0, String[] args) {
    HashSet<String> set = new HashSet<>();
    set.add("<nickname>");
    return set;
  }


    @Override
    public void execute(BungeePlayer cs, final String[] args) {
        
        boolean canNickSelf = Permissions.USER_HAS_PERMISSION(cs, Permissions.NICK_SELF, false);
        boolean canNickOthers = Permissions.USER_HAS_PERMISSION(cs, Permissions.NICK_OTHERS, false);
        boolean canNickColors = Permissions.USER_HAS_PERMISSION(cs, Permissions.NICK_STYLE_COLORS, false);
        boolean canNickBlack = Permissions.USER_HAS_PERMISSION(cs, Permissions.NICK_STYLE_COLOR_BLACK, false);
        boolean canNickFormat = Permissions.USER_HAS_PERMISSION(cs, Permissions.NICK_STYLE_FORMATTING, false);
        boolean canNickMagic = Permissions.USER_HAS_PERMISSION(cs, Permissions.NICK_STYLE_FORMATTING_MAGIC, false);
        boolean canNickSpecialChars = Permissions.USER_HAS_PERMISSION(cs, Permissions.NICK_STYLE_SPECIAL_CHARS, false);

        User issuer = dh.getUser(cs.getUniqueId());
        if (issuer == null) {
            cs.sendMessage(C.ERROR_TRY_AGAIN_LATER_COMMAND);
            return;
        }

        int issuerLevel = issuer.getRepLevel();
        if (!canNickSelf && issuerLevel >= 4) {
            canNickSelf = true;
        }

        if (!canNickSelf) {
            cs.sendMessage("§cYour rep level must be at least level 4, or you need the " + Permissions.NICK_SELF + " permission node.");
            return;
        }

        try {
            String nick = "";
            if (args.length == 1) {
                nick = args[0];
                part2(issuer, issuer, nick, canNickFormat, canNickColors, canNickBlack, canNickSpecialChars, canNickMagic);
            } else if (args.length == 2) {
                dh.getOfflineUserByNameOrDisplayName(args[0], (target) -> {
                    if (target == null) {
                        cs.sendMessage(C.ERROR_P_NOT_FOUND);
                        return;
                    }

                    if (!canNickOthers && !cs.getName().equalsIgnoreCase(target.getName())) {
                        cs.sendMessage(Permissions.TELL_USER_PERMISSION_THEY_LACK(Permissions.NICK_OTHERS));
                        return;
                    }

                    part2(issuer, target, args[1], canNickFormat, canNickColors, canNickBlack, canNickSpecialChars, canNickMagic);
                });
            } else {
                printHelp(cs);
                return;
            }

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException nfe) {
            printHelp(cs);
        }
    }

    private void part2(User issuer, User target, String nick, boolean canNickFormat, boolean canNickColors, boolean canNickBlack, boolean canNickSpecialChars, boolean canUseMagic) {
        nick = nick.replace("&", "§");
        nick = SharedUtility.removeColorCodes(nick, canNickFormat, canNickColors, canNickBlack, canUseMagic);
        nick = nick.replace("&", "");
        // Remove any weirdo characters.
        if (!canNickSpecialChars) {
            nick = nick.replaceAll("[^a-zA-Z0-9_&§\\-]", "");
        }

        if (ChatColor.stripColor(nick).length() > 16) {
            issuer.sendMessage("§cNickname must not exceed 16 visible characters.");
            return;
        }

        target.setDisplayName(nick);
        if (target.p() != null) {
            target.p().setDisplayName(nick);
            if (!issuer.getName().equalsIgnoreCase(target.p().getName())) {
                issuer.sendMessage("§aTarget's nickname has been set to " + nick + "§a.");
            }
            target.p().sendMessage("§aYour nickname has been set to " + nick + "§a.");
        }
        plugin.getDataService().saveUser(target);
    }

    private void printHelp(IPlayer cs) {
        cs.sendMessage("§c/nick [target name] <nickname>");
        cs.sendMessage("§cYou can give yourself a nickname. Example : My name is §fPangamma§c,"
            + " but I want it to be §6RockLobster§c. I would type \"§e/nick &6RockLobster§c\".");
    }

}
