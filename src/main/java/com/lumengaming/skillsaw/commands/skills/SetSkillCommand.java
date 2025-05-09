package com.lumengaming.skillsaw.commands.skills;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.config.Options;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.commands.HelpModule;
import com.lumengaming.skillsaw.models.SkillType;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.C;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.util.ArrayList;
import java.util.HashSet;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * @author Taylor Love (Pangamma)
 */
public class SetSkillCommand extends BungeeCommand {

  public SetSkillCommand(BungeeMain plug) {
    super(plug, "setskill", null, "sset", "sks", "skillset");
    super.addSyntax(Permissions.INSTRUCT, false, false, "/skillset <user> <r/o/p/a/t/v> <1-10>", "Set someone's skill\nlevel from 1 to 10.\nthe letters match with\nthe different skill tiers.");
  }

  @Override
  public Iterable<String> onTabCompleteBeforeFiltering(CommandSender cs, String[] args) {
    HashSet<String> set = new HashSet<>();
    if (args.length == 1) {
      set.addAll(this.getOnlinePlayerNames());
    } else if (args.length == 2) {
      set.add("r");
      set.add("o");
      set.add("p");
      set.add("a");
      set.add("t");
      set.add("v");
    } else if (args.length == 3) {
      set.add("<level>");
    }
    return set;
  }

  @Override
  public void execute(BungeePlayer cs, String[] args) {
    if (args.length == 0) {
      plugin.printHelp(cs);
    } else if (args.length == 1) {
      //<editor-fold defaultstate="collapsed" desc="show data about...">
      cs.sendMessage(C.MSG_PROCESSING);
      plugin.getDataService().getOfflineUserByNameOrDisplayName(args[0], (User u) -> {
        if (u != null) {
          u.showStatisticsTo(cs);
        } else {
          cs.sendMessage(C.ERROR_P_NOT_FOUND);
        }
      });
      //</editor-fold> FG 

    } else {
      if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.INSTRUCT)) {
        return;
      }

      if (!(cs.isPlayer())) {
        cs.sendMessage("Only players can set another player's skill level.");
        return;
      }

      try {
        ArrayList<SkillType> sts = Options.Get().getSkillTypes();
        SkillType st = null;
        for (SkillType st2 : sts) {
          if (st2.getListName().toLowerCase().startsWith(args[1].toLowerCase())) {
            st = st2;
            break;
          }
        }
        if (st == null) {
          cs.sendMessage("§cNo skilltype found that matched your request. Try something else.");
          return;
        }

        final SkillType finalSt = st;
        final int nLevel = Integer.parseInt(args[2]);
        if (nLevel < st.getMinLevel() || nLevel > st.getMaxLevel()) {
          cs.sendMessage("§cYou must specify a level between " + st.getMinLevel() + " and " + st.getMaxLevel() + ".");
          return;
        }

        User issuer = plugin.getDataService().getUser(cs.getUniqueId());
        if (issuer == null) {
          cs.sendMessage(C.ERROR_TRY_AGAIN_LATER_COMMAND);
          return;
        }

//                if (nLevel > Math.max(3, issuer.getSkill(st)) && issuer.getSkill(st) < st.getMinInstructLevel() && !Permissions.USER_HAS_PERMISSION(cs, Permissions.INSTRUCT_OVERRIDE)) {
//                    cs.sendMessage("§cYou cannot promote people in §4" + st.getListName() + "§c until your skill level in that "
//                        + "category is §4" + st.getMinInstructLevel() + "§c or higher.");
//                    return;
//                }
        //<editor-fold defaultstate="collapsed" desc="user">
        cs.sendMessage(C.MSG_PROCESSING);

        plugin.getDataService().getOfflineUserByNameOrDisplayName(args[0], (User target) -> {

          if (target == null) {
            cs.sendMessage(C.ERROR_P_NOT_FOUND);
            return;
          }

          if (target.getName().equalsIgnoreCase(cs.getName())) {
            cs.sendMessage("§cYou cannot change your own level.");
            return;
          }
          int oLevel = target.getSkill(finalSt);

          if (oLevel != nLevel) {
            target.setSkill(finalSt, nLevel);
            plugin.getDataService().saveUser(target);
            plugin.getSender().getPlayerLocation((ProxiedPlayer) issuer.getRawPlayer(), (loc) -> {
              plugin.getDataService().logPromotion(issuer, target, finalSt, oLevel, nLevel, loc);
            });
            playEffectsIfLevelChange(target.p(), oLevel, nLevel, finalSt.getListName());
          }
          cs.sendMessage("§aSet " + target.getName() + "'s §2" + finalSt.getListName() + "§a skill to level " + nLevel + ".");

        });
        //</editor-fold>

      } catch (ArrayIndexOutOfBoundsException ex) {
        plugin.printHelp(cs);
      }
    }
  }

  /**
   * player if valid, original skill level, and new level. *
   */
  private void playEffectsIfLevelChange(IPlayer p, int oSkill, int level, String skillName) {
    if (p == null) {
      return;
    }
    if (oSkill < level) {
      if (p != null && p.isValid() && p.isPlayer()) {
        plugin.getSender().doLevelUpEffect((ProxiedPlayer) p.getRaw(),
                "§7" + skillName + " tier increased",
                "§aYour §2" + skillName + "§a skill level has increased!",
                b -> {
                });
      }
    } else if (oSkill > level) {
      if (p != null && p.isValid() && p.isPlayer()) {
        plugin.getSender().doLevelDownEffect((ProxiedPlayer) p.getRaw(),
                "§7" + skillName + " tier decreased",
                "§cYour §4" + skillName + "§c skill level has decreased.",
                b -> {
                });
      }
    }
  }

}
