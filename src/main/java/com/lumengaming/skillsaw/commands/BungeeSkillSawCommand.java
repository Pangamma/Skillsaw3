package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.utility.C;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.utility.SharedUtility;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import java.util.HashSet;
import net.md_5.bungee.api.CommandSender;

/**
 * @author Taylor Love (Pangamma)
 */
public class BungeeSkillSawCommand extends BungeeCommand {

  public BungeeSkillSawCommand(BungeeMain plugin) {
    super(plugin, "skillsaw", null, "ssaw", "ss", "ss3");
  }

  @Override
  public Iterable<String> onTabCompleteBeforeFiltering(CommandSender cs, String[] args) {
    HashSet<String> set = new HashSet<>();
    if (args.length == 1) {
      set.add("perms");
      set.add("reload");
      set.add("lockout");
      set.add("help");
    }
    return set;
  }

  @Override
  public void execute(BungeePlayer cs, String[] args) {
    if (args.length != 1) {
      plugin.printHelp(cs);
    } else {
      if ((args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) && Permissions.USER_HAS_PERMISSION(cs, Permissions.ALL)) {
        plugin.onDisable();
        plugin.onEnable();
        cs.sendMessage("loading configs.");
        return;
      } else if (args[0].equalsIgnoreCase("help")) {
        HelpModule.printHelp(cs, args);
        return;
      } else if (args[0].equalsIgnoreCase("lockout")) {
        plugin.getLockoutCommand().execute(cs, SharedUtility.getSubArgs(args, 1));
        return;
      } else if (args[0].equalsIgnoreCase("perms")) {
        cs.sendMessage(C.C_DIV_LINE);
        cs.sendMessage(C.C_DIV_TITLE_PREFIX + "Permissions");
        cs.sendMessage(C.C_DIV_LINE);
        for (Permissions s : Permissions.values()) {
          if (Permissions.USER_HAS_PERMISSION(cs, s, false)) {
            cs.sendMessage(CText.hoverTextSuggest(C.C_MENU_CONTENT + s.node, "Click to copy", s.node));
          } else {
            cs.sendMessage(CText.hoverTextSuggest(C.C_MENU_CONTENT + "§c" + s.node, "Click to copy", s.node));
          }
        }
        cs.sendMessage(C.C_DIV_LINE);
        return;
      } else {
        plugin.printHelp(cs);
      }
    }
  }
}
