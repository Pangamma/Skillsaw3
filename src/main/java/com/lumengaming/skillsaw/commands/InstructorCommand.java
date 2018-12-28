package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.Options;
import com.lumengaming.skillsaw.models.SkillType;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.Constants;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.utility.SharedUtility;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.util.ArrayList;

public class InstructorCommand extends BungeeCommand {

    public InstructorCommand(BungeeMain plugin) {
        super(plugin, "instructor", null, "instructors", "instr");
    }

    private void printHelp(IPlayer cs) {
        cs.sendMessage("§c/instr +/add <username>");
        cs.sendMessage("§c/instr -/del/remove <username>");
        cs.sendMessage("§c/instr list [skill]");
    }

    @Override
    public void execute(BungeePlayer cs, String[] args) {

        try {
            if (args[0].equals("+") || args[0].equalsIgnoreCase("add")) {
                if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.INSTRUCTORS_MODIFY, true)) {
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
                if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.INSTRUCTORS_MODIFY, true)) {
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
                if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.INSTRUCTORS_LIST)) {
                    return;
                }
                this.plugin.getDataService().getOfflineInstructors((ArrayList<User> us) -> {
                    cs.sendMessage(Constants.C_DIV_LINE);
                    cs.sendMessage(Constants.C_DIV_TITLE_PREFIX + "Instructors List");
                    cs.sendMessage(Constants.C_DIV_SUBTITLE_PREFIX + "Sorted by last login date.");
                    cs.sendMessage(Constants.C_DIV_SUBTITLE_PREFIX + "Hover over the instructors to see their skills.");
                    cs.sendMessage(Constants.C_DIV_LINE);
                    boolean altColor = false;
                    ArrayList<SkillType> skillTypes = Options.Get().getSkillTypes();

                    //<editor-fold defaultstate="collapsed" desc="Filter the instructors if filter is used">
                    if (args.length > 1) {
                        SkillType selectedSkillType = null;
                        String needle = args[1];
                        for (SkillType st : skillTypes) {
                            if (st.getListName().toLowerCase().startsWith(args[1].toLowerCase())) {
                                selectedSkillType = st;
                            }
                        }

                        if (selectedSkillType != null) {
                            ArrayList<User> tmp = new ArrayList<>();
                            for (User u : us) {
                                if (u.getSkill(selectedSkillType) >= selectedSkillType.getMinInstructLevel()) {
                                    tmp.add(u);
                                }
                            }
                            us = tmp;
                        }
                    }
                    //</editor-fold>

                    us.sort((User o1, User o2) -> (int) (o1.getLastPlayed() - o2.getLastPlayed()));
                    for (User u : us) {
                        String hoverText = "Skills:";
                        for (int i = 0; i < skillTypes.size(); i++) {
                            SkillType st = skillTypes.get(i);
                            if (st.getMinInstructLevel() <= u.getSkill(st)) {
                                hoverText += "\n" + (Constants.C_MENU_CONTENT + "§a" + st.getListName() + " Tier = §2" + u.getSkill(st));
                            } else {
                                hoverText += "\n" + (Constants.C_MENU_CONTENT + st.getListName() + " Tier = §e" + u.getSkill(st));
                            }
                        }

                        altColor = !altColor;
                        if (altColor) {
                            cs.sendMessage(CText.hoverText(Constants.C_MENU_CONTENT
                                + SharedUtility.getTimePartsString(System.currentTimeMillis() - u.getLastPlayed())
                                + " - " + u.getName(), hoverText));
                        } else {
                            cs.sendMessage(CText.hoverText(Constants.C_MENU_CONTENT2
                                + SharedUtility.getTimePartsString(System.currentTimeMillis() - u.getLastPlayed())
                                + " - " + u.getName(), hoverText));
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
