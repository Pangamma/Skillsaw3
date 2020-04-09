/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.commands.teleportation;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.models.BooleanAnswer;
import com.lumengaming.skillsaw.models.TPRequest;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.models.XLocation;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.C;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import java.time.Duration;
import java.util.HashSet;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author prota
 */
public class TpaCommand extends BungeeCommand {

  public TpaCommand(BungeeMain aThis) {
    super(aThis, "tpaccept", null, "tpa");
    super.addSyntax(Permissions.TPA_TO, true, false, "/tpa <target>", "Request to teleport\nto the specified\nplayer.");
  }

  @Override
  public Iterable<String> onTabCompleteBeforeFiltering(CommandSender cs, String[] args) {
    HashSet<String> set = new HashSet<>();
    if (args.length == 1) {
      set.addAll(this.getOnlinePlayerNames());
    }
    return set;
  }

  @Override
  public void execute(BungeePlayer cs, String[] args) {
    if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.TPA_TO, true)) {
      return;
    }

    if (!cs.isPlayer()) {
      cs.sendMessage(C.ERROR_PLAYERS_ONLY);
      return;
    }

    if (args.length != 1) {
      printHelp2(cs);
      return;
    }

    User from = plugin.getApi().getUser(cs.getUniqueId());
    if (from == null) {
      cs.sendMessage(C.ERROR_TRY_AGAIN_LATER_COMMAND);
      return;
    }

    final User to = plugin.getApi().getUserBestOnlineMatch(args[0]);
    if (to == null) {
      cs.sendMessage(C.ERROR_P_NOT_FOUND);
      return;
    }

    BaseComponent[] promptForFromUser;
    if (to.getTpaLockState() == BooleanAnswer.No) {
      from.sendMessage("§4" + to.getDisplayName() + "§c has automatically §4denied§c your teleport request with /tpalock.");
      to.sendMessage("§cYou have §4denied§c " + from.getDisplayName() + "§a's teleport request with /tpalock.");
      return;
    }

    if (to.getTpaLockState() == BooleanAnswer.Yes) {
      from.sendMessage("§2" + to.getDisplayName() + "§a has automatically §2accepted§a your teleport request with /tpalock.");
      to.sendMessage("§aYou have §2accepted§a " + from.getDisplayName() + "§a's teleport request with /tpalock.");
      plugin.getSender().getPlayerLocation((ProxiedPlayer) to.getRawPlayer(), (loc) -> {
        plugin.getSender().setLocation((ProxiedPlayer) from.getRawPlayer(), loc, (b) -> {
          cs.sendMessage("§aTeleported!");
        });
      });
      return;
    }

    BaseComponent[] p1 = CText.legacy("§6" + from.getName() + "§e wants to teleport to you! Will you allow it? ");
    BaseComponent[] p2 = CText.hoverTextForce("§6/tpaccept§e, ", "Accept the request", "/tpaccept");
    BaseComponent[] p3 = CText.hoverTextForce("§6/tpdeny§e, ", "Reject the request", "/tpdeny");
    BaseComponent[] p4 = CText.legacy("§eYou can also specify automatic reactions to requests in the future with ");
    BaseComponent[] p5 = CText.hoverTextSuggest("§6/tpalock <yes/no/ask>", "Click to copy", "/tpalock");
    promptForFromUser = CText.merge(p1, p2, p3, p4, p5);

    cs.sendMessage("§eSent a request to §6" + to.getName() + "§e asking them if you can teleport to them.");
    TPRequest req = new TPRequest(from.p(), to.p(), new XLocation(), TPRequest.TpaType.TPA);
    if (!plugin.getTeleportRequests().contains(req.getKey())) {
      plugin.getTeleportRequests().put(req.getKey(), req, Duration.ofMinutes(1), (expired) -> {
        cs.sendMessage("§cYour teleport request to §4" + to.getName() + "§c has expired.");
        to.sendMessage("§cYour teleport request from §4" + cs.getName() + "§c has expired.");
      });
      to.sendMessage(promptForFromUser);
    }
  }

}
