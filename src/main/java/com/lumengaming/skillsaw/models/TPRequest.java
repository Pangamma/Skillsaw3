/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.models;

import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;

/**
 * @author Taylor
 */
public class TPRequest {
    private final XLocation loc;

    public String getKey(){
        return getKey(this.from.getName(), this.to.getName(), this.type);
    }
    
    public static String getKey(String nameFrom, String nameTo, TpaType typ){
        return nameFrom + nameTo + typ.name();
    }

    public TpaType getType() {
        return this.type;
    }
    
    public static enum TpaType {
        TPAHERE, TPA
    };
    
    private final TpaType type;
    private final IPlayer from;
    private final IPlayer to;
    private final long timeOfRequest;
//    public static final String C_GOOD = "§a";
//    public static final String C_BAD = "§c";
    public static final String C_REQ = "§e";
//    public static final String YES = "/yes";
//    public static final String NO = "/no";
//    public static final String YES2 = "/tpaccept";
//    public static final String NO2 = "/tpadeny";
//    public static final String SILENTNO = "/silentno";
    public static final String ERROR_REQUEST_ALREADY_EXISTS = "§cYou have already sent a teleportation request of this type to that person. Please wait until they accept or deny your request.";


    /**
     * use this constructor when you wish to create a new TP request AND notify the senders and receivers of the TpaType.
     * Assumes both players are valid. *
     * @param from
     * @param to
     * @param type
     * @param doNotify If true, players will be notified of the incoming requests.
     */
    public TPRequest(IPlayer from, IPlayer to, XLocation loc, TpaType type) {
        this.from = from;
        this.to = to;
        this.type = type;
        this.timeOfRequest = System.currentTimeMillis();
        this.loc = loc;
    }


    //<editor-fold defaultstate="collapsed" desc="notify">
//
//    /**
//     * participant *
//     */
//    public boolean finish(String result) {
//        boolean success = false;
//        Player pTo = STATIC.getPlayer(to);
//        Player pFrom = STATIC.getPlayer(from);
//        if (pTo == null && pFrom != null) {
//            pFrom.sendMessage(C_BAD + "Cannot finish the teleportation action with " + to + " because " + to + " is unavailable.");
//            return false;
//        }
//        if (pFrom == null && pTo != null) {
//            pTo.sendMessage(C_BAD + "Cannot finish the teleportation action with " + from + " because " + from + " is unavailable.");
//            return false;
//        }
//        if (pFrom == null && pTo == null) {
//            System.out.println("what the heck just happened?");
//        }
//        if (type == TpaType.TPA) {
//            if (result.equalsIgnoreCase(NO) || result.equalsIgnoreCase(NO2)) {
//                pFrom.sendMessage(C_BAD + to + " has denied your teleport request.");
//                pTo.sendMessage(C_BAD + "You have denied " + from + "'s teleport request.");
//                success = true;
//            } else if (result.equalsIgnoreCase(YES) || result.equalsIgnoreCase(YES2)) {
//                pFrom.sendMessage(C_GOOD + to + " has accepted your teleport request.");
//                pTo.sendMessage(C_GOOD + "You have accepted " + from + "'s teleport request.");
//                success = pFrom.teleport(pTo.getLocation());
//            } else if (result.equalsIgnoreCase(SILENTNO)) {
//                pTo.sendMessage(C_BAD + "You have silently denied " + from + "'s teleport request.");
//                success = true;
//            } else {
//                pFrom.sendMessage("something weired happened.");
//                pTo.sendMessage("something weired happened.");
//            }
//        } else if (type == TpaType.TPAHERE) {
//            if (result.equalsIgnoreCase(NO) || result.equalsIgnoreCase(NO2)) {
//                pFrom.sendMessage(C_BAD + to + " has denied your teleport request.");
//                pTo.sendMessage(C_BAD + "You have denied " + from + "'s teleport request.");
//                success = true;
//            } else if (result.equalsIgnoreCase(YES) || result.equalsIgnoreCase(YES2)) {
//                pFrom.sendMessage(C_GOOD + to + " has accepted your teleport request.");
//                pTo.sendMessage(C_GOOD + "You have accepted " + from + "'s teleport request.");
//                success = pTo.teleport(pFrom.getLocation());
//            } else if (result.equalsIgnoreCase(SILENTNO)) {
//                pTo.sendMessage(C_BAD + "You have silently denied " + from + "'s teleport request.");
//                success = true;
//            } else {
//                pFrom.sendMessage("something weired happened.");
//                pTo.sendMessage("something weired happened.");
//            }
//        }
//        return success;
//    }
	//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="access">

    /**
     * is this request being sent to the person specified by this method? Returns true if the answer is yes.*
     * @param name
     * @return 
     */
    public boolean isTo(String name) {
        return this.to.equals(name);
    }

    /**
     * is this request being sent from the person specified by this method? Returns true if the answer is yes.*
     */
    public boolean isFrom(String name) {
        return this.from.getName().equals(name);
    }
    public boolean isFrom(UUID name) {
        return this.from.getUniqueId().equals(name);
    }

    public XLocation getLoc() {
        return loc;
    }

    public IPlayer getTo() {
        return to;
    }

    @Override
    /**
     * compares from, to, and type. *
     */
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof TPRequest) {
            TPRequest tpr = (TPRequest) obj;
            boolean a = (this.type == tpr.type);
            boolean b = (this.from.getUniqueId().equals(tpr.from.getUniqueId()));
            boolean c = (this.to.getUniqueId().equals(tpr.to.getUniqueId()));
            return (a && b && c);
        } else {
            return false;
        }
    }

    public long getTime() {
        return timeOfRequest;
    }

    public IPlayer getFrom() {
        return this.from;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 41 * hash + (this.from != null ? this.from.getUniqueId().hashCode() : 0);
        hash = 41 * hash + (this.to != null ? this.to.getUniqueId().hashCode() : 0);
        return hash;
    }
	//</editor-fold>
}
