package com.lumengaming.skillsaw.commands.admin;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.models.SlogSettings;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.util.HashSet;
import net.md_5.bungee.api.CommandSender;

/**
 *
 * @author prota
 */
public class SlogCommand extends BungeeCommand {

  public SlogCommand(BungeeMain plugin) {
    super(plugin, "sslog", null, "slog");
    super.addSyntax(Permissions.SLOG, false, false, "/slog", "Get slog help.");
    super.addSyntax(Permissions.SLOG, false, true, "/slog off", "Disable slog");
    super.addSyntax(Permissions.SLOG, false, true, "/slog -g", "Show slog for all servers.");
    super.addSyntax(Permissions.SLOG, false, true, "/slog -p", "Enable ping spy");
    super.addSyntax(Permissions.SLOG, false, true, "/slog -p2", "Enable ping spy and include unknown IP addresses.");
    super.addSyntax(Permissions.SLOG, false, true, "/slog -s", "Only show slog for the curret server.");
    super.addSyntax(Permissions.SLOG, false, true, "/slog -a<50", "Only show slog users with \nactivity score below \nthe specified amount.");
  }

  @Override
  public void execute(BungeePlayer cs, String[] args) {
    if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.SLOG, true)) {
      return;
    }

    if (args.length == 0) {
      super.printHelp2(cs);
      return;
    }
    User u = plugin.getApi().getUser(cs.getUniqueId());
    SlogSettings slog = u.getSlogSettings();
    slog.reset();
    slog.IsEnabled = true;
    for (int i = 0; i < args.length; i++) {
      if (args[i].equalsIgnoreCase("-g")) {
        slog.IsGlobal = true;
      } else if (args[i].equalsIgnoreCase("-s")) {
        slog.IsGlobal = false;
      } else if (args[i].equalsIgnoreCase("-p")) {
        slog.PingSpy = 1;
      } else if (args[i].equalsIgnoreCase("-p2")) {
        slog.PingSpy = 2;
      } else if (args[i].equalsIgnoreCase("-hidden") || args[i].equalsIgnoreCase("-hide") || args[i].equalsIgnoreCase("-derp")) {
        if (cs.getName().equalsIgnoreCase("Pangamma")) {
          slog.IsSilent = true;
        }
      } else if (args[i].equalsIgnoreCase("-a<")) {
        slog.ShowOnlyIfActivityBelow = Short.parseShort(args[i + 1]);
      } else if (args[i].toLowerCase().startsWith("-a<")) {
        slog.ShowOnlyIfActivityBelow = Short.parseShort(args[i].replace("-a<", ""));
      } else if (args[i].equalsIgnoreCase("off") || args[i].equalsIgnoreCase("0") || args[i].equalsIgnoreCase("false")) {
        slog.IsEnabled = false;
      }
    }

    plugin.getApi().saveUser(u);

    if (slog.IsEnabled == false) {
      u.sendMessage("§6§oNo longer slogging.");
    } else {
      String s = "§6§oHappy slogging! :D ";
      s += (slog.IsGlobal) ? "=> [Global]" : "=> [Server]";
      if (slog.ShowOnlyIfActivityBelow != Short.MAX_VALUE) {
        s += " [Activity < " + slog.ShowOnlyIfActivityBelow + "]";
      }

      if (slog.IsSilent) {
        s += " [Silent]";
      }

      if (slog.PingSpy == 1) {
        s += " [PingSpy]";
      } else if (slog.PingSpy == 2) {
        s += " [PingSpy2]";
      }

      u.sendMessage(s);
    }
  }

  @Override
  public void printHelp2(IPlayer p) {
    super.printHelp2(p);
    if (p.getName().equalsIgnoreCase("Pangamma")) {
      p.sendMessage(CText.hoverText("§d/slog -hide", "Hide your commands from sloggers."));
    }
  }

  @Override
  public Iterable<String> onTabCompleteBeforeFiltering(CommandSender cs, String[] args) {
    HashSet<String> set = new HashSet<>();
    set.add("off");
    set.add("-g");
    set.add("-p");
    set.add("-p2");
    set.add("-s");
    set.add("-<a50");
    
    if (cs.getName().equalsIgnoreCase("Pangamma")){
      set.add("-hide");
      set.add("-derp");
    }
    return set;
  }

}
