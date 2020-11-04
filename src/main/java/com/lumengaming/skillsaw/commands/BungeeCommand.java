/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.models.CommandSyntax;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

/**
 *
 * @author prota
 */
public abstract class BungeeCommand extends Command implements TabExecutor {

  protected static ArrayList<CommandSyntax> syntaxList = new ArrayList<>();

  protected final BungeeMain plugin;

  public BungeeCommand(BungeeMain plugin, String name, String permission, String... aliases) {
    super(name, permission, aliases);
    this.plugin = plugin;
  }

  protected Set<String> getOnlinePlayerNames() {
    Set<String> names = ProxyServer.getInstance().getPlayers().stream().map(x -> x.getName()).collect(Collectors.toSet());
    return names;
  }

  protected Set<String> getFilteredTabComplete(Set<String> options, String arg) {
    final String lArg = arg.toLowerCase();
    Set<String> names = options.stream().filter(x -> x.toLowerCase().startsWith(lArg)).collect(Collectors.toSet());
    return names;
  }

  @Override
  public void execute(CommandSender csw, String[] args) {
    BungeePlayer cs = new BungeePlayer(csw);

    try {
      this.execute(cs, args);
    } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
      printHelp2(cs);
    }
  }

  public abstract void execute(BungeePlayer cs, String[] args);

  protected abstract Iterable<String> onTabCompleteBeforeFiltering(CommandSender cs, String[] args);

  @Override
  public Iterable<String> onTabComplete(CommandSender cs, String[] args) {
    final String lastArg = args.length > 0 ? args[args.length - 1].toLowerCase() : "";
    Iterable<String> raw = this.onTabCompleteBeforeFiltering(cs, args);
    HashSet<String> set = new HashSet<>();
    raw.forEach(r -> {
      if (r.toLowerCase().startsWith(lastArg)) {
        set.add(r);
      }
    });

    return set;
  }

  /**
   *
   * @param requiredPermission
   * @param hideIfNoPermission
   * @param isSubCommand A subcommand is only shown if they mess up the main command. It's error
   * handling helper text.
   * @param syntax
   * @param hoverText
   */
  protected void addSyntax(Permissions requiredPermission, boolean hideIfNoPermission, boolean isSubCommand, String syntax, String hoverText) {
    BaseComponent[] txtSuccess;
    BaseComponent[] txtRed;

    if (hoverText != null) {
      txtSuccess = CText.hoverTextSuggest("§7" + syntax, hoverText, syntax);
      txtRed = CText.hoverTextSuggest("§c" + syntax, hoverText, syntax);
    } else {
      txtSuccess = CText.hoverTextSuggest("§7" + syntax, "Click to copy", syntax);
      txtRed = CText.hoverTextSuggest("§c" + syntax, "Click to copy", syntax);
    }

    CommandSyntax cmd = new CommandSyntax(this.getName(), hideIfNoPermission, isSubCommand, requiredPermission, txtSuccess, txtRed);
    syntaxList.add(cmd);
  }

  public static ArrayList<CommandSyntax> getSyntaxList(boolean includeSubcommands) {
    ArrayList<CommandSyntax> output = new ArrayList<>();
    if (!includeSubcommands) {
      for (CommandSyntax tx : syntaxList) {
        if (!tx.isSubcommandSyntax()) {
          output.add(tx);
        }
      }
    } else {
      output.addAll(syntaxList);
    }

    output.sort(new Comparator<CommandSyntax>() {
      @Override
      public int compare(CommandSyntax o1, CommandSyntax o2) {
        return o1.getRawText().compareToIgnoreCase(o2.getRawText());
      }
    });

    return output;
  }

  public static ArrayList<CommandSyntax> getSyntaxList(String cmdName, boolean includeSubcommands) {
    ArrayList<CommandSyntax> output = new ArrayList<>();
    for (CommandSyntax tx : syntaxList) {

      if (tx.isSubcommandSyntax() && !includeSubcommands) {
        continue;
      }

      if (!tx.getCommandName().equalsIgnoreCase(cmdName)) {
        continue;
      }

      output.add(tx);
    }
    return output;
  }

  public void printHelp2(IPlayer p) {
    ArrayList<CommandSyntax> syntaxes = getSyntaxList(this.getName(), true);
    for (int i = 0; i < syntaxes.size(); i++) {
      CommandSyntax syntax = syntaxes.get(i);
      BaseComponent[] txt = syntax.getErrorSyntax(p);
      if (txt != null) {
        p.sendMessage(txt);
      }
    }
  }
}
