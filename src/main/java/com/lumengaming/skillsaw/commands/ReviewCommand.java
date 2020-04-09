package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.config.Options;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.models.XLocation;
import com.lumengaming.skillsaw.utility.C;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;

/**
 * @author Taylor Love (Pangamma)
 */
public class ReviewCommand extends BungeeCommand {

  //private LinkedList<ReviewRequest> requests = new LinkedList<ReviewRequest>();
  private final String regexList = "^(?i)(l|list)(.*)";
  private final String regexTeleport = "^(?i)(tp|tele|goto)(.*)";
  private final String regexReviewNormal = "^(?i)(req|this|here|casual|normal)(.*)";
  private final LinkedList<ReviewRequest> requests = new LinkedList<>();

  public ReviewCommand(BungeeMain plugin) {
    super(plugin, "review", null, "rev", "reviewlist");
  }

  @Override
  public Iterable<String> onTabCompleteBeforeFiltering(CommandSender cs, String[] args) {
    HashSet<String> set = new HashSet<>();

    switch (args.length) {
      case 1:
        set.add("list");
        set.add("tp");
        set.add("this");
        set.add("clear");
        set.add("remove");
        break;
      case 2:
        switch (args[0].toLowerCase()) {
          case "tp":
          case "tele":
          case "goto":
          case "remove":
            set.addAll(this.getOnlinePlayerNames());
            break;

          default:
            break;
        }
        break;
      default:
        break;
    }

    return set;
  }

  @Override
  public void execute(BungeePlayer cs, String[] args) {
    if (!cs.isPlayer()) {
      cs.sendMessage(C.ERROR_PLAYERS_ONLY);
      return;
    }

    try {
      if (args[0].matches(regexReviewNormal)) {
        if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.REVIEW_THIS)) {
          return;
        }

        cs.sendMessage(C.MSG_PROCESSING);
        plugin.getSender().getPlayerLocation(cs.p(), (loc) -> {

          boolean canPostAgain = true;
          //<editor-fold defaultstate="collapsed" desc="have they posted before in the last X minutes?">
          long timeOfLastPost = 0;
          long now = System.currentTimeMillis();
          ReviewRequest remove = null;
          for (ReviewRequest r : requests) {
            if (r.getRequester().equalsIgnoreCase(cs.getName())) {
              canPostAgain = false;
              remove = r;
              //if the request is more than 1 hour old...
              if (r.getTimeOfReq() + (Options.Get().ReviewList.MinutesRequiredBetweenReposts * 60000) <= now) {
                canPostAgain = true;
              }
            }
          }
          //</editor-fold>

          //<editor-fold defaultstate="collapsed" desc="either post it to the list, or tell them how long to wait. ">
          if (canPostAgain) {
            if (remove != null) {
              requests.remove(remove);
            }
            requests.addFirst(new ReviewRequest(cs.getName(), loc));
            cs.sendMessage("§2Post has been submitted to the top of the reviewing list. :)");
            BaseComponent[] bcPart1 = CText.legacy("§7" + cs.getName() + " just created a new §aPeer§7 review request. Use ");
            BaseComponent[] bcCommand = CText.legacy("§f/review tp " + cs.getName());
            CText.applyEvent(bcCommand, new HoverEvent(HoverEvent.Action.SHOW_TEXT, CText.legacy("Click to teleport")));
            CText.applyEvent(bcCommand, new ClickEvent(Action.RUN_COMMAND, "/review tp " + cs.getName()));
            BaseComponent[] bcPart2 = CText.legacy("§7 if you want to see it. BTW, there is §aautocomplete§7 so you don't need to type in their full name.");

            bcPart1 = CText.merge(bcPart1, bcCommand);
            bcPart1 = CText.merge(bcPart1, bcPart2);
            plugin.broadcast(bcPart1);

            plugin.getSender().doReviewListUpdatedEffect(() -> {
            });
          } else {
            double minutesRemaining = -1;
            if (remove != null) {
              long msRemaining = (long) ((remove.getTimeOfReq() + Options.Get().ReviewList.MinutesRequiredBetweenReposts * 60000) - System.currentTimeMillis());
              minutesRemaining = (double) (msRemaining / 60000);
            }
            cs.sendMessage("§cYou are only allowed to post your builds once per §4" + Options.Get().ReviewList.MinutesRequiredBetweenReposts
                    + " minutes§c. D: You must wait §4" + minutesRemaining + " minutes§c before posting to the list again."
                    + " If your build §4falls off the end of the list§c you will be able to repost early.");
          }
          //</editor-fold>
          //<editor-fold defaultstate="collapsed" desc="purge so list stays under X reviews">
          while (requests.size() > Options.Get().ReviewList.MaxEntriesInPublicReviewList) {
            requests.removeLast();
          }
          //</editor-fold>
        });

      } else if (args[0].matches(regexList)) {
        if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.REVIEW_LIST)) {
          return;
        }
        //<editor-fold defaultstate="collapsed" desc="list">
        cs.sendMessage(C.C_DIV_LINE);
        ArrayList<BaseComponent[]> results = new ArrayList<BaseComponent[]>();
        for (ReviewRequest r : requests) {
          String strike = r.visitors.contains(cs.getName()) ? "§m" : "";
          String msg = C.C_MENU_CONTENT + strike + r.getRequester() + " @ " + ((System.currentTimeMillis() - r.getTimeOfReq()) / 3600000) + "h " + ((System.currentTimeMillis() - r.getTimeOfReq()) / 60000) + "m";
          BaseComponent[] text = CText.hoverText(msg, "Click to teleport");
          CText.applyEvent(text, new ClickEvent(Action.RUN_COMMAND, "/rev tp " + r.getRequester()));
          results.add(text);
        }
        while (!results.isEmpty()) {
          cs.sendMessage(results.remove(results.size() - 1));
        }
        cs.sendMessage(C.C_DIV_LINE);
        //<editor-fold defaultstate="collapsed" desc="summary">
        int size = requests.size();
        if (size == 0) {
          cs.sendMessage(C.C_MENU_CONTENT + "No one is waiting for a peer review right now.");
        } else if (size == 1) {
          cs.sendMessage(C.C_MENU_CONTENT + "There is one person waiting for a peer review right now.");
          cs.sendMessage(C.C_MENU_CONTENT + "/review tp <their name>");
        } else {
          cs.sendMessage(C.C_MENU_CONTENT + "There are " + requests.size() + " people waiting for a peer review right now.");
          cs.sendMessage(C.C_MENU_CONTENT + "/review tp <their names>");
        }
        //</editor-fold>
        cs.sendMessage(C.C_DIV_LINE);
        //</editor-fold>
      } else if (args[0].matches(regexTeleport)) {
        if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.REVIEW_LIST)) {
          return;
        }
        //<editor-fold defaultstate="collapsed" desc="teleport">
        if (args.length == 1) {
          if (requests.isEmpty()) {
            cs.sendMessage("§cThere are no review requests right now.");
          } else {
            ReviewRequest match = null;
            for (ReviewRequest r : requests) {
              if (!r.hasVisited(cs.getName())) {
                match = r;
                break;
              }
            }
            if (match == null) {
              cs.sendMessage("§cThere are no review requests right now. Go build!");
              match = requests.getFirst();
            } else {
              match.logReviewTeleportVisit(cs);
            }
            plugin.getSender().setLocation(cs.p(), match.getLoc(), (b) -> {
              // Meh.
            });
          }
        } else if (args.length != 2) {
          cs.sendMessage("§c/review tp [name of person]");
        } else {
          ReviewRequest failureModeMatch = null;
          ReviewRequest match = null;
          for (ReviewRequest r : requests) {
            if (r.getRequester().equalsIgnoreCase(args[1])) {
              match = r;
              break;
            } else if (r.getRequester().toLowerCase().contains(args[1].toLowerCase())) {
              failureModeMatch = r;
            }
          }
          if (match != null) {

            plugin.getSender().setLocation(cs.p(), match.getLoc(), (b) -> {
              // Meh.
            });
            match.logReviewTeleportVisit(cs);
          } else if (failureModeMatch != null) {
            plugin.getSender().setLocation(cs.p(), failureModeMatch.getLoc(), (b) -> {
              // Meh.
            });
            failureModeMatch.logReviewTeleportVisit(cs);
          } else {
            cs.sendMessage("§cThere wasn't any review in the public review list under that name. Check to see which people are awaiting reviews. §4/review list");
          }
        }
        //</editor-fold>
      } else if (args[0].toLowerCase().equalsIgnoreCase("clear")) {
        if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.REVIEW_REMOVE_OTHERS)) {
          return;
        }
        //<editor-fold defaultstate="collapsed" desc="clear">
        if (Permissions.USER_HAS_PERMISSION(cs, Permissions.ALL)) {
          this.requests.clear();
          cs.sendMessage("§2Review list has been cleared.");
        } else {
          cs.sendMessage(Permissions.TELL_USER_PERMISSION_THEY_LACK(Permissions.ALL.node));
        }
        //</editor-fold>
      } else if (args[0].toLowerCase().equalsIgnoreCase("remove")) {
        if (args.length == 2) {
          if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.REVIEW_REMOVE_OTHERS)) {
            return;
          }
          if (this.requests.remove(new ReviewRequest(args[1]))) {
            cs.sendMessage("§2Review removed from the list.");
          } else {
            cs.sendMessage("§cCouldn't find a review with the poster's name: '" + args[1] + "'.");
          }
        } else {
          if (!Permissions.USER_HAS_PERMISSION(cs, Permissions.REVIEW_REMOVE_SELF)) {
            return;
          }
          this.requests.remove(new ReviewRequest(cs.getName()));
          cs.sendMessage("§2Review removed from the list.");
        }
      } else {
        printHelp(cs);
      }
    } catch (ArrayIndexOutOfBoundsException aie) {
      printHelp(cs);
    }
  }

//<editor-fold defaultstate="collapsed" desc="review request class">
  private class ReviewRequest {

    private final String requester;
    private final XLocation loc;
    private final long timeOfReq;
    private final HashSet<String> visitors = new HashSet<String>();

    /**
     *
     * @param requester	-- who made the request?
     * @param loc	-- the location the request was made in.
     * @param isProReview	-- specifies whether or not review requests will be
     * shown as red to all instructors.
     */
    public ReviewRequest(String requester, XLocation loc) {
      this.requester = requester;
      this.loc = loc;
      this.timeOfReq = System.currentTimeMillis();
      this.visitors.add(requester);
    }

    @Deprecated
    private ReviewRequest(String arg) {
      this(arg, null);
    }

    public String getRequester() {
      return requester;
    }

    public XLocation getLoc() {
      return loc;
    }

    public long getTimeOfReq() {
      return timeOfReq;
    }

    /**
     * Returns this.requester *
     */
    @Override
    public String toString() {
      return this.requester;
    }

    /**
     * only compares the name of the requester, because there will only be one
     * request per user at any given time. *
     */
    @Override
    public boolean equals(Object o) {
      if (o != null) {
        if (o instanceof ReviewRequest) {
          ReviewRequest req = (ReviewRequest) o;
          return this.requester.equalsIgnoreCase(req.requester);
        }
      }
      return false;
    }

    /**
     * terrible method name, I know. Basically just use this method to log a
     * teleport to this review request. *
     */
    public void logReviewTeleportVisit(IPlayer p) {
      p.sendMessage("§aTeleported to " + getRequester() + "'s submission. Review it with the /rep command if you are feeling generous.");
      if (!this.visitors.contains(p.getName())) {
        User visitor = plugin.getDataService().getUser(p.getUniqueId());
        if (visitor == null) {
          // Let them teleport to the location again if they want the rep...
          // otherwise just let them do their visit, but don't try to give
          // a rep reward to a ghost.
//					p.sendMessage(C.ERROR_TRY_AGAIN_LATER_COMMAND);
          return;
        }
        this.visitors.add(p.getName());
        double tmp = User.getRepBetweenLevelAndLevel(visitor.getRepLevel(), visitor.getRepLevel() + 1);

        if (tmp < 0.1) {
          tmp = 0.1;
        } //never give only zero.
        int level = visitor.getRepLevel();
        double bonusToGive = visitor.getRepBetweenLevelAndLevel(level, level + 1) / 200;
        double chance = (1.0 + new Random().nextInt(100)) / 100;
        bonusToGive *= chance;
        bonusToGive = User.round(bonusToGive);
        if (bonusToGive < 0.1) {
          bonusToGive = 0.1;
        } else if (bonusToGive > 10) {
          bonusToGive = 10;
        }
        visitor.addNaturalRep(bonusToGive);
        p.sendMessage("§2Randomly awarded " + bonusToGive + " rep for visiting this review request! :D");
        plugin.getDataService().saveUser(visitor);
      }
    }

    public boolean hasVisited(String name) {
      return (this.visitors.contains(name));
    }
  }
//</editor-fold>

  public void printHelp(IPlayer cs) {
    cs.sendMessage("§c/review this");
    cs.sendMessage("§c/review remove [name]");
    cs.sendMessage("§c/review l[ist]");
    cs.sendMessage("§c/review tp [name]");
    cs.sendMessage("§c/review clear");
  }
}
