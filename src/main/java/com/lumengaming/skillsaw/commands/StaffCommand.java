package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.Constants;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.utility.SharedUtility;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.util.ArrayList;

public class StaffCommand extends BungeeCommand {

    public StaffCommand(BungeeMain plugin) {
        super(plugin, "staff", null, "stafflist");
    }

    private void printHelp(IPlayer cs) {
        cs.sendMessage("§c/staff +/add <username>");
        cs.sendMessage("§c/staff -/del/remove <username>");
        cs.sendMessage("§c/staff list");
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {

        try {
            if (args[0].equals("+") || args[0].equalsIgnoreCase("add")) {
                if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.STAFF_MODIFY, true)) {
                    return;
                }
                this.plugin.getApi().getOfflineUserByNameOrDisplayName(args[1], (User u) -> {
                    if (u != null) {
                        if (!u.isInstructor()) {
                            u.setIsInstructor(true);
                            this.plugin.getDataService().saveUser(u);
                            cs.sendMessage("§a" + u.getName() + " has been added to the instructor list.");
                            if (u.p() != null && u.p().isValid()) {
                                u.p().sendMessage("§aYou've been added to the instructor list.");
                            }
                        } else {
                            cs.sendMessage("§c" + u.getName() + " is already a instructor member.");
                        }
                    } else {
                        cs.sendMessage(Constants.ERROR_P_NOT_FOUND);
                    }
                });
            } else if (args[0].equals("-") || args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("remove")) {
                if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.STAFF_MODIFY, true)) {
                    return;
                }

                this.plugin.getDataService().getOfflineUserByNameOrDisplayName(args[1], (User u) -> {
                    if (u != null) {
                        if (u.isInstructor()) {
                            u.setIsInstructor(false);
                            this.plugin.getDataService().saveUser(u);
                            cs.sendMessage("§a" + u.getName() + " has been removed from the instructor list.");
                            if (u.p() != null && u.p().isValid()) {
                                u.p().sendMessage("§cYou've been removed from the instructor list.");
                            }
                        } else {
                            cs.sendMessage("§c" + u.getName() + " is not a instructor member.");
                        }
                    } else {
                        cs.sendMessage(Constants.ERROR_P_NOT_FOUND);
                    }
                });
            } else if (args[0].equalsIgnoreCase("l") || args[0].equalsIgnoreCase("list")) {
                if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.STAFF_LIST)) {
                    return;
                }
                this.plugin.getDataService().getOfflineStaff((ArrayList<User> us) -> {
                    cs.sendMessage(Constants.C_DIV_LINE);
                    cs.sendMessage(Constants.C_DIV_TITLE_PREFIX + "Staff List");
                    cs.sendMessage(Constants.C_DIV_SUBTITLE_PREFIX + "Sorted by last login date.");
                    cs.sendMessage(Constants.C_DIV_SUBTITLE_PREFIX + "Hover over the staff to see their skills.");
                    cs.sendMessage(Constants.C_DIV_LINE);
                    boolean altColor = false;
                    us.sort((User o1, User o2) -> (int) (o1.getLastPlayed() - o2.getLastPlayed()));
                    for (User u : us) {
                        altColor = !altColor;
                        if (altColor) {
                            cs.sendMessage(CText.legacy(Constants.C_MENU_CONTENT
                                + SharedUtility.getTimePartsString(System.currentTimeMillis() - u.getLastPlayed())
                                + " - " + u.getName()));
                        } else {
                            cs.sendMessage(CText.legacy(Constants.C_MENU_CONTENT2
                                + SharedUtility.getTimePartsString(System.currentTimeMillis() - u.getLastPlayed())
                                + " - " + u.getName()));
                        }
                    }
                    cs.sendMessage(Constants.C_DIV_LINE);
                });
            } else {
                printHelp(cs);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            printHelp(cs);
        }
    }

}
