/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.models.CommandSyntax;
import com.lumengaming.skillsaw.utility.C;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.util.ArrayList;
import java.util.Arrays;
import net.md_5.bungee.api.chat.BaseComponent;

/**
 *
 * @author prota
 */
public class HelpModule {

    public static void printHelp(IPlayer cs, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
            args = Arrays.copyOfRange(args, 1, args.length - 1);
        }
        
        if (args.length == 0) {
            args = new String[] {"?"};
        }

        String category = args[0];
        switch (category.toLowerCase()) {
            case "tp":
            case "tele":
            case "teleport":
            case "tpa":
            case "teleportation":
                category = "TP";
                break;
            case "skills":
            case "rep":
            case "reputation":
            case "skill":
                category = "SKILLS";
                break;
            case "chat":
                category = "CHAT";
                break;
            case "other":
            case "alt":
            case "misc":
                category = "MISC";
                break;
            case "?":
            default:
                category = "?";
                break;
        }

        cs.sendMessage(C.C_DIV_LINE);
        cs.sendMessage(C.C_DIV_TITLE_PREFIX + "Help Page (" + category + ")");
        cs.sendMessage(C.C_DIV_LINE);
        if (category.equals("?")){
            sendSyntax(cs, null, "/ss help tp", "Show teleportation help");
            sendSyntax(cs, null, "/ss help rep", "Show reputation help");
            sendSyntax(cs, null, "/ss help skills", "Show skills help");
            sendSyntax(cs, null, "/ss help chat", "Show chat help");
            sendSyntax(cs, null, "/ss help misc", "Show uncategorized help");
        }
        else if (category.equals("TP")) {
            sendSyntax(cs, Permissions.TELEPORT_SELF, "/tp <target>", "Teleport to the player.");
            sendSyntax(cs, Permissions.TELEPORT_OTHERS, "/tp <source> <target>", "Teleport to the player.");
            sendSyntax(cs, null, "/tpaccept", "Accept a teleport");
            sendSyntax(cs, null, "/tpdeny", "Deny a teleport");
            sendSyntax(cs, Permissions.TELEPORT_OTHERS, "/tphere <target>", "Teleport player to you.");
            sendSyntax(cs, Permissions.TELEPORT_OTHERS, "/tphere *", "Teleport all players to you.");
            sendSyntax(cs, Permissions.TPA_LOCK, "/tpalock yes/no/ask", "Automatically accept or deny\nteleport requests with this\ncommand.");
            sendSyntax(cs, Permissions.TPA_TO, "/tpa <target>", "Request to teleport\nto the specified\nplayer.");
            sendSyntax(cs, Permissions.TPA_HERE, "/tpahere <target>", "Request another to teleport to you.");
            sendSyntax(cs, Permissions.TPA_HERE, "/tpahere *", "Request all players teleport to you.");
        }else if (category.equals("CHAT")){
            sendSyntax(cs, null, "/ch <channel>", "Change your speaking \nchannel.");
            cs.sendMessage(("§c/ch <channel>"));
            sendSyntax(cs, null, "/ch:<channel> <message>", "Chat on the specified \nchannel for just that \none message");
            sendSyntax(cs, Permissions.CHANNEL_STICKIES, "/ch + <channel>", "Adds the channel to \nyour list of sticky \nchannels. A sticky channel \nmeans you listen to that \nchannel even when not \nspeaking on it.");
            sendSyntax(cs, Permissions.CHANNEL_STICKIES, "/ch - <channel>", "Removes the channel from \nyour list of sticky \nchannels. A sticky channel \nmeans you listen to that \nchannel even when not \nspeaking on it.");
            sendSyntax(cs, Permissions.CHANNEL_STICKIES_INFINITE, "/ch *", "Listen to all channels \neven if you are not \nspeaking on them.");
            sendSyntax(cs, Permissions.CHANNEL_STICKIES, "/ch !*", "Stop listening to \nall sticky channels.");
            sendSyntax(cs, Permissions.CHANNEL_INFO, "/ch info [channel]", "Get info about a channel.");
            sendSyntax(cs, Permissions.CHANNEL_INFO, "/ch p [player]", "Get channel info \nabout a player.");
            sendSyntax(cs, Permissions.CHANNEL_LIST, "/ch list", "List all channels \nyou're able to see.");
        }else if (category.equals("SKILLS")){
            
        }else if (category.equals("REP")){
            
        }else if (category.equals("MISC")){
            sendSyntax(cs, Permissions.PVPMODE, "/pvpm", "Toggle pvp mode.");
        }

        cs.sendMessage(C.C_DIV_LINE);
        ArrayList<CommandSyntax> commands = getSyntax(cs);
    }

    private static ArrayList<CommandSyntax> getSyntax(IPlayer cs) {
        return new ArrayList<CommandSyntax>();
    }

    private static void sendSyntax(IPlayer cs, Permissions node, String syntax, String desc) {
        if (cs == null) {
            return;
        }
        boolean hasPermission = (node != null && Permissions.USER_HAS_PERMISSION(cs, node, false));
        BaseComponent[] txt;
        if (hasPermission) {
            txt = CText.hoverTextSuggest(C.C_MENU_CONTENT + syntax, desc, syntax);
        } else {
            txt = CText.hoverTextSuggest(C.C_MENU_CONTENT + "§c" + syntax, desc, "You need the '" + node.node + "' permission node.");
        }
        cs.sendMessage(txt);
    }
}
