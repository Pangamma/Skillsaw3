package com.lumengaming.skillsaw.commands.skills;

import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.utility.C;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;

/**
 * @author Taylor Love (Pangamma)
 */
public class SkillSawCommand extends BungeeCommand {

    public SkillSawCommand(BungeeMain plugin) {
        super(plugin, "skillsaw", null, "ss", "ssaw");
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
            } else if (args[0].equalsIgnoreCase("perms")) {
                cs.sendMessage(C.C_DIV_LINE);
                cs.sendMessage(C.C_DIV_TITLE_PREFIX + "Permissions");
                cs.sendMessage(C.C_DIV_LINE);
                for (Permissions s : Permissions.values()) {
                    if (Permissions.USER_HAS_PERMISSION(cs, s, false)){
                        cs.sendMessage(CText.hoverTextSuggest(C.C_MENU_CONTENT + s.node, "Click to copy", s.node));
                    }else{
                        cs.sendMessage(CText.hoverTextSuggest(C.C_MENU_CONTENT +"§c"+ s.node, "Click to copy", s.node));
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
