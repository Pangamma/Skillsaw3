package com.lumengaming.skillsaw.commands.chat;

import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.config.Options;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.models.Title;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.service.DataService;
import com.lumengaming.skillsaw.utility.C;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.util.ArrayList;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

/**
 * @author Taylor Love (Pangamma)
 */
public class TitleCommand extends BungeeCommand {

    private final DataService ds;

    public TitleCommand(BungeeMain aThis) {
        super(aThis, "title", null, "settitle");
        this.ds = plugin.getDataService();
    }

    private boolean isValidAction(String action) {
        switch (action.toLowerCase()) {
            case "list":
            case "l":
            case "remove":
            case "add":
                return true;
            default:
                return false;
        }
    }

    public void printHelp(IPlayer cs) {
        if (Permissions.USER_HAS_PERMISSION(cs, Permissions.CUSTOM_TITLES, false)) {
            cs.sendMessage("§c/title add <short title> <long title>");
            cs.sendMessage("§c/title remove <short/long title>");
            cs.sendMessage("§c/title username list");
            cs.sendMessage("§c/title username <short/long title>");
            cs.sendMessage("§c/title username add <short title> <long title>");
            cs.sendMessage("§c/title username remove <short/long title>");
        }
        cs.sendMessage("§c/title list");
        cs.sendMessage("§c/title [=] <title>");
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {

        if (!(cs.isPlayer())) {
            // Me finding this years later: "Well now I just don't have the TIME to do it."
            cs.sendMessage("Yeah, I am not going to deal with this. Log in and do this command from the game.");
            return;
        }

        User issuer = this.ds.getUser(cs.getUniqueId());
//                  1 = list                                 /title list
//                  1 = set it for self                     /title args[0]
//                  2 = set it for self                    /title = args[1]
//                  2 = list for other user               /title args[0] list
//                  2 = set for other user               /title args[0] args[1]
//                  2 = remove                          /title args[0] remove
//                  3 = remove title for other         /title args[usr] remove args[needle]
//                  3 = add title for self            /title add [short] [long]
//                  4 = add for other users          /title [user] add [short] [long]
        try {
            switch (args.length) {

                case 0:
                    printHelp(cs);
                    break;

                case 1:
                    if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l")) {
                        doListTitles(cs, issuer);
                    } else {
                        doSetTitle(cs, issuer, args[0]);
                    }
                    break;

                case 2:
                    if (args[0].equalsIgnoreCase("=")) {
                        doSetTitle(cs, issuer, args[1]);
                    } else if (args[1].equalsIgnoreCase("list") || args[1].equalsIgnoreCase("l")) {
                        ds.getOfflineUserByNameOrDisplayName(args[0], u -> {
                            doListTitles(cs, u);
                        });
                    } else if (args[0].equalsIgnoreCase("remove") || args[0].equals("-")) {
                        doRemoveTitle(cs, issuer, args[1]);
                    } else {
                        ds.getOfflineUserByNameOrDisplayName(args[0], u -> {
                            doSetTitle(cs, u, args[1]);
                        });
                    }
                    break;

                case 3:
                    if (args[1].equals("-") || args[1].equalsIgnoreCase("remove")) {
                        ds.getOfflineUserByNameOrDisplayName(args[0], u -> {
                            doRemoveTitle(cs, u, args[2]);
                        });
                    } else if (args[0].equals("+") || args[0].equalsIgnoreCase("add")) {
                        doAddTitle(cs, issuer, args[1], args[2]);
                    } else {
                        printHelp(cs);
                    }
                    break;

                case 4:
                    if (args[1].equals("+") || args[1].equalsIgnoreCase("add")) {
                        ds.getOfflineUserByNameOrDisplayName(args[0], u -> {
                            doAddTitle(cs, u, args[2], args[3]);
                        });
                    } else {
                        printHelp(cs);
                    }
                    break;
                default:
                    printHelp(cs);
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            printHelp(cs);
        }
    }

    private void doSetTitle(final BungeePlayer cs, final User target, String titleNeedle) {
        if (target == null) {
            cs.sendMessage(C.ERROR_P_NOT_FOUND);
            return;
        }

        boolean isSelf = target.getName().equalsIgnoreCase(cs.getName());
        boolean canSetSelf = Permissions.USER_HAS_PERMISSION(cs, Permissions.TITLE_SET_SELF, isSelf);
        boolean canSetOthers = Permissions.USER_HAS_PERMISSION(cs, Permissions.TITLE_SET_OTHERS, !isSelf);
        if (isSelf && !canSetSelf) {
            return;
        }
        if (!isSelf && !canSetOthers) {
            return;
        }

        ArrayList<Title> allTitles = target.getAllTitles();
        String needle = titleNeedle.replace("&", "§");
        Title title = Title.getMatchedTitle(needle, target.getAllTitles());

        if (title == null) {
            cs.sendMessage("§cCould not find title : " + needle + "§c.");
            return;
        }

        target.setTitle(title);
        ds.saveUser(target);

        if (isSelf) {
            cs.sendMessage("§aSuccessfully set your title to " + title.getLongTitle() + "§a.");
        } else {
            cs.sendMessage("§aSuccessfully set " + target.getDisplayName() + "'s title to " + title.getLongTitle() + "§a.");
            target.sendMessage("§a" + cs.getDisplayName() + "§a set your title to " + title.getLongTitle() + "§a.");
        }
    }

    private void doListTitles(final BungeePlayer cs, final User target) {
        if (target == null) {
            cs.sendMessage(C.ERROR_P_NOT_FOUND);
            return;
        }

        cs.sendMessage(C.C_DIV_LINE);
        cs.sendMessage(C.C_DIV_TITLE_PREFIX + "Titles for " + target.getName());
        cs.sendMessage(C.C_DIV_LINE);

        boolean isSelf = target.getName().equalsIgnoreCase(cs.getName());
        boolean canSetSelf = Permissions.USER_HAS_PERMISSION(cs, Permissions.TITLE_SET_SELF, false);
        boolean canSetOthers = Permissions.USER_HAS_PERMISSION(cs, Permissions.TITLE_SET_OTHERS, false);

        for (Title title : target.getAllTitles()) {
            BaseComponent[] txt = CText.legacy(C.C_MENU_CONTENT);
            BaseComponent[] sTitle = CText.legacy(title.getLongTitle());

            if ((canSetSelf && isSelf) || (canSetOthers && !isSelf)) {
                CText.applyEvent(sTitle, new HoverEvent(HoverEvent.Action.SHOW_TEXT, CText.legacy(title.getShortTitle())));
                CText.applyEvent(sTitle, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/title " + target.getName() + " " + title.getLongTitle().replace("§", "&")));
            }

            txt = CText.merge(txt, sTitle);
            cs.sendMessage(txt);
        }
        cs.sendMessage(C.C_DIV_LINE);
    }

    private void doRemoveTitle(final BungeePlayer cs, final User target, String titleNeedle) {
        if (target == null) {
            cs.sendMessage(C.ERROR_P_NOT_FOUND);
            return;
        }

        if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.TITLE_EDIT_ANY, true)) {
            return;
        }

        boolean isSelf = target.getName().equalsIgnoreCase(cs.getName());
        String needle = titleNeedle.replace("&", "§");
        Title title = Title.getMatchedTitle(needle, target.getAllTitles());
        if (title == null) {
            if (isSelf) {
                cs.sendMessage("§cCould not find title : " + needle + "§c.");
            } else {
                cs.sendMessage("§cYou do not have the '" + needle + "' title.");
            }
            return;
        }

        target.removeTitle(title);
        ds.saveUser(target);

        if (isSelf) {
            cs.sendMessage("§aSuccessfully removed the '" + title.getLongTitle() + "'§a title.");
        } else {
            cs.sendMessage("§aSuccessfully removed the '" + title.getLongTitle() + "'§a title from " + target.getDisplayName() + "§a.");
            target.sendMessage("§aThe '" + title.getLongTitle() + "'§a title has been removed from your list.");
        }
    }

    private void doAddTitle(BungeePlayer cs, User target, String shortTitle, String longTitle) {
        if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.TITLE_EDIT_ANY, true)) {
            return;
        }

        if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.TITLE_EDIT_ANY, true)) {
            return;
        }

        boolean isSelf = target.getName().equalsIgnoreCase(cs.getName());

        String sTitle = shortTitle.replace('&', '§');
        String lTitle = longTitle.replace('&', '§');
        Title nTitle = new Title(sTitle, lTitle);
        if (target.hasExactTitle(nTitle)) {
            if (isSelf) {
                cs.sendMessage("§cYou already have the '" + lTitle + "'§c title.");
            } else {
                cs.sendMessage("§cThat player already have the '" + lTitle + "'§c title.");
            }
            return;
        }

        if (ChatColor.stripColor(sTitle).length() > Options.Get().ChatSystem.MaxShortTitleLength) {
            cs.sendMessage("§cYour short title must not excede 8 visible characters.");
            return;
        }

        target.addTitle(nTitle);
        ds.saveUser(target);

        if (isSelf) {
            cs.sendMessage("§aSuccessfully added the '" + lTitle + "'§a title.");
        } else {
            cs.sendMessage("§aSuccessfully added the '" + lTitle + "'§a title to " + target.getDisplayName() + "§a's title list.");
            target.sendMessage("§aYou have acquire the '" + lTitle + "§a' title!");
        }
    }
}
