package com.lumengaming.skillsaw.commands.chat;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.Options;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.service.DataService;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.Constants;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.util.concurrent.CopyOnWriteArraySet;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

public class IgnoreCommand extends BungeeCommand {

    private final int maxIgnore;

    public IgnoreCommand(BungeeMain plugin) {
        super(plugin, "ignore", null);
        this.maxIgnore = Options.Get().ChatSystem.MaxIgnoreListSize;
    }

    private void printHelp(IPlayer p) {
        p.sendMessage(CText.hoverText("§c/ignore + <player>", "Add player name to\nyour ignore list."));
        p.sendMessage(CText.hoverText("§c/ignore - <player>", "Remove player name from\nyour ignore list."));
        p.sendMessage(CText.hoverText("§c/ignore *", "Ignore everyone."));
        p.sendMessage(CText.hoverText("§c/ignore !*", "Clear your ignore list.\n(Ignore no one)"));
        p.sendMessage(CText.hoverText("§c/ignore ?", "Show the people in\nyour ignore list."));
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {

        if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.IGNORE, true)) {
            return;
        }

        if (!cs.isPlayer()) {
            cs.sendMessage(Constants.ERROR_PLAYERS_ONLY);
            return;
        }

        try {
            DataService system = plugin.getDataService();
            User cp = system.getUser(cs.getUniqueId());
            if (cp == null) {
                cs.sendMessage(Constants.ERROR_TRY_AGAIN_LATER_COMMAND);
                return;
            }

            if ("+".equals(args[0])) {
                int size = cp.getIgnored().size();
                if (size >= this.maxIgnore) {
                    cs.sendMessage("§cMax of " + this.maxIgnore + " ignored players at a time unless you have the '" + Permissions.IGNORE_INF + "' permission node. Consider removing some names from your ignore list.");
                    return;
                } else {
                    if (args[1].equals("*")) {
                        cp.getIgnored().add("*");
                        cs.sendMessage("§aAdded everyone (*) to your ignore list.");
                        plugin.getDataService().saveUser(cp);
                    } else {
                        plugin.getApi().getOfflineUserByNameOrDisplayName(args[1], (target) -> {
                            if (target != null) {
                                cp.getIgnored().add(target.getName().toLowerCase());
                                cs.sendMessage("§aAdded " + target.getName().toLowerCase() + " to your ignore list.");
                                plugin.getDataService().saveUser(cp);
                            } else {
                                cp.getIgnored().add(args[1].toLowerCase());
                                cs.sendMessage("§aAdded " + args[1].toLowerCase() + " to your ignore list.");
                                plugin.getDataService().saveUser(cp);
                            }
                        });
                    }
                }
            } else if ("-".equals(args[0])) {
                CopyOnWriteArraySet<String> ignored = cp.getIgnored();
                String toRemove = null;
                for (String nm : ignored) {
                    if (nm.equalsIgnoreCase(args[1])) {
                        toRemove = nm;
                        break;
                    }
                }
                if (toRemove == null) {
                    for (String nm : ignored) {
                        if (nm.startsWith(args[1].toLowerCase())) {
                            if (toRemove == null || nm.length() < toRemove.length()) {
                                toRemove = nm;
                            }
                        }
                    }
                }
                if (toRemove != null) {
                    cp.getIgnored().remove(toRemove.toLowerCase());
                    cs.sendMessage("§aRemoved " + toRemove.toLowerCase() + " from your ignore list.");
                    plugin.getDataService().saveUser(cp);
                } else {
                    cp.sendMessage("§cYou're not ignoring '" + args[1] + "' right now.");
                }
            } else if ("*".equals(args[0])) {
                cp.getIgnored().add("*");
                cs.sendMessage("§aIgnoring all players now.");
                plugin.getDataService().saveUser(cp);
            } else if ("!*".equals(args[0])) {
                cp.getIgnored().clear();
                cs.sendMessage("§aNo longer ignoring anyone.");
                plugin.getDataService().saveUser(cp);
            } else if ("?".equals(args[0])) {
                cs.sendMessage(Constants.C_DIV_LINE);
                cs.sendMessage(Constants.C_DIV_TITLE_PREFIX + " Ignore List");
                cs.sendMessage(Constants.C_DIV_LINE);
                for (String name : cp.getIgnored()) {
                    BaseComponent[] txt = CText.legacy(Constants.C_MENU_CONTENT + name);
                    CText.applyEvent(txt, new HoverEvent(HoverEvent.Action.SHOW_TEXT, CText.legacy("Click to remove.")));
                    CText.applyEvent(txt, new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ignore - " + name));
                    cs.sendMessage(txt);
                }
                cs.sendMessage(Constants.C_DIV_LINE);
            } else {
                printHelp(cs);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            printHelp(cs);
        }
    }

}
