/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.models;

import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.utility.SharedUtility;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import net.md_5.bungee.api.chat.BaseComponent;

/**
 *
 * @author protaks
 */
public class CommandSyntax {

  private Permissions node;
  private final BaseComponent[] syntax;
  private final BaseComponent[] syntaxRed;
  private final boolean isDetailed;
  private final boolean hideIfNoPermission;
  private final String commandName;
  private final String rawText;

  public CommandSyntax(String cmdName, boolean hideIfNoPermission, boolean isSubcommand, Permissions requiredPermission, BaseComponent[] txtSuccess, BaseComponent[] txtRed) {
    this.node = requiredPermission;
    this.isDetailed = isSubcommand;
    this.syntax = txtSuccess;
    this.syntaxRed = txtRed;
    this.hideIfNoPermission = hideIfNoPermission;
    this.commandName = cmdName;
    this.rawText = SharedUtility.removeColorCodes(CText.toLegacyString(txtSuccess), false, false, false, false);
  }

  public boolean isSubcommandSyntax() {
    return isDetailed;
  }

  public boolean canUse(IPlayer p) {
    if (node == null) return true;
    return Permissions.USER_HAS_PERMISSION(p, node, false);
  }

  public boolean canView(IPlayer p) {
    if (node == null) return true;
    if (!this.hideIfNoPermission) return true;
    return Permissions.USER_HAS_PERMISSION(p, node, false);
  }

  /**
   * Only contains something like: /derp herp derp 2
   *
   * @param p
   * @return NULL if P not allowed to see it.
   */
  public BaseComponent[] getErrorSyntax(IPlayer p) {
    if (this.canUse(p)) {
      return this.syntaxRed;
    } else if (!this.hideIfNoPermission) {
      return this.syntaxRed;
    } else {
      return null;
    }
  }

  /**
   * Only contains something like: /derp herp derp 2
   *
   * @param p
   * @return NULL if P not allowed to see it.
   */
  public BaseComponent[] getSyntax(IPlayer p) {
    if (this.canUse(p)) {
      return this.syntax;
    } else if (!this.hideIfNoPermission) {
      return this.syntaxRed;
    } else {
      return null;
    }
  }
  
  public String getRawText() { 
    return this.rawText;
  }

  public String getCommandName() {
    return this.commandName;
  }
}
