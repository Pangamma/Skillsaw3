package com.lumengaming.skillsaw.commands.admin;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.config.ConfigHelper;
import com.lumengaming.skillsaw.models.LockoutSettings;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.LruCache;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * @author prota
 */
public class LockoutCommand extends BungeeCommand implements Listener {

  private class CachedLockoutResult {

    public String Username;
    public Boolean IsAllowed = null;
    public String DenyReason = null;

    public CachedLockoutResult(String username, Boolean isAllowed) {
      this.Username = username;
      this.IsAllowed = isAllowed;
    }

    public CachedLockoutResult(String username, Boolean isAllowed, String denyReason) {
      this.Username = username;
      this.IsAllowed = isAllowed;
      this.DenyReason = denyReason;
    }
  }

  private LockoutSettings lockoutSettings;
  private final LruCache<UUID, CachedLockoutResult> cache = new LruCache<>(500); // Cache of forever.

  //<editor-fold defaultstate="collapsed" desc="Setup">
  /**
   * TODO: Could also do a thing where it says, "Foo has tried to join. Would you like to allow
   * their next attempt? [Allow] [Ignore]"
   */
  public LockoutCommand(BungeeMain plugin) {
    super(plugin, "lockout", null, "skillsaw-lockout", "lockdown");
    super.addSyntax(Permissions.LOCKOUT, true, false, "/ss lockout", "Get lockout help.");
    super.addSyntax(Permissions.LOCKOUT, true, true, "/ss lockout ?", "View current lockout settings.");
    super.addSyntax(Permissions.LOCKOUT, true, true, "/ss lockout allow=<user>", "Allow a user to bypass the lockdown.");
    super.addSyntax(Permissions.LOCKOUT, true, true, "/ss lockout deny=<user>", "Stop allowing a user to bypass the lockdown.");

    super.addSyntax(Permissions.LOCKOUT, true, true, "/ss lockout 0/off", "Disable lockout");
    super.addSyntax(Permissions.LOCKOUT, true, true, "/ss lockout 1/on", "Enable lockout");

    super.addSyntax(Permissions.LOCKOUT, true, true, "/ss lockout -v", "Verbose. Show 'Tried to connect' messages.");
//    super.addSyntax(Permissions.LOCKOUT, true, true, "/ss lockout -ip", "Deny new IP addresses.");
//    super.addSyntax(Permissions.LOCKOUT, true, true, "/ss lockout -username", "Deny new users.");
    super.addSyntax(Permissions.LOCKOUT, true, true, "/ss lockout -a<40", "Deny users with activity \nscores of less than 40.");
    super.addSyntax(Permissions.LOCKOUT, true, true, "/ss lockout -minutes<40", "Deny users with less than \n40 minutes of total play time.");
    super.addSyntax(Permissions.LOCKOUT, true, true, "/ss lockout -replevel<10", "Deny users with rep level \nless than 10.");
    super.addSyntax(Permissions.LOCKOUT, true, true, "/ss lockout -allowInstructors", "Allow any instructor.\nThis is an override.");
    super.addSyntax(Permissions.LOCKOUT, true, true, "/ss lockout -allowStaff", "Allow any staff member.\nThis is an override.");

    /*
    IP address (only allow pre-saved IP addresses)
    NEW users (do not allow new users)
    TOTAL activity time
    ACTIVITY SCORE (last two weeks)
    SKILL TIERS
    REP_LEVEL
     */
  }

  public void onEnable() {
    this.lockoutSettings = new LockoutSettings();
//    List<BungeePlayer> staff = ProxyServer.getInstance().getPlayers().stream()
//            .map(p -> new BungeePlayer(p))
//            .filter(p -> Permissions.USER_HAS_PERMISSION(p, Permissions.LOCKOUT_EDIT, false))
//            .collect(Collectors.toList());
  }

  public void onDisable() {
    if (this.lockoutSettings != null) {
      this.lockoutSettings.reset();
    }

    this.cache.clear();
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Command logic">
  @Override
  public void execute(BungeePlayer cs, String[] args) {
    if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.LOCKOUT, true)) {
      return;
    }

    if (args.length == 0) {
      super.printHelp2(cs);
      return;
    }

    String larg0 = args[0].toLowerCase();
    if (larg0.equals("?")) {
      String json = ConfigHelper.getGson().toJson(this.lockoutSettings);
      BaseComponent[] legacy = CText.legacy(json);
      CText.applyEvent(legacy, new HoverEvent(HoverEvent.Action.SHOW_TEXT, CText.legacy("Click to copy command.")));
      CText.applyEvent(legacy, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, this.lockoutSettings.toString()));
    } else if (larg0.startsWith("-allow=")) {
      String name = larg0.split("=")[1];
      Optional<LruCache.LinkedListNode<UUID, CachedLockoutResult>> found = this.cache.values().stream()
              .filter(x -> x.val.Username.equalsIgnoreCase(name)).findFirst();
      if (!found.isPresent()) {
//        cs.sendMessage(ConfigHelper.getGson().toJson(this.cache.getValues()));
        cs.sendMessage("§cThat user is not in the cache... And Pangamma was too lazy to query the DB for a username to UUID check.");
      } else {
        found.get().val.IsAllowed = true;
        cs.sendMessage("§a" + name + " added to the allowed list.");
      }
      return;
    } else if (larg0.startsWith("-deny=")) {
      String name = larg0.split("=")[1];
      Optional<LruCache.LinkedListNode<UUID, CachedLockoutResult>> found = this.cache.values().stream()
              .filter(x -> x.val.Username.equalsIgnoreCase(name)).findFirst();
      if (!found.isPresent()) {
        cs.sendMessage("§cThat user is not in the cache... why bother blocking them at all?");
      } else {
        found.get().val.IsAllowed = false;
        cs.sendMessage("§cThat user will be denied until they are cleared from the cache.");
      }
      return;
    }

    LockoutSettings slog = new LockoutSettings();
    slog.IsEnabled = true;

    for (int i = 0; i < args.length; i++) {
      String larg = args[i].toLowerCase();
      if (args[i].equalsIgnoreCase("-v")) {
        slog.IsVerbose = true;
      } else if (larg.startsWith("-a<")) {
        slog.DenyLessActiveThan = Integer.parseInt(larg.split("<")[1]);
      } else if (larg.startsWith("-minutes<")) {
        slog.DenyNewerThanXMinutes = Integer.parseInt(larg.split("<")[1]);
      } else if (larg.startsWith("-replevel<")) {
        slog.MinimumRepLevelToAllow = Integer.parseInt(larg.split("<")[1]);
      } else if (larg.equals("0") || larg.equals("off") || larg.equals("false")) {
        slog.IsEnabled = false;
      } else if (larg.equals("1") || larg.equals("on") || larg.equals("true")) {
        slog.IsEnabled = false;
      } else if (larg.equals("-allowstaff")) {
        slog.AllowStaff = true;
      } else if (larg.equals("-allowinstructors")) {
        slog.AllowInstructors = true;
      }
    }

    this.lockoutSettings = slog;
    this.cache.clear();

    if (this.lockoutSettings.IsEnabled){
      ProxyServer.getInstance().broadcast(CText.hoverText("§f[§6Lockdown§f]§7 Lockdown has been §aactivated§7!", this.lockoutSettings.toString()));
    } else {
      ProxyServer.getInstance().broadcast(CText.hoverText("§f[§6Lockdown§f]§7 Lockdown has been §cdeactivated§7!", this.lockoutSettings.toString()));
    }
  }

  @Override
  public Iterable<String> onTabCompleteBeforeFiltering(CommandSender cs, String[] args) {
    HashSet<String> set = new HashSet<>();

    set.add("off");
    set.add("-v");
    set.add("-a<");
    set.add("-minutes<");
    set.add("-replevel<");
    set.add("-allow=");
    set.add("-deny=");
    set.add("-allowInstructors");
    set.add("-allowStaff");

    return set;
  }

  //</editor-fold>
  //<editor-fold defaultstate="collapsed" desc="Listener logic">
  public String getRejectionReason(User u, LockoutSettings ls) {
    if (u == null) {
      return "§f[§cLockdown Active§f]\n New users are denied entry.";
    }

    if (ls.AllowStaff && u.isStaff()) {
      return null;
    }

    if (ls.AllowInstructors && u.isInstructor()) {
      return null;
    }

    {
      int totalMinutes = u.getTotalMinutesPlayed();
      if (totalMinutes < ls.DenyNewerThanXMinutes) {
        return "§f[§cLockdown Active§f]\nPlayers must have " + ls.DenyNewerThanXMinutes + " of play time or more to enter.\nNew players are restricted for now.";
      }
    }

    {
      int totalMinutes = u.getActivityScore();
      if (totalMinutes < ls.DenyLessActiveThan) {
        return "§f[§cLockdown Active§f]\nPlayers must have an activity score of " + ls.DenyLessActiveThan + " or higher to enter.";
      }
    }

    if (u.getRepLevel() < ls.MinimumRepLevelToAllow) {
      return "§f[§cLockdown Active§f]\nYou must have a rep level of " + ls.MinimumRepLevelToAllow + " or higher to enter.";
    }

    return null;
  }

  private void sendLoginAttemptMessage(String username, String ip) {
    BaseComponent[] txt = CText.hoverText("§f[§6Lockout§f]§7 " + username + " tried to join but was rejected. ", ip);
    txt = CText.merge(txt, CText.hoverTextForce("§f[§aAllow§f] ", "Click to allow", "/lockout -allow=" + username));
    txt = CText.merge(txt, CText.hoverTextForce("§f[§cDeny§f] ", "Click to deny", "/lockout -deny=" + username));

    List<BungeePlayer> staff = ProxyServer.getInstance().getPlayers().stream()
            .map(p -> new BungeePlayer(p))
            .filter(p -> Permissions.USER_HAS_PERMISSION(p, Permissions.LOCKOUT_EDIT, false))
            .collect(Collectors.toList());

    for (BungeePlayer bp : staff) {
      bp.sendMessage(txt);
    }
  }

  @EventHandler
  public void onLogin(final PostLoginEvent e) {
    if (this.lockoutSettings != null && this.lockoutSettings.IsEnabled){
      e.getPlayer().sendMessage(CText.hoverText("§f[§6Lockdown§f]§7 Lockdown is active right now!", this.lockoutSettings.toString()));
    }
  }
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void onLogin(final LoginEvent e) {

    LockoutSettings ls = this.lockoutSettings;
    if (!ls.IsEnabled) return;
    String ipv4 = e.getConnection().getAddress().getAddress().getHostAddress();
    UUID uuid = e.getConnection().getUniqueId();
    String username = e.getConnection().getName();

    if (uuid == null)
      throw new IllegalStateException("Skillsaw3: The UUID was not set? Wtf kind of sorcery is this??");

    CachedLockoutResult rs = this.cache.get(uuid);
    if (rs != null) {
      if (rs.IsAllowed == true) {
        e.setCancelled(false);
        return;
      } else if (rs.IsAllowed == false) {
        e.setCancelled(true);
        e.setCancelReason(CText.legacy(rs.DenyReason));
        if (!rs.Username.equals("NewPlayer")) {
          if (this.lockoutSettings.IsVerbose) {
            sendLoginAttemptMessage(rs.Username, ipv4);
          }
        }
        return;
      } else if (rs.IsAllowed == null) {
        // Not yet checked....
      }
    }

    rs = null;
    e.registerIntent((Plugin) this.plugin);
    LockoutCommand.this.plugin.getApi().getOfflineUser(uuid, true, (User u) -> {
      try {
        String reason = getRejectionReason(u, ls);
        if (reason == null) {
          cache.put(uuid, new CachedLockoutResult(u == null
                  ? "NewPlayer" : u.getName(), true, reason));
          e.setCancelled(false);
          return;
        }

        if (this.lockoutSettings.IsVerbose) {
          reason += "\n§dYou might get added to the whitelist.";
        } else {
          reason += "\n§cPlease try again later. :)";
        }

        e.setCancelReason(CText.legacy(reason));
        e.setCancelled(true);
        cache.put(uuid, new CachedLockoutResult(u == null ? "NewPlayer" : u.getName(), false, reason));

        if (u != null) {
          if (this.lockoutSettings.IsVerbose) {
            sendLoginAttemptMessage(u.getName(), ipv4);
          }
        }

      } finally {
        e.completeIntent((Plugin) LockoutCommand.this.plugin);
      }
    });
  }
//</editor-fold>
}
