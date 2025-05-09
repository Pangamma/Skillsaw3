package com.lumengaming.skillsaw.commands.skills;

import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.models.RepLogEntry;
import com.lumengaming.skillsaw.models.RepType;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.service.DataService;
import com.lumengaming.skillsaw.utility.C;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.plugin.TabExecutor;

public abstract class AbstractViewLogCommand extends BungeeCommand implements TabExecutor {

    public AbstractViewLogCommand(BungeeMain plugin, String commandName, String... aliases) {
        super(plugin, commandName, null, aliases);
    }

    public void execute(BungeePlayer cs, String cmdAlias, String[] args) {

        if (args.length == 0) {
            args = new String[]{cs.getName()};
        }

        try {
            switch (cmdAlias.toLowerCase()) {
                case "replog":
                    getLogs(cs, RepType.NaturalRep, args[0]);
                    break;
                case "vlog":
                case "viewlog":
                    getLogs(cs, args[0]);
                    break;
                case "sreplog":
                    getLogs(cs, RepType.StaffRep, args[0]);
                    break;
                case "xreplog":
                    getLogs(cs, RepType.XRep, args[0]);
                    break;
                case "notes":
                    getLogs(cs, RepType.Note, args[0]);
                    break;
                default:
                    printHelp(cs);
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException nfe) {
            printHelp(cs);
        }
    }

    private boolean getLogs(BungeePlayer cs, RepType rt, String targetName) {
        DataService ds = plugin.getDataService();
        // do they have permission?
        if (rt == RepType.NaturalRep && !Permissions.USER_HAS_PERMISSION(cs, Permissions.VIEWLOGS_NATURAL_REP)
            || rt == RepType.Note && !Permissions.USER_HAS_PERMISSION(cs, Permissions.VIEWLOGS_NOTE)
            || rt == RepType.StaffRep && !Permissions.USER_HAS_PERMISSION(cs, Permissions.VIEWLOGS_STAFF_REP)
            || rt == RepType.XRep && !Permissions.USER_HAS_PERMISSION(cs, Permissions.VIEWLOGS_REP_FIX)) {
            return false;
        }

        ds.getOfflineUser(targetName, true, (User target) -> {
            if (target == null) {
                cs.sendMessage(C.ERROR_P_NOT_FOUND);
                return;
            }
            ds.getLogEntriesByTarget(rt, target.getUuid(), 15, 0, (ArrayList<RepLogEntry> entries) -> {
                boolean colorA = false;
                cs.sendMessage(C.C_DIV_LINE);
                cs.sendMessage(C.C_DIV_TITLE_PREFIX + rt.name() + " Log Entries for " + target.getName());
                cs.sendMessage(C.C_DIV_LINE);
                for (RepLogEntry e : entries) {
                    colorA = !colorA;
                    String c1 = colorA ? "§7" : "§8";
                    String c2 = colorA ? "§a" : "§2";
                    String reason = e.getReason();
                    String name = e.getIssuerName();
                    cs.sendMessage(CText.hoverText(c1 + "[" + c2 + e.getAmount() + c1 + "] " + c2 + name + c1 + " -> " + c2 + reason, e.getTime().toString()));
                }
                cs.sendMessage(C.C_DIV_LINE);
            });
        });
        return true;
    }

    private boolean getLogs(BungeePlayer cs, String targetName) {
        DataService ds = plugin.getDataService();
        // do they have permission?
        if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.ALL)) {
            return false;
        }

        ds.getOfflineUser(targetName, true, (User target) -> {
            if (target == null) {
                cs.sendMessage(C.ERROR_P_NOT_FOUND);
                return;
            }
            ds.getLogEntriesByTarget(target.getUuid(), 15, 0, (ArrayList<RepLogEntry> entries) -> {
                boolean colorA = false;
                cs.sendMessage(C.C_DIV_LINE);
                cs.sendMessage(C.C_DIV_TITLE_PREFIX + " * Log Entries for " + target.getName());
                cs.sendMessage(C.C_DIV_LINE);
                for (RepLogEntry e : entries) {
                    colorA = !colorA;
                    String c1 = colorA ? "§7" : "§8";
                    String c2 = colorA ? "§a" : "§2";
                    String reason = e.getReason();
                    String name = e.getIssuerName();
                    Timestamp time = e.getTime();
                    cs.sendMessage(CText.hoverText(c1 + "[" + e.getType().name() + "][" + c2 + e.getAmount() + c1 + "] " + c2 + name + c1 + " -> " + c2 + reason, e.getTime().toString()));
                }
                cs.sendMessage(C.C_DIV_LINE);
            });
        });
        return true;
    }

    @Override
    public Iterable<String> onTabCompleteBeforeFiltering(CommandSender cs, String[] args) {
      HashSet<String> set = new HashSet<>();
      if (args.length == 1){
        set.addAll(this.getOnlinePlayerNames());
      }
      return set;
    }
    
  
    public static void printHelp(IPlayer cs) {
        BaseComponent[] txt = CText.hoverText("§c/replog [target]", "§cView " + RepType.NaturalRep.name() + "(s) given to a player.");
        CText.applyEvent(txt, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/replog "));
        cs.sendMessage(txt);
        txt = CText.hoverText("§c/sreplog [target]", "§cView " + RepType.StaffRep.name() + "(s) given to a player.");
        CText.applyEvent(txt, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/seplog "));
        cs.sendMessage(txt);
        txt = CText.hoverText("§c/xreplog [target]", "§cView " + RepType.XRep.name() + "(s) given to a player.");
        CText.applyEvent(txt, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/xeplog "));
        cs.sendMessage(txt);
        txt = CText.hoverText("§c/notes [target]", "§cView " + RepType.Note.name() + "(s) given to a player.");
        CText.applyEvent(txt, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/notes "));
        cs.sendMessage(txt);
        txt = CText.hoverText("§c/viewlog [target]", "§cView all rep type entries given to a player.");
        CText.applyEvent(txt, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/viewlog "));
        cs.sendMessage(txt);
    }
}
