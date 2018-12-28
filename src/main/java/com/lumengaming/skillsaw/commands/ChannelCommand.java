package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.service.DataService;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.Constants;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author Taylor
 */
public class ChannelCommand extends BungeeCommand {

    public ChannelCommand(BungeeMain plugin) {
        super(plugin, "channel", null, "ch");
    }

    /**
     * Just as it sounds. Returns array with one fewer argument. Missing the first arg. *
     */
    private String[] stripArg(String[] origArray) {
        String[] nArray = new String[origArray.length - 1];
        if (origArray.length > 0) {
            for (int i = 1; i < origArray.length; i++) {
                nArray[i - 1] = origArray[i];
            }
        }
        return nArray;
    }

    private void printHelp(IPlayer cs) {
        cs.sendMessage(("§c/ch"));
        cs.sendMessage(("§c/ch:<channel> <message>"));
        cs.sendMessage(("§c/ch + <channel>"));
        cs.sendMessage(("§c/ch - <channel>"));
        cs.sendMessage(("§c/ch *"));
        cs.sendMessage(("§c/ch !*"));
        cs.sendMessage(("§c/ch info [channel]"));
        cs.sendMessage(("§c/ch p [player]"));
        cs.sendMessage(("§c/ch list"));
        cs.sendMessage(("§c/ch <channel>"));
    }

    private void runChannelInfo(IPlayer cs, String[] args) {

        if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.CHANNEL_INFO)) {
            return;
        }

        DataService system = plugin.getDataService();
        String ch = args.length > 0 ? args[0] : null;
        if (cs.isPlayer()) {
            if (ch == null) {
                User cp = system.getUser(cs.getUniqueId());
                if (cp != null) {
                    ch = cp.getSpeakingChannel();
                }
            }
            if (ch == null) {
                cs.sendMessage(Constants.ERROR_REPORT_THIS_TO_PANGAMMA(1));
                return;
            }
            cs.sendMessage(Constants.C_DIV_LINE);
            cs.sendMessage(Constants.C_DIV_TITLE_PREFIX + "Channl Info");
            cs.sendMessage(Constants.C_DIV_LINE);
            ArrayList<User> cps = system.getOnlineUsersReadOnly();
            int numListening = 0;
            int numSpeaking = 0;

            for (User cp : cps) {
                if (cp.isSpeakingOnChannel(ch)) {
                    numSpeaking++;
                    BaseComponent[] txt = CText.legacy(Constants.C_MENU_CONTENT + cp.getName());
                    cs.sendMessage(txt);
                }
                if (cp.isListeningOnChannel(ch)) {
                    numListening++;
                }
            }

            cs.sendMessage(Constants.C_DIV_LINE);
            cs.sendMessage(Constants.C_MENU_CONTENT + "# Listening: " + numListening);
            cs.sendMessage(Constants.C_MENU_CONTENT + "# Speaking: " + numSpeaking);
            cs.sendMessage(Constants.C_DIV_LINE);

        } else {

            if (ch == null) {
                cs.sendMessage("Could not determine the channel you want info for.");
                return;
            }

            cs.sendMessage(Constants.C_DIV_LINE_NC);
            cs.sendMessage(Constants.C_DIV_TITLE_PREFIX_NC + "Channl Info");
            cs.sendMessage(Constants.C_DIV_LINE_NC);
            Collection<User> cps = system.getOnlineUsersReadOnly();
            int numListening = 0;
            int numSpeaking = 0;

            for (User cp : cps) {
                if (cp.isSpeakingOnChannel(ch)) {
                    numSpeaking++;
                    cs.sendMessage(Constants.C_MENU_CONTENT_NC + cp.getName());
                }
                if (cp.isListeningOnChannel(ch)) {
                    numListening++;
                }
            }
            cs.sendMessage(Constants.C_DIV_LINE_NC);
            cs.sendMessage(Constants.C_MENU_CONTENT_NC + "# Listening: " + numListening);
            cs.sendMessage(Constants.C_MENU_CONTENT_NC + "# Speaking: " + numSpeaking);
            cs.sendMessage(Constants.C_DIV_LINE_NC);
        }
    }

    private void runChannelPlayer(final IPlayer cs, String[] args) {

        if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.CHANNEL_INFO)) {
            return;
        }

        DataService system = plugin.getDataService();

        String pName = args.length > 0 ? args[0] : cs.getName();

        system.getOfflineUserByNameOrDisplayName(pName, (cp) -> {
            if (cp == null) {
                cs.sendMessage(Constants.ERROR_P_NOT_FOUND);
                return;
            }

            boolean showPrivate = cp.getName().equals(cs.getName()) || Permissions.USER_HAS_PERMISSION(cs, Permissions.CHANNEL_LIST_PRIVATE, false);

            cs.sendMessage(Constants.C_DIV_LINE);
            cs.sendMessage(Constants.C_MENU_CONTENT + "Chat info for: §a" + cp.getName() + ".");
            for (String ch : cp.getStickyChannels()) {
                if (showPrivate || !ch.startsWith("_")) {
                    cs.sendMessage(Constants.C_MENU_CONTENT + "(L): " + ch);
                }
            }
            if (showPrivate || !cp.getSpeakingChannel().startsWith("_")) {
                cs.sendMessage(Constants.C_MENU_CONTENT + "§a(S): " + cp.getSpeakingChannel());
            } else {
                cs.sendMessage(Constants.C_MENU_CONTENT + "§c(S): *Private Channel*");
            }
            cs.sendMessage(Constants.C_DIV_LINE);

        });
    }

    private void runChannelList(IPlayer cs, String[] args) {
        if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.CHANNEL_LIST)) {
            return;
        }
        ArrayList<User> users = plugin.getDataService().getOnlineUsersReadOnly();
        HashMap<String, Integer> speakers = new HashMap<>();
        for (User cp : users) {
            String ch = cp.getSpeakingChannel();
            if (speakers.containsKey(ch)) {
                Integer get = speakers.get(ch);
                speakers.put(ch, get + 1);
            } else {
                speakers.put(ch, 1);
            }
        }

        cs.sendMessage(Constants.C_DIV_LINE);
        for (String ch : speakers.keySet()) {
            if (!ch.startsWith("_")) {
                cs.sendMessage("§2=§7 " + ch + " (" + speakers.get(ch) + " people)");
            } else if (Permissions.USER_HAS_PERMISSION(cs, Permissions.CHANNEL_LIST_PRIVATE, false)) {
                cs.sendMessage("§2=§c " + ch + " (" + speakers.get(ch) + " people)");
            }
        }
        cs.sendMessage(Constants.C_DIV_LINE);

    }

    private void runChannelPlus(IPlayer cs, String[] args) {

        if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.CHANNEL_STICKIES)) {
            return;
        }

        if (cs.isPlayer()) {
            User cp = plugin.getDataService().getUser(cs.getUniqueId());
            if (cp != null) {
                if (cp.getStickyChannels().size() < 10 || Permissions.USER_HAS_PERMISSION(cs, Permissions.CHANNEL_STICKIES_INFINITE)) {
                    String ch = args[0].toLowerCase();
                    if (!isValidChannelName(ch)) {
                        cs.sendMessage(("§cInvalid symbols in the channel name. Pick a different channel."));
                        return;
                    }
                    cp.addStickyChannel(ch);
                    cs.sendMessage("§aAdded new \"sticky\" channel §2" + ChatColor.stripColor(ch));
                    plugin.getDataService().saveUser(cp);
                } else {
                    cs.sendMessage("§cMaximum number of sticky channels has been reached. You cannot add any more!");
                }
            } else {
                cs.sendMessage(Constants.ERROR_TRY_AGAIN_LATER_COMMAND);
            }
        } else {
            cs.sendMessage(Constants.ERROR_PLAYERS_ONLY);
        }

    }

    private void runChannelMinus(IPlayer cs, String[] args) {
        if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.CHANNEL_STICKIES)) {
            return;
        }
        if (cs.isPlayer()) {
            String ch = args[0].toLowerCase();
            User cp = plugin.getDataService().getUser(cs.getUniqueId());
            if (cp != null) {
                String toRemove = args[0].toLowerCase();
                if (cp.getStickyChannels().remove(toRemove)) {
                    cs.sendMessage("§aRemoved \"sticky\" channel §2" + ChatColor.stripColor(toRemove));
                    plugin.getDataService().saveUser(cp);
                } else {
                    cs.sendMessage("§cYou do not have the \"sticky\" channel §4" + ChatColor.stripColor(toRemove));
                }
            } else {
                cs.sendMessage(Constants.ERROR_TRY_AGAIN_LATER_COMMAND);
            }
        } else {
            cs.sendMessage(Constants.ERROR_PLAYERS_ONLY);
        }
    }

    private void runChannelSet(IPlayer cs, String[] args) {
        if (cs.isPlayer()) {
            String ch = args[0].toLowerCase();

            if (!isValidChannelName(ch)) {
                cs.sendMessage(("§cInvalid symbols in the channel name. Pick a different channel."));
                return;
            }

            User cp = plugin.getDataService().getUser(cs.getUniqueId());

            if (cp != null) {
                cp.setSpeakingChannel(ch);
                cp.sendMessage(("§aSet the chat channel to §2" + ch));
                plugin.getDataService().saveUser(cp);
            } else {
                cs.sendMessage(Constants.ERROR_TRY_AGAIN_LATER_COMMAND);
            }
        } else {
            cs.sendMessage(Constants.ERROR_PLAYERS_ONLY);
        }
    }

    /**
     * Channel name must only contain chars between decimal value 32 and 127. (non inclusive). Channel name must also
     * have at least one char.
     *
     * @param chName
     * @return
     */
    private boolean isValidChannelName(String chName) {
        if (chName == null || chName.isEmpty()) {
            return false;
        }

        for (int c : chName.toCharArray()) {
            if (c < 33 || c > 126) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {

        try {

            // Otherwise see if they used the in-command thing with arg[0]
            switch (args[0].toLowerCase()) {
                case "+":
                case "add":
                    runChannelPlus(cs, stripArg(args));
                    break;
                case "*":
                    runChannelPlus(cs, new String[]{"*"});
                    break;
                case "!*":
                    runChannelMinus(cs, new String[]{"*"});
                    break;
                case "-":
                case "remove":
                    runChannelMinus(cs, stripArg(args));
                    break;
                case "info":
                case "i":
                case "?":
                    runChannelInfo(cs, stripArg(args));
                    break;
                case "pinfo":
                case "p":
                case "?p":
                    runChannelPlayer(cs, stripArg(args));
                    break;
                case "list":
                case "l":
                    runChannelList(cs, args);
                    break;
                default:
                    runChannelSet(cs, args);
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
            printHelp(cs);
        }
    }
}
