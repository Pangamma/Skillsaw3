package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.config.Options;
import com.lumengaming.skillsaw.models.SkillType;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.C;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.utility.SharedUtility;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.util.ArrayList;
import java.util.HashSet;
import net.md_5.bungee.api.CommandSender;

public class InstructorCommand extends BungeeCommand {

  public InstructorCommand(BungeeMain plugin) {
    super(plugin, "instructor", null, "instructors", "instr");
  }

  @Override
  public Iterable<String> onTabCompleteBeforeFiltering(CommandSender cs, String[] args) {
    HashSet<String> set = new HashSet<>();

    switch (args.length) {
      case 1:
        set.add("+");
        set.add("add");

        set.add("-");
        set.add("del");
        set.add("remove");

        set.add("L");
        set.add("list");
        break;
      case 2:
        if (!args[0].equalsIgnoreCase("L") && !args[0].equalsIgnoreCase("list")) {
          set.addAll(this.getOnlinePlayerNames());
        }else{
          set.add("r");
          set.add("o");
          set.add("p");
          set.add("a");
          set.add("t");
          set.add("v");
        }
        break;
      default:
        break;
    }

    return set;
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
            cs.sendMessage(C.ERROR_P_NOT_FOUND);
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
            cs.sendMessage(C.ERROR_P_NOT_FOUND);
          }
        });
      } else if (args[0].equalsIgnoreCase("l") || args[0].equalsIgnoreCase("list")) {
        if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.INSTRUCTORS_LIST)) {
          return;
        }
        this.plugin.getDataService().getOfflineInstructors((ArrayList<User> us) -> {
          cs.sendMessage(C.C_DIV_LINE);
          cs.sendMessage(C.C_DIV_TITLE_PREFIX + "Instructors List");
          cs.sendMessage(C.C_DIV_SUBTITLE_PREFIX + "Sorted by last login date.");
          cs.sendMessage(C.C_DIV_SUBTITLE_PREFIX + "Hover over the instructors to see their skills.");
          cs.sendMessage(C.C_DIV_LINE);
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
                hoverText += "\n" + (C.C_MENU_CONTENT + "§a" + st.getListName() + " Tier = §2" + u.getSkill(st));
              } else {
                hoverText += "\n" + (C.C_MENU_CONTENT + st.getListName() + " Tier = §e" + u.getSkill(st));
              }
            }

            altColor = !altColor;
            if (altColor) {
              cs.sendMessage(CText.hoverText(C.C_MENU_CONTENT
                      + SharedUtility.getTimePartsString(System.currentTimeMillis() - u.getLastPlayed())
                      + " - " + u.getName(), hoverText));
            } else {
              cs.sendMessage(CText.hoverText(C.C_MENU_CONTENT2
                      + SharedUtility.getTimePartsString(System.currentTimeMillis() - u.getLastPlayed())
                      + " - " + u.getName(), hoverText));
            }
          }
          cs.sendMessage(C.C_DIV_LINE);
        });
      } else {
        printHelp(cs);
      }
    } catch (ArrayIndexOutOfBoundsException ex) {
      printHelp(cs);
    }
  }

}
